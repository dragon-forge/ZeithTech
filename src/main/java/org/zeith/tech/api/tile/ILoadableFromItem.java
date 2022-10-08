package org.zeith.tech.api.tile;

import net.minecraft.world.item.ItemStack;

public interface ILoadableFromItem
{
	void loadFromItem(ItemStack stack);
}