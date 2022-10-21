package org.zeith.tech.utils;

import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.api.inv.SimpleInventory;

import java.util.List;
import java.util.stream.IntStream;

public class InventoryHelper
{
	/**
	 * Checks/stores all stacks into the inventory. (Please make a simulated call first, to make sure all stacks can actually fit!)
	 * This method affects the original stack, if not simulated!
	 */
	public static boolean storeAllStacks(SimpleInventory inventory, IntStream slots, List<ItemStack> stacks, boolean simulate)
	{
		for(ItemStack sub : stacks)
			if(!storeStack(inventory, slots, sub, simulate))
				return false;
		return true;
	}
	
	/**
	 * Checks/stores an entire {@link ItemStack} into the inventory.
	 * This method affects the original stack, if not simulated!
	 * Returns if the stack was fully stored inside the given slot range of the inventory.
	 */
	public static boolean storeStack(SimpleInventory inventory, IntStream slots, ItemStack stack, boolean simulate)
	{
		if(simulate) stack = stack.copy();
		for(int i : slots.toArray())
		{
			var os = inventory.getItem(i);
			
			if(os.isEmpty())
			{
				if(!simulate) inventory.setItem(i, stack);
				return true;
			}
			
			int mss;
			if(ItemStackHelper.matchesIgnoreCount(os, stack) && os.getCount() < (mss = Math.min(os.getMaxStackSize(), stack.getMaxStackSize())))
			{
				int canAccept = Math.min(mss - os.getCount(), stack.getCount());
				stack.split(canAccept);
				if(!simulate) os.grow(canAccept);
			}
		}
		
		return stack.isEmpty();
	}
}