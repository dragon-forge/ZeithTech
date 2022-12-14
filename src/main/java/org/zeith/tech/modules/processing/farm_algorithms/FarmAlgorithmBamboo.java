package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.tech.api.misc.farm.*;
import org.zeith.tech.modules.processing.farm_algorithms.base.FarmAlgorithmPlantBased;

public class FarmAlgorithmBamboo
		extends FarmAlgorithmPlantBased
{
	public FarmAlgorithmBamboo()
	{
		super(new Properties()
						.upsideDown(false),
				(BambooBlock) Blocks.BAMBOO,
				Direction.UP
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
		if(isValidSoil(controller, controller.getFarmPosition(), stack)) return EnumFarmItemCategory.SOIL;
		return EnumFarmItemCategory.UNKNOWN;
	}
	
	@Override
	public AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var sandPos = platform.above();
		
		// Place dirt
		if(!canSustainPlant(controller, sandPos))
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
		
		var bambooPos = sandPos.above();
		var bambooState = level.getBlockState(bambooPos);
		
		// Plant bamboo
		if(!bambooState.is(Blocks.BAMBOO) && !bambooState.is(Blocks.BAMBOO_SAPLING) && level.isEmptyBlock(bambooPos))
		{
			if(canBePlantedHere(level, bambooPos))
			{
				controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, new ItemStack(Items.BAMBOO)),
						bambooPos, Blocks.BAMBOO_SAPLING.defaultBlockState(), 100, 0);
				
				return AlgorithmUpdateResult.SUCCESS;
			}
			
			return AlgorithmUpdateResult.PASS;
		}
		
		// Harvest bamboo
		if(bambooState.is(Blocks.BAMBOO))
		{
			var maxCacti = bambooPos;
			
			int harvest = 0;
			
			var grownCacti = bambooPos.above();
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
			if(maxCacti.getY() > bambooPos.getY())
			{
				controller.queueBlockHarvest(maxCacti, 0);
				return harvest > 1 ? AlgorithmUpdateResult.RETRY : AlgorithmUpdateResult.SUCCESS;
			}
		}
		
		return AlgorithmUpdateResult.PASS;
	}
}