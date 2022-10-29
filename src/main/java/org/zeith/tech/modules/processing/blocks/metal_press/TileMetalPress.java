package org.zeith.tech.modules.processing.blocks.metal_press;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.recipes.processing.RecipeHammering;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.tile.slots.*;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.*;
import org.zeith.tech.utils.InventoryHelper;
import org.zeith.tech.utils.SidedInventory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class TileMetalPress
		extends TileBaseMachine<TileMetalPress>
		implements ITileSlotProvider
{
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.ITEM))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.NONE)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.NONE);
	
	@NBTSerializable("Items")
	public final SidedInventory inventory = new SidedInventory(2, sidedConfig.createItemAccess(new int[] { 0 }, new int[] { 1 }));
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.LOW_VOLTAGE, EnumEnergyManagerKind.CONSUMER);
	
	@NBTSerializable("Progress")
	public int _progress;
	
	@NBTSerializable("MaxProgress")
	public int _maxProgress = 100;
	
	public final PropertyInt progress = new PropertyInt(DirectStorage.create(i -> _progress = i, () -> _progress));
	public final PropertyInt maxProgress = new PropertyInt(DirectStorage.create(i -> _maxProgress = i, () -> _maxProgress));
	public final PropertyItemStack inputItemDisplay = new PropertyItemStack(DirectStorage.allocate(ItemStack.EMPTY));
	
	public int fallTimer = 0;
	
	public TileMetalPress(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.METAL_PRESS, pos, state);
		dispatcher.registerProperty("display", progress);
		dispatcher.registerProperty("progress", inputItemDisplay);
		dispatcher.registerProperty("max_progress", maxProgress);
	}
	
	public int prevProgress, currentProgress;
	
	@Override
	public void update()
	{
		prevProgress = currentProgress;
		currentProgress = _progress;
		
		if(fallTimer > 0 && fallTimer < 5)
			++fallTimer;
		if(fallTimer == 5 && isOnClient())
		{
			prevProgress = currentProgress = _progress = 0;
			fallTimer = -1;
		}
		
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
					if((redstone.shouldWork(this) || _progress > _maxProgress - 6) && energy.consumeEnergy(getConsumptionPerTick()))
					{
						progress.setInt(_progress + 1);
//						_progress++;
						isInterrupted.setBool(false);
					} else
					{
						isInterrupted.setBool(true);
					}
				}
				
				var enable = _progress > 0;
				if(isEnabled() != enable)
					setEnabledState(enable);
				
				if(_progress == _maxProgress - 5)
					level.blockEvent(worldPosition, getBlockState().getBlock(), 2, 195);
				
				if(_progress >= _maxProgress && storeRecipeResult(recipe, false))
				{
					inventory.getItem(0)
							.shrink(recipe.getInputCount());
					_progress = 0;
					level.blockEvent(worldPosition, getBlockState().getBlock(), 2, 200);
					
					var p3d = Vec3.atLowerCornerOf(worldPosition);
					
					for(LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(p3d.x + 1 / 16F, p3d.y + 4 / 16F, p3d.z + 1 / 16F, p3d.x + 15 / 16F, p3d.y + 1F, p3d.z + 15 / 16F)))
						entity.hurt(DamageSource.ANVIL, 8);
				}
			}, () ->
			{
				if(_progress > 0)
					progress.setInt(Math.max(_progress - 2, 0));
				else if(isEnabled())
					setEnabledState(false);
			});
		}
	}
	
	private int getConsumptionPerTick()
	{
		return 40;
	}
	
	protected boolean storeRecipeResult(RecipeHammering recipe, boolean simulate)
	{
		return store(recipe.assemble(this), simulate);
	}
	
	public boolean store(ItemStack stacks, boolean simulate)
	{
		return InventoryHelper.storeStack(inventory, IntStream.range(1, 2), stacks, simulate);
	}
	
	@Override
	public ContainerBaseMachine<TileMetalPress> openContainer(Player player, int windowId)
	{
		return new ContainerMetalPress(this, player, windowId);
	}
	
	private ResourceLocation prevRecipeId;
	
	public Optional<RecipeHammering> getActiveRecipe()
	{
		if(prevRecipeId != null)
		{
			var r = RecipeRegistriesZT_Processing.HAMMERING.getRecipe(prevRecipeId);
			if(r != null && r.matches(null, inventory.getItem(0), TechTier.ADVANCED))
				return Optional.of(r);
			prevRecipeId = null;
		}
		
		if(atTickRate(5))
		{
			var r = RecipeRegistriesZT_Processing.HAMMERING.getRecipes().stream().filter(r0 -> r0.matches(null, inventory.getItem(0), TechTier.ADVANCED)).findFirst().orElse(null);
			if(r != null)
			{
				prevRecipeId = r.getRecipeName();
				return Optional.of(r);
			}
		}
		
		return Optional.empty();
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(inventory, energy.batteryInventory);
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
	
	@Override
	public boolean triggerEvent(int event, int data)
	{
		if(event == 2)
		{
			if(data == 195)
			{
				fallTimer = 1;
				currentProgress = _maxProgress - 5;
				return true;
			}
			
			if(data == 200)
			{
				if(isOnClient())
					ZeithTechAPI.get().getAudioSystem().playTileSound(this, SoundsZT_Processing.METAL_PRESS_ACT, 0.5F, 0.8F);
				return true;
			}
		}
		
		return super.triggerEvent(event, data);
	}
}