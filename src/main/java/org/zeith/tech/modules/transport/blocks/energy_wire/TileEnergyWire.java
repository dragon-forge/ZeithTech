package org.zeith.tech.modules.transport.blocks.energy_wire;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.enums.SideConfig;
import org.zeith.tech.api.tile.IHasPriority;
import org.zeith.tech.api.tile.energy.EnergyMeasurableWrapper;
import org.zeith.tech.api.tile.energy.IEnergyMeasurable;
import org.zeith.tech.api.tile.sided.SideConfig6;
import org.zeith.tech.modules.transport.blocks.base.traversable.EndpointData;
import org.zeith.tech.modules.transport.blocks.base.traversable.ITraversable;
import org.zeith.tech.modules.transport.init.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TileEnergyWire
		extends TileSyncableTickable
		implements ITraversable<FECharge>
{
	@NBTSerializable("EnergyPassed")
	public float energyPassed;
	
	@NBTSerializable("Contents")
	public final EnergyWireContents contents = new EnergyWireContents();
	
	@NBTSerializable("Sides")
	public final SideConfig6 sideConfigs = new SideConfig6(SideConfig.NONE);
	
	public final EnergyMeasurableWrapper measurables = new EnergyMeasurableWrapper(this::getPosition, this::getLoad);
	
	public TileEnergyWire(BlockPos pos, BlockState state)
	{
		this(TilesZT_Transport.ENERGY_WIRE, pos, state);
	}
	
	public TileEnergyWire(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	public BlockEnergyWire getWireBlock()
	{
		var state = getBlockState();
		if(state.getBlock() instanceof BlockEnergyWire wire)
			return wire;
		return BlocksZT_Transport.UNINSULATED_COPPER_WIRE;
	}
	
	public EnergyWireProperties getWireProps()
	{
		return getWireBlock().properties;
	}
	
	public float getLoad()
	{
		return lastLoad;
	}
	
	private float lastLoad;
	
	@Override
	public void update()
	{
		measurables.update();
		
		if(energyPassed > 0)
		{
			measurables.onEnergyTransfer(energyPassed);
			
			if(level != null && isOnServer())
			{
				var props = getWireProps();
				
				float maxFE = props.tier().maxTransfer();
				float loadFactor = energyPassed / maxFE;
				
				this.lastLoad = loadFactor;
				
				if(loadFactor > 1F && props.burns())
				{
					// burn down the cable
					level.levelEvent(1502, worldPosition, 0);
					level.removeBlock(worldPosition, false);
				}
				
				if(!props.insulated())
				{
					float damage = Mth.sqrt((float) (loadFactor * Math.cbrt(maxFE)));
					float radius = Math.max(0.25F, Math.min(5, energyPassed / maxFE));
					
					var peaceful = level.getDifficulty() == Difficulty.PEACEFUL;
					var dmgSrc = peaceful ? DamageSourcesZT_Transport.ELECTROCUTION_PEACEFUL : DamageSourcesZT_Transport.ELECTROCUTION;
					if(peaceful) damage /= 2;
					
					for(LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(worldPosition).inflate(radius)))
						entity.hurt(dmgSrc, damage);
				}
			}
		} else this.lastLoad = 0;
		
		// Count in the energy that has been extracted from the wire into the machine, but DO NOT count the energy that has been passed through the wire itself.
		measurables.onEnergyTransfer(Math.max(0, contents.emit(this) - energyPassed));
		energyPassed = 0;
	}
	
	public boolean doesConnectTo(Direction to)
	{
		return sideConfigs.get(to.ordinal()) != SideConfig.DISABLE &&
				(getRelativeTraversable(to, null).isPresent() || relativeEnergyHandler(to).isPresent());
	}
	
	private final net.minecraftforge.common.util.LazyOptional<?>[] sidedEnergyHandlers =
			Direction.stream()
					.map(dir -> LazyOptional.of(() -> new WireEnergyHandler(dir, this)))
					.toArray(LazyOptional[]::new);
	
	private final LazyOptional<IEnergyMeasurable> energyMeasurable = LazyOptional.of(() -> measurables);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(side != null && cap == ForgeCapabilities.ENERGY && sideConfigs.get(side.ordinal()) != SideConfig.DISABLE)
			return sidedEnergyHandlers[side.ordinal()].cast();
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE)
			return energyMeasurable.cast();
		return super.getCapability(cap, side);
	}
	
	public int getPriority(Direction dir)
	{
		return Cast.optionally(level.getBlockEntity(worldPosition.relative(dir)), IHasPriority.class)
				.map(p -> p.getPriorityForFace(dir.getOpposite(), ForgeCapabilities.ENERGY))
				.orElse(0);
	}
	
	public LazyOptional<IEnergyStorage> relativeEnergyHandler(Direction to)
	{
		var be = level.getBlockEntity(worldPosition.relative(to));
		return be == null || be instanceof TileEnergyWire
				? LazyOptional.empty() // Either there is no block entity, or the block entity is a pipe
				: be.getCapability(ForgeCapabilities.ENERGY, to.getOpposite());
	}
	
	private boolean connectsTo(Direction to, TileEnergyWire wire)
	{
		return sideConfigs.get(to.ordinal()) != SideConfig.DISABLE
				&& wire.sideConfigs.get(to.getOpposite().ordinal()) != SideConfig.DISABLE;
	}
	
	public void emitTo(Direction to, float fe)
	{
		contents.add(to, fe);
	}
	
	public int emitToDirect(Direction to, int fe, boolean simulate)
	{
		return relativeEnergyHandler(to)
				.map(storage -> storage.receiveEnergy(fe, simulate))
				.orElse(0);
	}
	
	@Override
	public Optional<? extends ITraversable<FECharge>> getRelativeTraversable(Direction side, FECharge ignored)
	{
		return Cast.optionally(level.getBlockEntity(worldPosition.relative(side)), TileEnergyWire.class)
				.filter(pipe -> connectsTo(side, pipe));
	}
	
	@Override
	public List<EndpointData> getEndpoints(FECharge contents)
	{
		return Stream.of(BlockEnergyWire.DIRECTIONS)
				.filter(dir ->
				{
					int canEmit = emitToDirect(dir, contents.FE, true);
					return canEmit > 0 && canEmit > this.contents.energy[dir.ordinal()];
				})
				.map(dir -> new EndpointData(worldPosition, dir, getPriority(dir), true))
				.toList();
	}
	
	@Override
	public BlockPos getPosition()
	{
		return worldPosition;
	}
}