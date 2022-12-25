package org.zeith.tech.compat.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.api.misc.SoundConfiguration;
import org.zeith.tech.api.misc.farm.*;
import org.zeith.tech.api.modules.processing.FarmData;
import vectorwing.farmersdelight.common.block.BuddingTomatoBlock;
import vectorwing.farmersdelight.common.block.TomatoVineBlock;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.ArrayList;

public class TomatoCropSubAlgorithm
		implements FarmData.ICropSubAlgorithm
{
	@Override
	public boolean plant(IFarmController controller, ServerLevel level, BlockPos platform, BlockPos cropPos, ItemStack stack)
	{
		if(!stack.isEmpty() && stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof BuddingTomatoBlock crop)
		{
			var newState = crop.getPlant(level, cropPos);
			controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, stack.copy().split(1)), cropPos, newState, 100, 1);
			return true;
		}
		
		return false;
	}
	
	@Override
	public AlgorithmUpdateResult takeCareOfPlant(IFarmController controller, ServerLevel level, BlockPos platform, BlockPos cropPos, BlockState cropState)
	{
		if(cropState.getBlock() instanceof TomatoVineBlock vine && vine.isMaxAge(cropState))
		{
			var drops = new ArrayList<ItemStack>();
			
			int quantity = 1 + level.random.nextInt(2);
			drops.add(new ItemStack(ModItems.TOMATO.get(), quantity));
			if(level.random.nextFloat() < 0.05) drops.add(new ItemStack(ModItems.ROTTEN_TOMATO.get()));
			
			controller.queueBlockTransformation(cropPos, cropState, vine.getStateForAge(0), drops, SoundConfiguration.place(cropState.getSoundType()), 5, 5);
			
			return AlgorithmUpdateResult.RETRY;
		}
		
		return AlgorithmUpdateResult.PASS;
	}
}