package org.zeith.tech.utils;

import net.minecraft.world.item.ItemStack;

public class ItemStackHelper
{
	public static boolean matchesIgnoreCount(ItemStack a, ItemStack b)
	{
		if(!a.is(b.getItem()))
			return false;
		else if(a.getTag() == null && b.getTag() != null)
			return false;
		else
			return (a.getTag() == null || a.getTag().equals(b.getTag())) && a.areCapsCompatible(b);
	}
}