package org.zeith.tech.modules.processing.blocks.base.unary_machine.basic;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import org.zeith.hammerlib.net.properties.PropertyItemStack;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.misc.Tuple2;
import org.zeith.tech.api.recipes.base.IUnaryRecipe;
import org.zeith.tech.api.tile.ITieredTile;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.tile.slots.*;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.SoundsZT_Processing;
import org.zeith.tech.utils.InventoryHelper;
import org.zeith.tech.utils.SidedInventory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public abstract class TileUnaryRecipeMachineB<T extends TileUnaryRecipeMachineB<T, R>, R extends IUnaryRecipe>
		extends TileBaseMachine<T>
		implements ITieredTile, ITileSlotProvider
{
	protected final IUnaryRecipe.IUnaryRecipeProvider<R> recipeProvider = createRecipeProvider();
	
	protected R currentRecipe;
	protected int lastRefresh;
	
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.ITEM))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.NONE)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.NONE);
	
	@NBTSerializable("Items")
	public final SidedInventory inventory = createSidedInventory();
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	@NBTSerializable("FE")
	public final EnergyManager energy = createEnergyManager();
	
	@NBTSerializable("Progress")
	public int _progress;
	
	@NBTSerializable("MaxProgress")
	public int _maxProgress;
	
	public final PropertyInt energyStored = new PropertyInt(DirectStorage.create(energy.fe::setEnergyStored, energy.fe::getEnergyStored));
	public final PropertyInt progress = new PropertyInt(DirectStorage.create(i -> _progress = i, () -> _progress));
	public final PropertyInt maxProgress = new PropertyInt(DirectStorage.create(i -> _maxProgress = i, () -> _maxProgress));
	public final PropertyItemStack inputItemDisplay = new PropertyItemStack(DirectStorage.allocate(ItemStack.EMPTY));
	
	public TileUnaryRecipeMachineB(BlockEntityType<T> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		dispatcher.registerProperty("display", inputItemDisplay);
	}
	
	protected ResourceLocation getGuiCustomTexture()
	{
		return null;
	}
	
	protected abstract IUnaryRecipe.IUnaryRecipeProvider<R> createRecipeProvider();
	
	protected SidedInventory createSidedInventory()
	{
		var inv = new SidedInventory(2, sidedConfig.createItemAccess(new int[] { 0 }, new int[] { 1 }));
		inv.isStackValid = (slot, stack) -> level != null && slot == 0 && recipeProvider.findMatching(this, stack).isPresent();
		return inv;
	}
	
	protected int getConsumptionPerTick()
	{
		return 20;
	}
	
	@Override
	public ContainerUnaryRecipeMachineB<T> openContainer(Player player, int windowId)
	{
		return new ContainerUnaryRecipeMachineB(this, player, windowId);
	}
	
	protected EnergyManager createEnergyManager()
	{
		return new EnergyManager(EnergyTier.EXTRA_LOW_VOLTAGE, EnumEnergyManagerKind.CONSUMER);
	}
	
	protected SoundEvent getWorkingSound()
	{
		return null;
	}
	
	protected SoundEvent getInterruptSound()
	{
		return SoundsZT_Processing.BASIC_MACHINE_INTERRUPT;
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		
		if(isOnServer())
		{
			if(atTickRate(5))
			{
				ItemStack first = inventory.getItem(0).copy();
				if(first.isEmpty()) first = inventory.getItem(1).copy();
				inputItemDisplay.set(first);
			}
			
			getActiveRecipe().ifPresentOrElse(recipe ->
			{
				maxProgress.setInt(recipe.getCraftTime());
				
				if(_progress < _maxProgress && isOnServer()
						&& (_progress > 0 || storeRecipeResult(recipe, true)))
				{
					if(redstone.shouldWork(this) && energy.consumeEnergy(getConsumptionPerTick()))
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
				
				if(isOnServer() && _progress >= _maxProgress && storeRecipeResult(recipe, false))
				{
					inventory.getItem(0)
							.shrink(recipe.getInputCount());
					_progress = 0;
					sync();
				}
			}, () ->
			{
				if(_progress > 0)
					_progress = Math.max(0, _progress - 2);
				else if(isEnabled())
					setEnabledState(false);
			});
		}
		
		if(isOnClient() && isEnabled() && !isInterrupted())
		{
			var work = getWorkingSound();
			if(work != null)
				ZeithTechAPI.get()
						.getAudioSystem()
						.playMachineSoundLoop(this, work, getInterruptSound());
		}
	}
	
	protected boolean storeRecipeResult(R recipe, boolean simulate)
	{
		return store(recipe.assemble(this), simulate);
	}
	
	public boolean store(ItemStack stacks, boolean simulate)
	{
		return InventoryHelper.storeStack(inventory, IntStream.range(1, 2), stacks, simulate);
	}
	
	public Optional<R> getActiveRecipe()
	{
		var input = inventory.getItem(0);
		
		if(currentRecipe != null
				&& currentRecipe.matches(this, input)
				&& currentRecipe.techTierMatches(this)
		) return Optional.of(currentRecipe);
		
		currentRecipe = null;
		
		if(ticksExisted - lastRefresh > 5 && !input.isEmpty())
		{
			currentRecipe = recipeProvider
					.findMatching(this, input)
					.orElse(null);
			
			lastRefresh = ticksExisted;
		}
		
		return Optional.ofNullable(currentRecipe);
	}
	
	@Override
	public TechTier getTechTier()
	{
		return TechTier.BASIC;
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
										? new Tuple2<>(slot, inventory.sidedItemAccess.canPlaceItemThroughFace(slot, dir) ? SlotRole.BOTH : SlotRole.OUTPUT)
										: inventory.sidedItemAccess.canPlaceItemThroughFace(slot, dir)
										? new Tuple2<>(slot, SlotRole.INPUT)
										: null)
				)
				.filter(Objects::nonNull)
				.distinct()
				.map(pair -> ISlot.simpleSlot(new ContainerItemSlotAccess(inventory, pair.first(), pair.second()), pair.second(), pair.toString(), getClass().getSimpleName()))
				.forEach(lst::add);
		
		return lst.build();
	}).get();
	
	@Override
	public List<ISlot<?>> getSlots()
	{
		return slots;
	}
}