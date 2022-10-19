package org.zeith.tech.modules.generators.blocks.fuel_generator.solid.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyFloat;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.modules.generators.init.TilesZT_Generators;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.SoundsZT_Processing;

import java.util.EnumSet;
import java.util.List;

public class TileSolidFuelGeneratorB
		extends TileBaseMachine<TileSolidFuelGeneratorB>
{
	@NBTSerializable("Items")
	public final SimpleInventory fuelInventory = new SimpleInventory(1);
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	@NBTSerializable("FuelTotal")
	private int _fuelTicksTotal;
	
	@NBTSerializable("FuelLeft")
	private float _fuelTicksLeft;
	
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.NONE)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.BACK, SideConfig.PUSH)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.FRONT, SideConfig.DISABLE)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.UP, SideConfig.DISABLE);
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(20000, 0, 64)
			.setKind(EnumEnergyManagerKind.GENERATOR);
	
	public final PropertyInt fuelTicksTotal = new PropertyInt(DirectStorage.create(i -> _fuelTicksTotal = i, () -> _fuelTicksTotal));
	public final PropertyInt energyStored = new PropertyInt(DirectStorage.create(energy.fe::setEnergyStored, energy.fe::getEnergyStored));
	public final PropertyFloat fuelTicksLeft = new PropertyFloat(DirectStorage.create(i -> _fuelTicksLeft = i, () -> _fuelTicksLeft));
	
	public int currentGenPerTick = 20;
	
	public TileSolidFuelGeneratorB(BlockPos pos, BlockState state)
	{
		super(TilesZT_Generators.BASIC_SOLID_FUEL_GENERATOR, pos, state);
		
		fuelInventory.isStackValid = (slot, stack) ->
				!stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent() && ForgeHooks.getBurnTime(stack, null) > 0;
	}
	
	@Override
	public ContainerBaseMachine<TileSolidFuelGeneratorB> openContainer(Player player, int windowId)
	{
		return new ContainerSolidFuelGeneratorB(this, player, windowId);
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		
		if(isOnServer())
		{
			var works = redstone.shouldWork(this);
			
			if(energy.fe.getEnergyTillFull() > 0 && _fuelTicksLeft <= 0 && works)
				consumeFuel();
			
			if(_fuelTicksLeft > 0)
			{
				if(!isEnabled())
					setEnabledState(true);
				
				float take = Math.min(1, _fuelTicksLeft);
				if(works && energy.generateAnyEnergy(Math.round(currentGenPerTick * take)))
					_fuelTicksLeft -= take;
				else
					_fuelTicksLeft -= 0.05F;
				
				if(_fuelTicksLeft <= 0)
				{
					if(energy.fe.getEnergyTillFull() > 0 && works)
						consumeFuel();
					if(_fuelTicksLeft <= 0)
						setEnabledState(false);
				}
			}
		}
		
		if(isOnClient() && isEnabled() && !isInterrupted())
		{
			ZeithTechAPI.get()
					.getAudioSystem()
					.playMachineSoundLoop(this, SoundsZT_Processing.BASIC_FUEL_GENERATOR, SoundsZT_Processing.BASIC_MACHINE_INTERRUPT);
			
			Vec3 pos = Vec3.atBottomCenterOf(worldPosition);
			if(getRNG().nextInt(5) == 0)
				level.addParticle(ParticleTypes.SMOKE,
						pos.x + getRNG().nextFloat(-1 / 16F, 1 / 16F),
						pos.y + 1F,
						pos.z + getRNG().nextFloat(-1 / 16F, 1 / 16F),
						0, 0.01F, 0);
		}
	}
	
	@Override
	public boolean isInterrupted()
	{
		return false;
	}
	
	public void consumeFuel()
	{
		var fuelItem = fuelInventory.getItem(0);
		var duration = ForgeHooks.getBurnTime(fuelItem, null);
		
		if(fuelItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent())
			return;
		
		if(duration > 0)
		{
			_fuelTicksLeft = _fuelTicksTotal = duration;
			setEnabledState(true);
			
			if(fuelItem.hasCraftingRemainingItem())
			{
				var remainder = fuelItem.getCraftingRemainingItem();
				fuelItem.shrink(1);
				if(fuelItem.isEmpty())
					fuelInventory.setItem(0, remainder.copy());
			} else
			{
				fuelItem.shrink(1);
			}
		}
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(fuelInventory, energy.batteryInventory);
	}
	
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	private final LazyOptional<RedstoneControl> redstoneCap = LazyOptional.of(() -> redstone);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY && (side == null || sidedConfig.canAccess(SidedConfigTyped.ENERGY, side))) return energyCap.cast();
		if(cap == ZeithTechCapabilities.REDSTONE_CONTROL) return redstoneCap.cast();
		if(cap == ZeithTechCapabilities.SIDED_CONFIG) return sidedConfigCap.cast();
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE) return energy.measurableCap.cast();
		return super.getCapability(cap, side);
	}
}