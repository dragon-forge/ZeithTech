package org.zeith.tech.modules.shared.ui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotNonInteractable
		extends Slot
{
	public SlotNonInteractable(Container container, int id, int x, int y)
	{
		super(container, id, x, y);
	}
	
	@Override
	public boolean mayPickup(Player player)
	{
		return false;
	}
	
	@Override
	public boolean mayPlace(@NotNull ItemStack stack)
	{
		return false;
	}
}