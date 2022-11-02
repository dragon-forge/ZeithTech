package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.tech.api.misc.farm.*;

public class FarmAlgorithmCactus
		extends FarmAlgorithm
{
	public FarmAlgorithmCactus()
	{
		super(new Properties()
				.upsideDown(false)
		);
	}
	
	@Override
	public int getColor()
	{
		return 0x39581A;
	}
	
	@Override
	public @NotNull Ingredient getProgrammingItem()
	{
		return RecipeHelper.fromComponent(Items.CACTUS);
	}
	
	@Override
	public @NotNull EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack)
	{
		if(stack.is(Items.CACTUS)) return EnumFarmItemCategory.PLANT;
		
		if(stack.getItem() instanceof BlockItem bi)
		{
			var state = bi.getBlock().defaultBlockState();
			if(state.canSustainPlant(controller.getFarmLevel(), controller.getFarmPosition(), Direction.UP, (CactusBlock) Blocks.CACTUS))
				return EnumFarmItemCategory.SOIL;
		}
		
		return EnumFarmItemCategory.UNKNOWN;
	}
	
	@Override
	public AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var sandPos = platform.above();
		var sandState = level.getBlockState(sandPos);
		
		// Place sand
		if(!sandState.canSustainPlant(level, sandPos, Direction.UP, (CactusBlock) Blocks.CACTUS))
		{
			if(level.isEmptyBlock(sandPos))
			{
				ItemStack match = new ItemStack(Items.SAND);
				
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
				return AlgorithmUpdateResult.SUCCESS;
			}
		}
		
		var cactiPos = sandPos.above();
		var cactiState = level.getBlockState(cactiPos);
		
		// Plant cacti
		if(!cactiState.is(Blocks.CACTUS) && level.isEmptyBlock(cactiPos))
		{
			for(var dir : Direction.Plane.HORIZONTAL)
			{
				var rp = platform.relative(dir);
				if(controller.hasPlatform(rp) && level.getBlockState(rp.above(2)).is(Blocks.CACTUS))
				{
					// Do not place cactus if we detect any of them nearby.
					return AlgorithmUpdateResult.PASS;
				}
			}
			
			controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, new ItemStack(Items.CACTUS)),
					cactiPos, Blocks.CACTUS.defaultBlockState(), 100, 0);
			
			return AlgorithmUpdateResult.SUCCESS;
		}
		
		// Harvest cacti
		if(cactiState.is(Blocks.CACTUS))
		{
			var maxCacti = cactiPos;
			
			int harvest = 0;
			
			var grownCacti = cactiPos.above();
			while(true)
			{
				if(level.getBlockState(grownCacti).is(Blocks.CACTUS))
				{
					++harvest;
					maxCacti = grownCacti;
					grownCacti = grownCacti.above();
					continue;
				}
				
				break;
			}
			
			// We have found a cactus above the original one, might as well harvest it.
			if(maxCacti.getY() > cactiPos.getY())
			{
				controller.queueBlockHarvest(maxCacti, 0);
				return harvest > 1 ? AlgorithmUpdateResult.RETRY : AlgorithmUpdateResult.SUCCESS;
			}
		}
		
		return AlgorithmUpdateResult.PASS;
	}
	
	@Override
	public boolean tryFertilize(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		return false;
	}
}