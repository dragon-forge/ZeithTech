package org.zeith.tech.modules.shared.client.renderer.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.api.item.tooltip.TooltipStack;

public class ClientTooltipStack
		implements ClientTooltipComponent
{
	final ItemStack stack;
	
	public ClientTooltipStack(TooltipStack stack)
	{
		this.stack = stack.stack();
	}
	
	@Override
	public int getHeight()
	{
		return 16;
	}
	
	@Override
	public int getWidth(Font font)
	{
		return 18 + font.width(stack.getHoverName());
	}
	
	@Override
	public void renderImage(Font font, int x, int y, PoseStack pose, ItemRenderer renderer, int z)
	{
		RenderUtils.renderItemIntoGui(pose, stack, x, y - 1);
		font.draw(pose, stack.getHoverName(), x + 18, y - 1, 0xFFFFFF);
	}
}