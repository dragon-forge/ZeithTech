package org.zeith.tech.modules.shared.client.renderer.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.zeith.tech.api.item.tooltip.TooltipMulti;

import java.util.List;

public class ClientTooltipMulti
		implements ClientTooltipComponent
{
	protected final List<ClientTooltipComponent> children;
	
	public ClientTooltipMulti(TooltipMulti multi)
	{
		children = multi.children.stream().map(ClientTooltipComponent::create).toList();
	}
	
	@Override
	public int getHeight()
	{
		return children.stream().mapToInt(ClientTooltipComponent::getHeight).sum();
	}
	
	@Override
	public int getWidth(Font font)
	{
		return children.stream().mapToInt(c -> c.getWidth(font)).max().orElse(0);
	}
	
	@Override
	public void renderImage(Font font, int x, int y, PoseStack pose, ItemRenderer renderer, int z)
	{
		for(ClientTooltipComponent child : children)
		{
			child.renderImage(font, x, y, pose, renderer, z);
			y += child.getHeight();
		}
	}
}