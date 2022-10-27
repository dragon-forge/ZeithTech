package org.zeith.tech.modules.processing.blocks.electric_furnace.basic;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
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
import org.zeith.hammerlib.net.properties.*;
import org.zeith.hammerlib.util.java.Cast;
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
import org.zeith.tech.utils.SidedInventory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class TileElectricFurnaceB
		extends TileBaseMachine<TileElectricFurnaceB>
		implements ITileSlotProvider
{
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.ITEM))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.NONE)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.NONE);
	
	{
		var ecfg = sidedConfig.getSideConfigs(SidedConfigTyped.ENERGY);
		ecfg.setRelative(RelativeDirection.BACK, SideConfig.PULL);
		ecfg.setRelative(RelativeDirection.FRONT, SideConfig.DISABLE);
		ecfg.setRelative(RelativeDirection.UP, SideConfig.DISABLE);
		ecfg.setRelative(RelativeDirection.DOWN, SideConfig.DISABLE);
		
		var icfg = sidedConfig.getSideConfigs(SidedConfigTyped.ITEM);
		icfg.setRelative(RelativeDirection.BACK, SideConfig.DISABLE);
		icfg.setRelative(RelativeDirection.UP, SideConfig.PULL);
		icfg.setRelative(RelativeDirection.DOWN, SideConfig.PUSH);
	}
	
	@NBTSerializable("Items")
	public final SidedInventory inventory = new SidedInventory(2, sidedConfig.createItemAccess(new int[] { 0 }, new int[] { 1 }));
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.EXTRA_LOW_VOLTAGE, EnumEnergyManagerKind.CONSUMER);
	
	@NBTSerializable("Progress")
	public int _progress;
	
	@NBTSerializable("MaxProgress")
	public int _maxProgress = 200;
	
	@NBTSerializable("Recipe")
	public ResourceLocation _activeRecipe;
	
	public final PropertyInt energyStored = new PropertyInt(DirectStorage.create(energy.fe::setEnergyStored, energy.fe::getEnergyStored));
	
	public final PropertyInt progress = new PropertyInt(DirectStorage.create(i -> _progress = i, () -> _progress));
	public final PropertyInt maxProgress = new PropertyInt(DirectStorage.create(i -> _maxProgress = i, () -> _maxProgress));
	public final PropertyResourceLocation activeRecipe = new PropertyResourceLocation(DirectStorage.create(i -> _activeRecipe = i, () -> _activeRecipe));
	
	public final PropertyItemStack inputItemDisplay = new PropertyItemStack(DirectStorage.allocate(ItemStack.EMPTY));
	
	public TileElectricFurnaceB(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.BASIC_ELECTRIC_FURNACE, pos, state);
		
		dispatcher.registerProperty("ar", activeRecipe);
		dispatcher.registerProperty("display", inputItemDisplay);
		
		inventory.isStackValid = (slot, stack) ->
		{
			if(level != null && slot == 0)
			{
				var rm = level.getRecipeManager();
				return rm.getAllRecipesFor(RecipeType.SMELTING)
						.stream()
						.anyMatch(sr -> sr.getIngredients().get(0).test(stack));
			}
			return false;
		};
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		
		if(isOnServer() && atTickRate(5))
		{
			ItemStack first = inventory.getItem(0).copy();
			if(first.isEmpty()) first = inventory.getItem(1).copy();
			inputItemDisplay.set(first);
		}
		
		if(isOnServer())
		{
			var recipe = getActiveRecipe();
			if(recipe != null)
			{
				maxProgress.setInt(recipe.getCookingTime());
				
				if(_progress < _maxProgress)
				{
					if(redstone.shouldWork(this) && energy.consumeEnergy(20))
					{
						_progress += 1;
						isInterrupted.setBool(false);
					} else
						isInterrupted.setBool(true);
				}
				
				var enable = _progress > 0;
				if(isEnabled() != enable)
					setEnabledState(enable);
				
				if(_progress >= _maxProgress && storeAll(recipe.assemble(inventory)))
				{
					inventory.getItem(0).shrink(1);
					_progress = 0;
					sync();
				}
			} else if(_progress > 0)
				_progress = Math.max(0, _progress - 2);
			else if(isEnabled())
				setEnabledState(false);
		}
		
		if(isOnClient() && isEnabled() && !isInterrupted())
			ZeithTechAPI.get()
					.getAudioSystem()
					.playMachineSoundLoop(this, SoundsZT_Processing.BASIC_ELECTRIC_FURNACE, SoundsZT_Processing.BASIC_MACHINE_INTERRUPT);
	}
	
	public boolean storeAll(ItemStack stack)
	{
		var os = inventory.getItem(1);
		if(os.isEmpty())
		{
			inventory.setItem(1, stack);
			return true;
		}
		if(os.sameItem(stack) && os.getCount() + stack.getCount() <= Math.min(os.getMaxStackSize(), stack.getMaxStackSize()))
		{
			os.grow(stack.getCount());
			return true;
		}
		return false;
	}
	
	public SmeltingRecipe getActiveRecipe()
	{
		var rm = level.getRecipeManager();
		
		if(inventory.getItem(0).isEmpty())
		{
			activeRecipe.set(null);
			return null;
		}
		
		if(_activeRecipe != null)
		{
			var rec = rm.byKey(_activeRecipe)
					.flatMap(r -> Cast.optionally(r, SmeltingRecipe.class))
					.orElse(null);
			if(rec != null && rec.getIngredients().get(0).test(inventory.getItem(0)))
				return rec;
			_activeRecipe = null;
		}
		
		var rec = rm.getRecipeFor(RecipeType.SMELTING, inventory, level).orElse(null);
		if(rec != null) activeRecipe.set(rec.getId());
		return rec;
	}
	
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<? extends IItemHandler>[] itemHandlers = SidedInvWrapper.create(inventory, Direction.values());
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	private final LazyOptional<RedstoneControl> redstoneCap = LazyOptional.of(() -> redstone);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY && (side == null || sidedConfig.canAccess(SidedConfigTyped.ENERGY, side))) return energyCap.cast();
		if(cap == ZeithTechCapabilities.REDSTONE_CONTROL) return redstoneCap.cast();
		if(cap == ZeithTechCapabilities.SIDED_CONFIG) return sidedConfigCap.cast();
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE) return energy.measurableCap.cast();
		if(cap == ForgeCapabilities.ITEM_HANDLER && (side == null || sidedConfig.canAccess(SidedConfigTyped.ITEM, side))) return itemHandlers[side == null ? 0 : side.ordinal()].cast();
		return super.getCapability(cap, side);
	}
	
	@Override
	public ContainerBaseMachine<TileElectricFurnaceB> openContainer(Player player, int windowId)
	{
		return new ContainerElectricFurnaceB(this, player, windowId);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(inventory, energy.batteryInventory);
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