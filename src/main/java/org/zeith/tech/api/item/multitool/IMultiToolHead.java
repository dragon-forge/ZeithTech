package org.zeith.tech.api.item.multitool;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface IMultiToolHead
		extends IMultiToolPart
{
	boolean isCorrectHeadForDrops(ItemStack headStack, ItemStack multiToolStack, BlockState state);
	
	float getHeadMiningSpeed(ItemStack headStack, ItemStack multiToolStack, BlockState state);
	
	default List<TagKey<Item>> getHeadItemTags(ItemStack headStack, ItemStack multiToolStack)
	{
		return List.of();
	}
}