package org.zeith.tech.common.blocks.item_pipe;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class PipeItemHandler
		implements IItemHandler
{
	public final TileItemPipe pipe;
	public final Direction from;
	
	public PipeItemHandler(TileItemPipe pipe, Direction from)
	{
		this.pipe = pipe;
		this.from = from;
	}
	
	@Override
	public int getSlots()
	{
		return 1;
	}
	
	@Override
	public @NotNull ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
	{
		return pipe.insertItemIntoPipe(stack.copy(), from, simulate);
	}
	
	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}
	
	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack)
	{
		return true;
	}
}
