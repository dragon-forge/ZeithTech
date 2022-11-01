package org.zeith.tech.modules.processing.blocks.farm;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

public class GuiFarm
		extends GuiBaseMachine<ContainerFarm>
{
	public GuiFarm(ContainerFarm container, Inventory playerInv, Component name)
	{
		super(container, playerInv, name);
		setSize(176, 174);
		inventoryLabelY += 10;
		titleLabelY -= 2;
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		WidgetAPI.drawPowerBarOverlay(this, pose, leftPos + 7, topPos + 14, menu.tile.energy, mouseX, mouseY);
		pose.popPose();
		
		return false;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		menu.tile.water.setFluid(menu.tile.tankSmooth.getClientAverage(minecraft.getPartialTick()));
		
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/processing/gui/farm.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		WidgetAPI.drawPowerBar(pose, leftPos + 7, topPos + 14, menu.tile.energy.getFillRate());
	}
}