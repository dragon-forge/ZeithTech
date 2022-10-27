package org.zeith.tech.modules.transport.blocks.energy_cell.basic;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.tile.slots.*;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;
import org.zeith.tech.utils.SidedInventory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class TileEnergyCellB
		extends TileBaseMachine<TileEnergyCellB>
		implements ITileSlotProvider
{
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.ITEM))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.PULL)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.NONE)
			.setFor(SidedConfigTyped.ITEM, RelativeDirection.FRONT, SideConfig.DISABLE)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.FRONT, SideConfig.PUSH);
	
	@NBTSerializable("Items")
	public final SidedInventory inventory = new SidedInventory(2, sidedConfig.createItemAccess(new int[] {
			0,
			1
	}, new int[] {
			0,
			1
	}));
	
	@NBTSerializable("FE")
	public final EnergyManager energy = createEnergyStorage();
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	public boolean isWorkingNow;
	
	public TileEnergyCellB(BlockPos pos, BlockState state)
	{
		super(TilesZT_Transport.BASIC_ENERGY_CELL, pos, state);
		
		inventory.isStackValid = (slot, stack) -> level != null && (slot == 0 &&
				stack.getCapability(ForgeCapabilities.ENERGY)
						.filter(IEnergyStorage::canExtract)
						.map(e -> e.getEnergyStored() > 0 && e.extractEnergy(Math.max(energy.fe.getEnergyTillFull(), 1), true) > 0)
						.orElse(false)) ||
				(slot == 1 &&
						stack.getCapability(ForgeCapabilities.ENERGY)
								.filter(IEnergyStorage::canReceive)
								.map(e -> e.getEnergyStored() < e.getMaxEnergyStored() && e.receiveEnergy(Math.max(energy.fe.getEnergyStored(), 1), true) > 0)
								.orElse(false));
	}
	
	protected EnergyManager createEnergyStorage()
	{
		return newEnergyCellStorage(EnergyTier.EXTRA_LOW_VOLTAGE);
	}
	
	public static EnergyManager newEnergyCellStorage(EnergyTier tier)
	{
		return new EnergyManager(tier.capacity() * 100, tier.maxTransfer() * 2, tier.maxTransfer() * 2).setKind(EnumEnergyManagerKind.ENERGY_CELL);
	}
	
	@SuppressWarnings("AssignmentUsedAsCondition")
	@Override
	public void update()
	{
		if(isWorkingNow = redstone.shouldWork(this))
		{
			energy.update(level, worldPosition, sidedConfig);
			energy.chargeMachineFromItem(inventory.getItem(0));
			energy.chargeItem(inventory.getItem(1));
		}
	}
	
	@Override
	public ContainerBaseMachine<TileEnergyCellB> openContainer(Player player, int windowId)
	{
		return new ContainerEnergyCellB(this, player, windowId);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(energy.batteryInventory);
	}
	
	private final LazyOptional<IEnergyStorage> energyReceiveCapability = LazyOptional.of(() -> new IEnergyStorage()
	{
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate)
		{
			if(!isWorkingNow) return 0;
			int r = energy.receiveEnergy(maxReceive, simulate);
			if(r > 0 && !simulate) setChanged();
			return r;
		}
		
		@Override
		public int extractEnergy(int maxExtract, boolean simulate)
		{
			return 0;
		}
		
		@Override
		public int getEnergyStored()
		{
			return energy.getEnergyStored();
		}
		
		@Override
		public int getMaxEnergyStored()
		{
			return energy.getMaxEnergyStored();
		}
		
		@Override
		public boolean canExtract()
		{
			return false;
		}
		
		@Override
		public boolean canReceive()
		{
			return true;
		}
	});
	
	private final LazyOptional<IEnergyStorage> energySendCapability = LazyOptional.of(() -> new IEnergyStorage()
	{
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate)
		{
			return 0;
		}
		
		@Override
		public int extractEnergy(int maxExtract, boolean simulate)
		{
			if(!isWorkingNow) return 0;
			int e = energy.extractEnergy(maxExtract, simulate);
			if(e > 0 && !simulate) setChanged();
			return e;
		}
		
		@Override
		public int getEnergyStored()
		{
			return energy.getEnergyStored();
		}
		
		@Override
		public int getMaxEnergyStored()
		{
			return energy.getMaxEnergyStored();
		}
		
		@Override
		public boolean canExtract()
		{
			return false;
		}
		
		@Override
		public boolean canReceive()
		{
			return true;
		}
	});
	
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	private final LazyOptional<RedstoneControl> redstoneCap = LazyOptional.of(() -> redstone);
	
	@Override
	public @NotNull <CAP> LazyOptional<CAP> getCapability(@NotNull Capability<CAP> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY)
		{
			if(side == null || sidedConfig.getAccess(SidedConfigTyped.ENERGY, side) != SideConfig.PUSH)
				return energyReceiveCapability.cast();
			return energySendCapability.cast();
		}
		if(cap == ZeithTechCapabilities.REDSTONE_CONTROL) return redstoneCap.cast();
		if(cap == ZeithTechCapabilities.SIDED_CONFIG) return sidedConfigCap.cast();
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE) return energy.measurableCap.cast();
		return super.getCapability(cap, side);
	}
	
	public final List<ISlot<?>> slots = ((Supplier<List<ISlot<?>>>) () ->
	{
		ImmutableList.Builder<ISlot<?>> lst = new ImmutableList.Builder<>();
		
		Direction.stream()
				.flatMap(dir ->
						IntStream.of(inventory.getSlotsForFace(dir))
								.mapToObj(slot -> inventory.sidedItemAccess.canTakeItemThroughFace(slot, dir)
										? Tuples.immutable(slot, inventory.sidedItemAccess.canPlaceItemThroughFace(slot, dir) ? SlotRole.BOTH : SlotRole.OUTPUT)
										: inventory.sidedItemAccess.canPlaceItemThroughFace(slot, dir)
										? Tuples.immutable(slot, SlotRole.INPUT)
										: null)
				)
				.filter(Objects::nonNull)
				.distinct()
				.map(pair -> ISlot.simpleSlot(new ContainerItemSlotAccess(inventory, pair.a(), pair.b()), pair.b(), pair.toString(), getClass().getSimpleName()))
				.forEach(lst::add);
		
		return lst.build();
	}).get();
	
	@Override
	public List<ISlot<?>> getSlots()
	{
		return slots;
	}
}