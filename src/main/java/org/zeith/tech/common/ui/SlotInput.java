package org.zeith.tech.common.ui;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotInput
		extends Slot
{
	public SlotInput(Container container, int id, int x, int y)
	{
		super(container, id, x, y);
	}
	
	@Override
	public boolean mayPlace(@NotNull ItemStack stack)
	{
		return container.canPlaceItem(getContainerSlot(), stack);
	}
}