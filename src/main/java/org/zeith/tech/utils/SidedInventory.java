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
	public boolean canPlaceItemThroughFace(int p_19235_, ItemStack p_19236_, @Nullable Direction p_19237_)
	{
		return isItemValid(p_19235_, p_19236_) && sidedItemAccess.canPlaceItemThroughFace(p_19235_, p_19236_, p_19237_);
	}
	
	@Override
	public boolean canTakeItemThroughFace(int p_19239_, ItemStack p_19240_, Direction p_19241_)
	{
		return sidedItemAccess.canTakeItemThroughFace(p_19239_, p_19240_, p_19241_);
	}
}
