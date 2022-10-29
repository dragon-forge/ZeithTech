package org.zeith.tech.api.item.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record TooltipStack(ItemStack stack)
		implements TooltipComponent
{
}