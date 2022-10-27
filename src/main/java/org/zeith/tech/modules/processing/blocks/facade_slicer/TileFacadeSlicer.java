package org.zeith.tech.modules.processing.blocks.facade_slicer;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.tech.api.ZeithTechAPI;
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
import org.zeith.tech.modules.processing.init.SoundsZT_Processing;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;
import org.zeith.tech.modules.shared.init.ItemsZT;
import org.zeith.tech.utils.InventoryHelper;
import org.zeith.tech.utils.SidedInventory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class TileFacadeSlicer
		extends TileBaseMachine<TileFacadeSlicer>
		implements ITileSlotProvider
{
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.ITEM))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.NONE)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.NONE)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.DOWN, SideConfig.PULL)
			.setForAll(RelativeDirection.UP, SideConfig.DISABLE);
	
	@NBTSerializable("Items")
	public final SidedInventory inventory = new SidedInventory(2, sidedConfig.createItemAccess(new int[] { 0 }, new int[] { 1 }));
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.EXTRA_LOW_VOLTAGE, EnumEnergyManagerKind.CONSUMER);
	
	@NBTSerializable("Progress")
	public int _progress;
	
	@NBTSerializable("MaxProgress")
	public int _maxProgress = 100;
	
	@NBTSerializable("Consumption")
	public int usePerTick = 20;
	
	public final PropertyInt energyStored = new PropertyInt(DirectStorage.create(energy.fe::setEnergyStored, energy.fe::getEnergyStored));
	public final PropertyInt progress = new PropertyInt(DirectStorage.create(i -> _progress = i, () -> _progress));
	
	public TileFacadeSlicer(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.FACADE_SLICER, pos, state);
		inventory.isStackValid = (slot, stack) -> slot == 0 && !ItemsZT.FACADE.forItem(stack, 6, false).isEmpty();
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		
		if(isOnServer())
		{
			var result = ItemsZT.FACADE.forItem(inventory.getItem(0), 6, false);
			if(!result.isEmpty())
			{
				if(_progress < _maxProgress && isOnServer()
						&& (_progress > 0 || storeRecipeResult(inventory.getItem(0), true)))
				{
					if(redstone.shouldWork(this) && energy.consumeEnergy(usePerTick))
					{
						_progress += 1;
						isInterrupted.setBool(false);
					} else
					{
						isInterrupted.setBool(true);
					}
				}
				
				var enable = _progress > 0;
				if(isEnabled() != enable)
					setEnabledState(enable);
				
				if(isOnServer() && _progress >= _maxProgress && storeRecipeResult(inventory.getItem(0), false))
				{
					inventory.getItem(0).shrink(1);
					_progress = 0;
					sync();
				}
			} else
			{
				if(_progress > 0)
					_progress = Math.max(0, _progress - 2);
				else if(isEnabled())
					setEnabledState(false);
			}
		}
		
		if(isOnClient() && isEnabled() && !isInterrupted())
			ZeithTechAPI.get()
					.getAudioSystem()
					.playMachineSoundLoop(this, SoundsZT_Processing.FACADE_SLICER, SoundsZT_Processing.BASIC_MACHINE_INTERRUPT);
	}
	
	protected boolean storeRecipeResult(ItemStack input, boolean simulate)
	{
		return store(ItemsZT.FACADE.forItem(input, 6, false), simulate);
	}
	
	public boolean store(ItemStack stacks, boolean simulate)
	{
		return InventoryHelper.storeStack(inventory, IntStream.range(1, 2), stacks, simulate);
	}
	
	@Override
	public ContainerBaseMachine<TileFacadeSlicer> openContainer(Player player, int windowId)
	{
		return new ContainerFacadeSlicer(this, player, windowId);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(energy.batteryInventory, inventory);
	}
	
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<? extends IItemHandler>[] itemHandlers = SidedInvWrapper.create(inventory, Direction.values());
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	private final LazyOptional<RedstoneControl> redstoneCap = LazyOptional.of(() -> redstone);
	
	@Override
	public @NotNull <CAP> LazyOptional<CAP> getCapability(@NotNull Capability<CAP> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY && (side == null || sidedConfig.canAccess(SidedConfigTyped.ENERGY, side))) return energyCap.cast();
		if(cap == ZeithTechCapabilities.REDSTONE_CONTROL) return redstoneCap.cast();
		if(cap == ZeithTechCapabilities.SIDED_CONFIG) return sidedConfigCap.cast();
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE) return energy.measurableCap.cast();
		if(cap == ForgeCapabilities.ITEM_HANDLER && (side == null || sidedConfig.canAccess(SidedConfigTyped.ITEM, side))) return itemHandlers[side == null ? 0 : side.ordinal()].cast();
		return super.getCapability(cap, side);
	}
	
	public final List<ISlot<?>> slots = ((Supplier<List<ISlot<?>>>) () ->
	{
		ImmutableList.Builder<ISlot<?>> lst = new ImmutableList.Builder<>();
		
		lst.add(energy.createSlot());
		
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