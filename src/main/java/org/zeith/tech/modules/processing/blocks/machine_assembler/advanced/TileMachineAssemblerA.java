package org.zeith.tech.modules.processing.blocks.machine_assembler.advanced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyResourceLocation;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.item.IRecipePatternItem;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.modules.processing.blocks.machine_assembler.TileAbstractMachineAssembler;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;
import org.zeith.tech.utils.ISidedItemAccess;
import org.zeith.tech.utils.ItemStackHelper;

import java.util.*;
import java.util.stream.IntStream;

public class TileMachineAssemblerA
		extends TileAbstractMachineAssembler<TileMachineAssemblerA>
{
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.ITEM))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.PULL)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.NONE)
			.setForAll(RelativeDirection.UP, SideConfig.DISABLE);
	
	@NBTSerializable("Pattern")
	public final SimpleInventory patternInventory = new SimpleInventory(2);
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.MEDIUM_VOLTAGE, EnumEnergyManagerKind.CONSUMER);
	
	@NBTSerializable("ForcedRecipe")
	protected ResourceLocation _forcedRecipeId;
	
	public final WorldlyContainer inventory = new AutomatedContainer();
	
	public final PropertyResourceLocation forcedRecipeId = new PropertyResourceLocation(DirectStorage.create(v -> _forcedRecipeId = v, () -> _forcedRecipeId));
	
	public TileMachineAssemblerA(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.ADVANCED_MACHINE_ASSEMBLER, pos, state);
		dispatcher.registerProperty("pattern_recipe", forcedRecipeId);
		
		patternInventory.isStackValid = (slot, stack) -> slot == 0
				&& !stack.isEmpty()
				&& stack.getItem() instanceof IRecipePatternItem prov
				&& ((prov.getProvidedRecipe(stack) instanceof RecipeMachineAssembler rec && rec.isTierGoodEnough(getTechTier())) || prov.getProvidedRecipe(stack) == null);
	}
	
	@Override
	public void update()
	{
		prevProgress = _progress;
		
		energy.update(level, worldPosition, sidedConfig);
		
		if(isOnServer())
		{
			var r = getActiveRecipe();
			
			if(r != null)
			{
				if(!isValidRecipe(r))
				{
					setEnabledState(false);
				}
			}
			
			if(r == null && atTickRate(5))
			{
				var recipe = RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY
						.getRecipes()
						.stream()
						.filter(this::isValidRecipe)
						.findFirst()
						.orElse(null);
				
				if(recipe != null)
				{
					_activeRecipeId = recipe.id;
					craftResult.set(recipe.getRecipeOutput(this));
					setEnabledState(true);
				} else
					craftResult.set(ItemStack.EMPTY);
			}
			
			if(isOnServer() && r != null && redstone.shouldWork(this) && craftingProgress.getInt() < craftTime.getInt() && energy.consumeEnergy(getEnergyUsage()))
			{
				if(_craftResult.isEmpty())
					craftResult.set(r.getRecipeOutput(this));
				craftingProgress.setInt(_progress + 1);
			}
			
			if(_progress >= _craftTime)
			{
				if(!_craftResult.isEmpty())
				{
					var stored = resultInventory.getItem(0);
					
					movePattern();
					
					for(ItemStack stack : craftingInventory)
						if(!stack.isEmpty())
							stack.shrink(1);
					level.blockEvent(worldPosition, getBlockState().getBlock(), 1, 1);
					
					if(stored.isEmpty())
						resultInventory.setItem(0, _craftResult);
					else if(ItemStackHelper.matchesIgnoreCount(stored, _craftResult) && stored.getCount() + _craftResult.getCount() <= stored.getMaxStackSize())
						stored.grow(_craftResult.getCount());
					
					craftResult.set(ItemStack.EMPTY);
					craftingProgress.setInt(0);
				}
			}
			
			if(r == null && _progress > 0)
				craftingProgress.setInt(0);
		}
	}
	
	public void movePattern()
	{
		var recipe = getActiveRecipe();
		
		if(patternInventory.getItem(1).isEmpty())
		{
			var item = patternInventory.extractItem(0, 1, false);
			
			if(item.getItem() instanceof IRecipePatternItem pat && recipe != null)
				item = pat.createEncoded(RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY, recipe);
			
			patternInventory.setItem(1, item);
		}
	}
	
	@Override
	public ContainerMachineAssemblerA openContainer(Player player, int windowId)
	{
		return new ContainerMachineAssemblerA(this, player, windowId);
	}
	
	public RecipeMachineAssembler getActiveRecipe()
	{
		if(!patternInventory.getItem(1).isEmpty())
			return null;
		
		if(!patternInventory.getItem(0).isEmpty() && patternInventory.getItem(0).getItem() instanceof IRecipePatternItem provider)
		{
			if(provider.getProvidedRecipe(patternInventory.getItem(0)) instanceof RecipeMachineAssembler recipe && recipe.isTierGoodEnough(getTechTier()))
			{
				forcedRecipeId.set(recipe.getRecipeName());
				if(isValidRecipe(recipe))
				{
					_activeRecipeId = recipe.getRecipeName();
					return recipe;
				}
			} else
				forcedRecipeId.set(null);
		} else
			forcedRecipeId.set(null);
		
		var rec = RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY.getRecipe(_activeRecipeId);
		if(rec != null && !isValidRecipe(rec))
		{
			_activeRecipeId = null;
			craftResult.set(ItemStack.EMPTY);
			craftResult.markChanged(true);
			craftingProgress.setInt(0);
			return null;
		}
		
		return rec;
	}
	
	@Override
	public boolean hasInputSlot(int slot)
	{
		int x = slot % 5, y = slot / 5;
		
		int start = 0;
		int end = 5;
		if(y == 0 || y == 4)
		{
			start = 1;
			end = 4;
		}
		
		return x >= start && x < end;
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(craftingInventory, resultInventory, patternInventory);
	}
	
	@Override
	public TechTier getTechTier()
	{
		return TechTier.ADVANCED;
	}
	
	public int getEnergyUsage()
	{
		return 64;
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
	
	protected class AutomatedContainer
			implements WorldlyContainer
	{
		protected final Map<Integer, Tuple2<SimpleInventory, Integer>> slotMapping = new HashMap<>();
		
		public final int[] outputSlot = new int[] { 0 };
		public final int[] patternSlots = IntStream.range(1, 3).toArray();
		public final int[] craftingSlots = IntStream.range(3, 21 + 3).toArray();
		
		protected final ISidedItemAccess access;
		
		public AutomatedContainer()
		{
			access = sidedConfig.createItemAccess(IntStream.concat(IntStream.of(craftingSlots), IntStream.of(1)).toArray(),
					new int[] {
							0,
							2
					});
			
			for(int i = 0; i < outputSlot.length; i++)
				slotMapping.put(outputSlot[i], Tuples.immutable(resultInventory, i));
			for(int i = 0; i < patternSlots.length; i++)
				slotMapping.put(patternSlots[i], Tuples.immutable(patternInventory, i));
			
			int j = 0;
			for(int i = 0; i < 25; ++i)
			{
				if(hasInputSlot(i))
				{
					slotMapping.put(craftingSlots[j], Tuples.immutable(craftingInventory, i));
					++j;
				}
			}
		}
		
		@Override
		public boolean canPlaceItem(int slot, ItemStack stack)
		{
			return Optional.ofNullable(slotMapping.get(slot))
					.map(t ->
					{
						if(t.a() == craftingInventory)
						{
							RecipeMachineAssembler rec;
							if(_forcedRecipeId != null && (rec = RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY.getRecipe(_forcedRecipeId)) != null && rec.isTierGoodEnough(getTechTier()))
							{
								int actualSlot = t.b();
								return rec.isValidIngredient(actualSlot, stack);
							}
							
							return false;
						}
						
						return t.a().isItemValid(t.b(), stack);
					})
					.orElse(false);
		}
		
		@Override
		public int[] getSlotsForFace(Direction dir)
		{
			return access.getSlotsForFace(dir);
		}
		
		@Override
		public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir)
		{
			return access.canPlaceItemThroughFace(slot, dir)
					&& canPlaceItem(slot, stack);
		}
		
		@Override
		public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir)
		{
			return access.canTakeItemThroughFace(slot, dir);
		}
		
		@Override
		public int getContainerSize()
		{
			return outputSlot.length + patternSlots.length + craftingSlots.length;
		}
		
		@Override
		public boolean isEmpty()
		{
			return slotMapping.values().stream().allMatch(c -> c.a().getItem(c.b()).isEmpty());
		}
		
		@Override
		public ItemStack getItem(int slot)
		{
			return Optional.ofNullable(slotMapping.get(slot))
					.map(t -> t.a().getItem(t.b()))
					.orElse(ItemStack.EMPTY);
		}
		
		@Override
		public ItemStack removeItem(int slot, int count)
		{
			return Optional.ofNullable(slotMapping.get(slot))
					.map(t -> t.a().removeItem(t.b(), count))
					.orElse(ItemStack.EMPTY);
		}
		
		@Override
		public ItemStack removeItemNoUpdate(int slot)
		{
			return Optional.ofNullable(slotMapping.get(slot))
					.map(t -> t.a().removeItemNoUpdate(t.b()))
					.orElse(ItemStack.EMPTY);
		}
		
		@Override
		public void setItem(int slot, ItemStack stack)
		{
			Optional.ofNullable(slotMapping.get(slot))
					.ifPresent(t -> t.a().setItem(t.b(), stack));
		}
		
		@Override
		public void setChanged()
		{
			sync();
			slotMapping.values()
					.stream()
					.map(Tuple2::a)
					.distinct()
					.forEach(Container::setChanged);
		}
		
		@Override
		public boolean stillValid(Player player)
		{
			return !isRemoved() && player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 64D;
		}
		
		@Override
		public void clearContent()
		{
			slotMapping.values()
					.forEach(t -> t.a().setItem(t.b(), ItemStack.EMPTY));
		}
		
		@Override
		public int getMaxStackSize()
		{
			return 1;
		}
	}
}