package org.zeith.tech.api.item.multitool;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public record TooltipFlagMultiTool(TooltipFlag parent, ItemStack multiToolStack)
		implements TooltipFlag
{
	@Override
	public boolean isAdvanced()
	{
		return parent.isAdvanced();
	}
}