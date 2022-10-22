package org.zeith.tech.api.item.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record TooltipEnergyBar(float energy)
		implements TooltipComponent
{
	public TooltipEnergyBar(int energy, int maxEnergy)
	{
		this(energy / (float) maxEnergy);
	}
}