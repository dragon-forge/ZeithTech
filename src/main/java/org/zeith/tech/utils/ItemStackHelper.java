package org.zeith.tech.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemStackHelper
{
	public static boolean matchesIgnoreCount(ItemStack a, ItemStack b)
	{
		return (a.isEmpty() && b.isEmpty()) || ItemHandlerHelper.canItemStacksStack(a, b);
	}
}