package org.zeith.tech.modules.processing.blocks.machine_assembler.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.LogicalSide;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.api.tile.IHammerable;
import org.zeith.tech.modules.processing.blocks.machine_assembler.TileAbstractMachineAssembler;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;
import org.zeith.tech.utils.ItemStackHelper;

import java.util.List;

public class TileMachineAssemblerB
		extends TileAbstractMachineAssembler<TileMachineAssemblerB>
		implements IHammerable
{
	public TileMachineAssemblerB(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.BASIC_MACHINE_ASSEMBLER, pos, state);
		this.dispatcher.registerProperty("result", craftResult);
		
		resultInventory.isStackValid = (slot, stack) -> false;
	}
	
	@Override
	public void update()
	{
		prevProgress = _progress;
		
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
			
			if(_progress >= _craftTime)
			{
				var ar = getActiveRecipe();
				if(_craftResult.isEmpty() && ar != null)
					craftResult.set(ar.getRecipeOutput(this));
				
				if(!_craftResult.isEmpty())
				{
					var stored = resultInventory.getItem(0);
					
					for(ItemStack stack : craftingInventory)
						if(!stack.isEmpty())
							stack.shrink(1);
					
					if(stored.isEmpty())
					{
						level.blockEvent(worldPosition, getBlockState().getBlock(), 1, 1);
						resultInventory.setItem(0, _craftResult);
						craftResult.set(ItemStack.EMPTY);
						craftingProgress.setInt(0);
					} else if(ItemStackHelper.matchesIgnoreCount(stored, _craftResult) && stored.getCount() + _craftResult.getCount() <= stored.getMaxStackSize())
					{
						level.blockEvent(worldPosition, getBlockState().getBlock(), 1, 1);
						stored.grow(_craftResult.getCount());
						craftResult.set(ItemStack.EMPTY);
						craftingProgress.setInt(0);
					}
				}
			}
			
			if(r == null && _progress > 0)
				craftingProgress.setInt(0);
		}
	}
	
	@Override
	public ContainerMachineAssemblerB openContainer(Player player, int windowId)
	{
		return new ContainerMachineAssemblerB(this, player, windowId);
	}
	
	public RecipeMachineAssembler getActiveRecipe()
	{
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
	public List<Container> getAllInventories()
	{
		return List.of(craftingInventory, resultInventory);
	}
	
	@Override
	public boolean onHammerLeftClicked(ItemStack hammerStack, LogicalSide side, Direction face, Player player, InteractionHand hand, BlockHitResult vec)
	{
		if(face != Direction.UP || vec.getLocation().y < worldPosition.getY() + 0.875F)
			return false;
		
		var recipe = getActiveRecipe();
		
		if(recipe != null && craftingProgress.getInt() < craftTime.getInt())
		{
			if(side == LogicalSide.SERVER)
			{
				craftingProgress.setInt(_progress + 10);
				sync();
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public TechTier getTechTier()
	{
		return TechTier.BASIC;
	}
	
	@Override
	public boolean hasInputSlot(int slot)
	{
		int x = slot % 5, y = slot / 5;
		
		int start = 0;
		int end = 5;
		if(y == 0 || y == 4)
		{
			start = 2;
			end = 3;
		} else if(y == 1 || y == 3)
		{
			start = 1;
			end = 4;
		}
		
		return x >= start && x < end;
	}
}