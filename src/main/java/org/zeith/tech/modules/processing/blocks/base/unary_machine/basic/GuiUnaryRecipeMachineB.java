package org.zeith.tech.modules.processing.blocks.base.unary_machine.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

public class GuiUnaryRecipeMachineB<C extends ContainerUnaryRecipeMachineB<?>>
		extends GuiBaseMachine<C>
{
	public static final ResourceLocation DEFAULT_MACHINE_GUI = new ResourceLocation(ZeithTech.MOD_ID, "textures/processing/gui/unary_recipe_machine/basic.png");
	
	protected ResourceLocation backgroundImage;
	
	public GuiUnaryRecipeMachineB(C container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		setSize(176, 166);
		this.titleLabelX += 16;
		this.inventoryLabelX += 16;
		
		backgroundImage = container.tile.getGuiCustomTexture();
		if(backgroundImage == null) backgroundImage = DEFAULT_MACHINE_GUI;
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
		FXUtils.bindTexture(backgroundImage);
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		var tile = menu.tile;
		
		if(tile._maxProgress > 0 && tile._progress > 0)
		{
			float prog = tile._progress / (float) tile._maxProgress;
			drawProgress(pose, prog);
		}
		
		WidgetAPI.drawPowerBar(pose, leftPos + 7, topPos + 8, tile.energy.getFillRate());
	}
	
	protected void drawProgress(PoseStack pose, float progress)
	{
		RenderUtils.drawTexturedModalRect(pose, leftPos + 72, topPos + 35, 176, 0, 22 * progress, 16);
	}
}