package org.zeith.tech.modules.processing.blocks.fuelgen.basic;

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
import org.zeith.tech.api.capabilities.ZeithTechCapabilities;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;

import java.util.EnumSet;
import java.util.List;

public class TileFuelGeneratorB
		extends TileBaseMachine<TileFuelGeneratorB>
{
	@NBTSerializable("FuelInv")
	public final SimpleInventory fuelInventory = new SimpleInventory(1);
	
	@NBTSerializable("ChargeInv")
	public final SimpleInventory chargeInventory = new SimpleInventory(1);
	
	@NBTSerializable("FuelTotal")
	private int _fuelTicksTotal;
	
	@NBTSerializable("FuelLeft")
	private float _fuelTicksLeft;
	
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.NONE);
	
	{
		var ecfg = sidedConfig.getSideConfigs(SidedConfigTyped.ENERGY);
		ecfg.setRelative(RelativeDirection.BACK, SideConfig.PUSH);
		ecfg.setRelative(RelativeDirection.FRONT, SideConfig.DISABLE);
		ecfg.setRelative(RelativeDirection.UP, SideConfig.DISABLE);
	}
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(20000, 0, 200);
	
	public final PropertyInt fuelTicksTotal = new PropertyInt(DirectStorage.create(i -> _fuelTicksTotal = i, () -> _fuelTicksTotal));
	public final PropertyInt energyStored = new PropertyInt(DirectStorage.create(energy.fe::setEnergyStored, energy.fe::getEnergyStored));
	public final PropertyFloat fuelTicksLeft = new PropertyFloat(DirectStorage.create(i -> _fuelTicksLeft = i, () -> _fuelTicksLeft));
	
	public int currentGenPerTick = 20;
	
	public TileFuelGeneratorB(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.FUEL_GENERATOR_BASIC, pos, state);
		
		fuelInventory.isStackValid = (slot, stack) -> ForgeHooks.getBurnTime(stack, null) > 0;
	}
	
	@Override
	public ContainerBaseMachine<TileFuelGeneratorB> openContainer(Player player, int windowId)
	{
		return new ContainerFuelGeneratorB(this, player, windowId);
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		
		if(isOnServer())
		{
			if(energy.fe.getEnergyTillFull() > 0 && _fuelTicksLeft <= 0)
				consumeFuel();
			
			if(_fuelTicksLeft > 0)
			{
				float take = Math.min(1, _fuelTicksLeft);
				if(energy.storeNonZeroEnergy(Math.round(currentGenPerTick * take)))
					_fuelTicksLeft -= take;
				else
					_fuelTicksLeft -= 0.05F;
				
				if(_fuelTicksLeft <= 0)
				{
					if(energy.fe.getEnergyTillFull() > 0)
						consumeFuel();
					if(_fuelTicksLeft <= 0)
						setEnabledState(false);
				}
			}
			
			energy.charge(chargeInventory.getItem(0));
		}
		
		if(isOnClient() && isEnabled())
		{
			Vec3 pos = Vec3.atBottomCenterOf(worldPosition);
			if(getRNG().nextInt(5) == 0)
				level.addParticle(ParticleTypes.SMOKE,
						pos.x + getRNG().nextFloat(-1 / 16F, 1 / 16F),
						pos.y + 1F,
						pos.z + getRNG().nextFloat(-1 / 16F, 1 / 16F),
						0, 0.01F, 0);
		}
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
		return List.of(fuelInventory, chargeInventory);
	}
	
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY && (side == null || sidedConfig.canAccess(SidedConfigTyped.ENERGY, side)))
			return energyCap.cast();
		if(cap == ZeithTechCapabilities.SIDED_CONFIG)
			return sidedConfigCap.cast();
		return super.getCapability(cap, side);
	}
}