package org.zeith.tech.core.client.renderer.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.tech.api.item.tooltip.TooltipImage;

public class ClientTooltipImage
		implements ClientTooltipComponent
{
	protected final UV image;
	protected final int w, h;
	
	public ClientTooltipImage(TooltipImage image)
	{
		this.image = image.uv();
		this.w = image.width();
		this.h = image.height();
	}
	
	@Override
	public int getHeight()
	{
		return w;
	}
	
	@Override
	public int getWidth(Font font)
	{
		return h;
	}
	
	@Override
	public void renderImage(Font font, int x, int y, PoseStack pose, ItemRenderer renderer, int z)
	{
		image.render(pose, x, y - 2, w, h);
	}
}
