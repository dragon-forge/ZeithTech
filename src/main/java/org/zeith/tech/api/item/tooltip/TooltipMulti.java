package org.zeith.tech.api.item.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TooltipMulti
		implements TooltipComponent
{
	public final List<TooltipComponent> children;
	
	public TooltipMulti(TooltipComponent... children)
	{
		this.children = List.of(children);
	}
	
	public static Optional<TooltipComponent> create(Stream<TooltipComponent> stream)
	{
		var comp = stream.toArray(TooltipComponent[]::new);
		return comp.length == 0 ? Optional.empty() : Optional.of(comp.length == 1 ? comp[0] : new TooltipMulti(comp));
	}
}