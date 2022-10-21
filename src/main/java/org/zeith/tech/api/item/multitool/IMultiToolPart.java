package org.zeith.tech.api.item.multitool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IMultiToolPart
{
	ResourceLocation getMultiToolPartModel(ItemStack partStack, ItemStack multiToolStack);
	
	List<ResourceLocation> getAllPossibleMultiToolPartModels();
	
	default Item multiToolPartAsItem()
	{
		return (Item) this;
	}
}