package org.zeith.tech.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ISidedItemAccess
{
	int[] getSlotsForFace(Direction face);
	
	boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction face);
	
	boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face);
}