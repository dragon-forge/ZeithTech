package org.zeith.tech.core.client.renderer.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.zeith.tech.api.item.tooltip.TooltipEnergyBar;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

public class ClientTooltipEnergy
		implements ClientTooltipComponent
{
	final TooltipEnergyBar bar;
	
	public ClientTooltipEnergy(TooltipEnergyBar bar)
	{
		this.bar = bar;
	}
	
	@Override
	public int getHeight()
	{
		return 13;
	}
	
	@Override
	public int getWidth(Font font)
	{
		return 66;
	}
	
	@Override
	public void renderImage(Font font, int x, int y, PoseStack pose, ItemRenderer renderer, int z)
	{
		WidgetAPI.bind();
		pose.pushPose();
		pose.translate(x + 66, y - 2, 0);
		pose.mulPose(Axis.ZP.rotationDegrees(90));
		WidgetAPI.drawPowerBar(pose, 0, 0, bar.energy());
		pose.popPose();
	}
}