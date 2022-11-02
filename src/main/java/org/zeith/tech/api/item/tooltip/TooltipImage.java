package org.zeith.tech.api.item.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.zeith.hammerlib.client.utils.UV;

public record TooltipImage(UV uv, int width, int height)
		implements TooltipComponent
{
}