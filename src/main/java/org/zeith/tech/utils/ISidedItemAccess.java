package org.zeith.tech.utils;

import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public interface ISidedItemAccess
{
	int[] getSlotsForFace(Direction face);
	
	boolean canPlaceItemThroughFace(int slot, @Nullable Direction face);
	
	boolean canTakeItemThroughFace(int slot, Direction face);
}