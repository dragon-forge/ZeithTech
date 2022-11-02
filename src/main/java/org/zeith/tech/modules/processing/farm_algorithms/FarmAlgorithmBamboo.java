package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.tech.api.misc.farm.*;

public class FarmAlgorithmBamboo
		extends FarmAlgorithm
{
	public FarmAlgorithmBamboo()
	{
		super(new Properties()
				.upsideDown(false)
		);
	}
	
	@Override
	public int getColor()
	{
		return 0x418B0A;
	}
	
	@Override
	public @NotNull Ingredient getProgrammingItem()
	{
		return RecipeHelper.fromComponent(Items.BAMBOO);
	}
	
	@Override
	public @NotNull EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack)
	{
		if(stack.is(Items.BAMBOO)) return EnumFarmItemCategory.PLANT;
		if(stack.is(Items.BONE_MEAL)) return EnumFarmItemCategory.FERTILIZER;
		
		if(stack.getItem() instanceof BlockItem bi)
		{
			var state = bi.getBlock().defaultBlockState();
			if(state.canSustainPlant(controller.getFarmLevel(), controller.getFarmPosition(), Direction.UP, (BambooBlock) Blocks.BAMBOO))
				return EnumFarmItemCategory.SOIL;
		}
		
		return EnumFarmItemCategory.UNKNOWN;
	}
	
	@Override
	public AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var sandPos = platform.above();
		var sandState = level.getBlockState(sandPos);
		
		// Place dirt
		if(!sandState.canSustainPlant(level, sandPos, Direction.UP, (BambooBlock) Blocks.BAMBOO))
		{
			if(level.isEmptyBlock(sandPos))
			{
				ItemStack match = new ItemStack(Items.DIRT);
				
				var inv = controller.getInventory(EnumFarmItemCategory.SOIL);
				for(int i = 0; i < inv.getSlots(); ++i)
				{
					var it = inv.getStackInSlot(i);
					if(!it.isEmpty() && categorizeItem(controller, it) == EnumFarmItemCategory.SOIL)
					{
						match = it.copy().split(1);
						break;
					}
				}
				
				if(!match.isEmpty() && match.getItem() instanceof BlockItem bi)
					controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.SOIL, match),
							sandPos, bi.getBlock().defaultBlockState(), 0, 1);
				
				return AlgorithmUpdateResult.RETRY;
			} else
			{
				controller.queueBlockHarvest(sandPos, 2);
				return AlgorithmUpdateResult.RETRY;
			}
		}
		
		var cactiPos = sandPos.above();
		var cactiState = level.getBlockState(cactiPos);
		
		// Plant bamboo
		if(!cactiState.is(Blocks.BAMBOO) && level.isEmptyBlock(cactiPos))
		{
			if(Blocks.BAMBOO.defaultBlockState().canSurvive(level, cactiPos))
			{
				controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, new ItemStack(Items.BAMBOO)),
						cactiPos, Blocks.BAMBOO.defaultBlockState(), 100, 0);
				
				return AlgorithmUpdateResult.SUCCESS;
			}
			
			return AlgorithmUpdateResult.PASS;
		}
		
		// Harvest bamboo
		if(cactiState.is(Blocks.BAMBOO))
		{
			var maxCacti = cactiPos;
			
			int harvest = 0;
			
			var grownCacti = cactiPos.above();
			while(true)
			{
				if(level.getBlockState(grownCacti).is(Blocks.BAMBOO))
				{
					++harvest;
					maxCacti = grownCacti;
					grownCacti = grownCacti.above();
					continue;
				}
				
				break;
			}
			
			// We have found more bamboo above the original one, might as well harvest it.
			if(maxCacti.getY() > cactiPos.getY())
			{
				controller.queueBlockHarvest(maxCacti, 0);
				return harvest > 1 ? AlgorithmUpdateResult.RETRY_NOW : AlgorithmUpdateResult.SUCCESS;
			}
		}
		
		return AlgorithmUpdateResult.PASS;
	}
	
	@Override
	public boolean tryFertilize(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var cropPos = platform.above(2);
		var cropState = level.getBlockState(cropPos);
		
		if(cropState.getBlock() instanceof BonemealableBlock gr
				&& gr.isValidBonemealTarget(level, cropPos, cropState, level.isClientSide)
				&& gr.isBonemealSuccess(level, level.random, cropPos, cropState))
		{
			gr.performBonemeal(level, level.random, cropPos, cropState);
			level.levelEvent(1505, cropPos, 0);
			return true;
		}
		
		return false;
	}
}