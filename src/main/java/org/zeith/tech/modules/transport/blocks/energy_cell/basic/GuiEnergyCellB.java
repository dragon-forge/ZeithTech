package org.zeith.tech.modules.transport.blocks.energy_cell.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

public class GuiEnergyCellB
		extends GuiBaseMachine<ContainerEnergyCellB>
{
	public GuiEnergyCellB(ContainerEnergyCellB container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		imageWidth = 176;
		imageHeight = 166;
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		WidgetAPI.drawPowerBarOverlay(this, pose, leftPos + 98, topPos + 10, menu.tile.energy, mouseX, mouseY);
		pose.popPose();
		
		return false;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/transport/gui/energy_cell.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		WidgetAPI.drawPowerBar(pose, leftPos + 98, topPos + 10, menu.tile.energy.getFillRate());
	}
}