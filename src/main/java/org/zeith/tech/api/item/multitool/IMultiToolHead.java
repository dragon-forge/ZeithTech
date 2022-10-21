package org.zeith.tech.api.item.multitool;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface IMultiToolHead
		extends IMultiToolPart
{
	boolean isCorrectHeadForDrops(ItemStack headStack, ItemStack multiToolStack, BlockState state);
	
	float getHeadMiningSpeed(ItemStack headStack, ItemStack multiToolStack, BlockState state);
}