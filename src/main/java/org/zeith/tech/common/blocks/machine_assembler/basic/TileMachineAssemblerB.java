package org.zeith.tech.common.blocks.machine_assembler.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.api.crafting.ICraftingExecutor;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.*;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.RecipeMachineAssembler;
import org.zeith.tech.api.recipes.RecipeRegistriesZT;
import org.zeith.tech.common.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.common.items.ItemHammer;
import org.zeith.tech.init.TilesZT;
import org.zeith.tech.utils.ItemStackHelper;

import java.util.ArrayList;
import java.util.List;

public class TileMachineAssemblerB
		extends TileBaseMachine<TileMachineAssemblerB>
		implements ICraftingExecutor
{
	@NBTSerializable
	public final SimpleInventory craftingInventory = new SimpleInventory(25);
	
	@NBTSerializable("ToolInv")
	public final SimpleInventory toolInventory = new SimpleInventory(1);
	
	@NBTSerializable("ResultInv")
	public final SimpleInventory resultInventory = new SimpleInventory(1);
	
	@NBTSerializable("ActiveRecipe")
	private ResourceLocation _activeRecipeId;
	
	@NBTSerializable("CraftProgress")
	private int _progress;
	
	@NBTSerializable("CraftTime")
	private int _craftTime = 100;
	
	@NBTSerializable("CraftResult")
	private ItemStack _craftResult = ItemStack.EMPTY;
	
	public final PropertyResourceLocation activeRecipeId = new PropertyResourceLocation(DirectStorage.create(r -> _activeRecipeId = r, () -> _activeRecipeId));
	public final PropertyItemStack craftResult = new PropertyItemStack(DirectStorage.create(v -> _craftResult = v, () -> _craftResult));
	
	// GUI-synced parameters.
	public final PropertyInt craftingProgress = new PropertyInt(DirectStorage.create(v -> _progress = v, () -> _progress));
	public final PropertyInt craftTime = new PropertyInt(DirectStorage.create(v -> _craftTime = v, () -> _craftTime));
	
	
	private final List<Player> playerWithOpenMenu = new ArrayList<>();
	
	public TileMachineAssemblerB(BlockPos pos, BlockState state)
	{
		super(TilesZT.MACHINE_ASSEMBLER_BASIC, pos, state);
		this.dispatcher.registerProperty("ar_id", activeRecipeId);
		
		toolInventory.isStackValid = (slot, stack) -> !stack.isEmpty() && stack.getItem() instanceof ItemHammer;
		resultInventory.isStackValid = (slot, stack) -> false;
	}
	
	@Override
	public void update()
	{
		if(isOnServer())
		{
			var r = getActiveRecipe();
			
			if(r != null)
			{
				if(!isValidRecipe(r))
				{
					activeRecipeId.set(null);
					setEnabledState(false);
				}
			}
			
			if(atTickRate(5))
				playerWithOpenMenu.removeIf(p -> !(p.containerMenu instanceof ContainerMachineAssemblerB a && a.tile == this));
			
			if(r == null && atTickRate(2) && !playerWithOpenMenu.isEmpty())
			{
				var recipe = RecipeRegistriesZT.MACHINE_ASSEBMLY.getRecipes().stream().filter(this::isValidRecipe).findFirst().orElse(null);
				if(recipe != null)
				{
					activeRecipeId.set(recipe.getRecipeName());
					setEnabledState(true);
				}
			}
			
			if(!_craftResult.isEmpty())
			{
				var hammer = toolInventory.getItem(0);
				
				var hasHammer = !hammer.isEmpty() && hammer.getItem() instanceof ItemHammer;
				
				if(_progress < _craftTime && hasHammer)
					++_progress;
				
				if(_progress >= _craftTime && hasHammer)
				{
					if(hammer.hurt(1, level.random, null))
					{
						hammer.shrink(1);
						hammer.setDamageValue(0);
					}
					
					var stored = resultInventory.getItem(0);
					
					if(stored.isEmpty())
					{
						resultInventory.setItem(0, _craftResult);
						craftResult.set(ItemStack.EMPTY);
						_progress = 0;
					} else if(ItemStackHelper.matchesIgnoreCount(stored, _craftResult) && stored.getCount() + _craftResult.getCount() <= stored.getMaxStackSize())
					{
						stored.grow(_craftResult.getCount());
						craftResult.set(ItemStack.EMPTY);
						_progress = 0;
					}
				}
			}
		}
		
		if(isOnClient())
		{
			playerWithOpenMenu.clear();
		}
	}
	
	public boolean performCraftOperation()
	{
		if(isOnServer() && _craftResult.isEmpty())
		{
			var r = getActiveRecipe();
			if(r != null)
			{
				var out = r.getRecipeOutput(this);
				if(!out.isEmpty())
				{
					for(ItemStack stack : craftingInventory)
						if(!stack.isEmpty())
							stack.shrink(1);
					craftResult.set(out);
					craftingProgress.setInt(0);
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public ContainerMachineAssemblerB openContainer(Player player, int windowId)
	{
		playerWithOpenMenu.add(player);
		return new ContainerMachineAssemblerB(this, player, windowId);
	}
	
	public RecipeMachineAssembler getActiveRecipe()
	{
		return RecipeRegistriesZT.MACHINE_ASSEBMLY.getRecipe(_activeRecipeId);
	}
	
	private boolean isValidRecipe(RecipeMachineAssembler recipe)
	{
		return (resultInventory.getItem(0).isEmpty() || ItemStackHelper.matchesIgnoreCount(resultInventory.getItem(0), recipe.getRecipeOutput(this))) && recipe.matches(craftingInventory, TechTier.BASIC);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(craftingInventory, resultInventory, toolInventory);
	}
}