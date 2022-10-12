package org.zeith.tech.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;

public class SidedInventory
		extends SimpleInventory
		implements WorldlyContainer
{
	public final ISidedItemAccess sidedItemAccess;
	
	public SidedInventory(int slotCount, ISidedItemAccess sidedItemAccess)
	{
		super(slotCount);
		this.sidedItemAccess = sidedItemAccess;
	}
	
	@Override
	public int[] getSlotsForFace(Direction face)
	{
		return sidedItemAccess.getSlotsForFace(face);
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction from)
	{
		return isItemValid(slot, stack) && sidedItemAccess.canPlaceItemThroughFace(slot, from);
	}
	
	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction from)
	{
		return sidedItemAccess.canTakeItemThroughFace(slot, from);
	}
}
