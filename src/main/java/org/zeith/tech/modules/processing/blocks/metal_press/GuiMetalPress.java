package org.zeith.tech.modules.processing.blocks.metal_press;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.GuiUnaryRecipeMachineB;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

public class GuiMetalPress
		extends GuiBaseMachine<ContainerMetalPress>
{
	public GuiMetalPress(ContainerMetalPress container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		setSize(176, 166);
		this.titleLabelX += 16;
		this.inventoryLabelX += 16;
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		WidgetAPI.drawPowerBarOverlay(this, pose, 7 + leftPos, 8 + topPos, menu.tile.energy, mouseX, mouseY);
		pose.popPose();
		
		return false;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(GuiUnaryRecipeMachineB.DEFAULT_MACHINE_GUI);
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		var tile = menu.tile;
		
		if(tile._maxProgress > 0 && tile._progress > 0)
		{
			float progress = Mth.lerp(partialTime, tile.prevProgress, tile.currentProgress) / (float) Math.max(tile._maxProgress, 1);
			RenderUtils.drawTexturedModalRect(pose, leftPos + 72, topPos + 35, 176, 0, 22 * progress, 16);
		}
		
		WidgetAPI.drawPowerBar(pose, leftPos + 7, topPos + 8, tile.energy.getFillRate());
	}
}