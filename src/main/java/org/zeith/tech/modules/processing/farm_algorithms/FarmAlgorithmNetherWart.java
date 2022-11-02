package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.tech.api.misc.farm.*;

public class FarmAlgorithmNetherWart
		extends FarmAlgorithm
{
	public FarmAlgorithmNetherWart()
	{
		super(new Properties()
				.upsideDown(false)
		);
	}
	
	@Override
	public int getColor()
	{
		return 0x992127;
	}
	
	@Override
	public @NotNull Ingredient getProgrammingItem()
	{
		return RecipeHelper.fromComponent(Items.NETHER_WART);
	}
	
	@Override
	public @NotNull EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack)
	{
		if(stack.is(Items.NETHER_WART)) return EnumFarmItemCategory.PLANT;
		
		if(stack.getItem() instanceof BlockItem bi)
		{
			var state = bi.getBlock().defaultBlockState();
			if(state.canSustainPlant(controller.getFarmLevel(), controller.getFarmPosition(), Direction.UP, (NetherWartBlock) Blocks.NETHER_WART))
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
		if(!sandState.canSustainPlant(level, sandPos, Direction.UP, (NetherWartBlock) Blocks.NETHER_WART))
		{
			if(level.isEmptyBlock(sandPos))
			{
				ItemStack match = new ItemStack(Items.SOUL_SAND);
				
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
		
		var wartPos = sandPos.above();
		var wartState = level.getBlockState(wartPos);
		
		// Plant nether wart
		if(!wartState.is(Blocks.NETHER_WART) && level.isEmptyBlock(wartPos))
		{
			controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, new ItemStack(Items.NETHER_WART)),
					wartPos, Blocks.NETHER_WART.defaultBlockState(), 0, 0);
			
			return AlgorithmUpdateResult.SUCCESS;
		}
		
		// Harvest grown nether wart
		if(wartState.is(Blocks.NETHER_WART) && wartState.getValue(NetherWartBlock.AGE) >= 3)
		{
			controller.queueBlockHarvest(wartPos, 0);
			
			controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, new ItemStack(Items.NETHER_WART)),
					wartPos, Blocks.NETHER_WART.defaultBlockState(), 0, 0);
			
			return AlgorithmUpdateResult.SUCCESS;
		}
		
		return AlgorithmUpdateResult.PASS;
	}
	
	@Override
	public boolean tryFertilize(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		return false;
	}
}