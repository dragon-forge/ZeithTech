package org.zeith.tech.api.item.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class TooltipEnergyBar
		implements TooltipComponent
{
	public final float energy;
	
	public TooltipEnergyBar(float energy)
	{
		this.energy = energy;
	}
	
	public TooltipEnergyBar(int energy, int maxEnergy)
	{
		this.energy = energy / (float) maxEnergy;
	}
}