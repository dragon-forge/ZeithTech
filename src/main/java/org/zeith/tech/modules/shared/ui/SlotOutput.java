package org.zeith.tech.modules.shared.ui;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotOutput
		extends Slot
{
	public SlotOutput(Container container, int id, int x, int y)
	{
		super(container, id, x, y);
	}
	
	@Override
	public boolean mayPlace(@NotNull ItemStack stack)
	{
		return false;
	}
}