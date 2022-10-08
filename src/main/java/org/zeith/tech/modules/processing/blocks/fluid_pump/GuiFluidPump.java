package org.zeith.tech.modules.processing.blocks.fluid_pump;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.IAdvancedGui;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

@IAdvancedGui.ApplyToJEI
public class GuiFluidPump
		extends GuiBaseMachine<ContainerFluidPump>
		implements IAdvancedGui
{
	public GuiFluidPump(ContainerFluidPump container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		setSize(176, 166);
		this.titleLabelX += 16;
		this.inventoryLabelX += 16;
	}
	
	@Override
	public Object getIngredientUnderMouse(double mouseX, double mouseY)
	{
		mouseX -= leftPos;
		mouseY -= topPos;
		if(mouseX >= 97 && mouseY >= 10 && mouseX < 97 + 16 && mouseY < 10 + 66)
			return menu.tile.fluidTank.getFluid();
		return null;
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		WidgetAPI.drawPowerBarOverlay(this, pose, 7 + leftPos, 8 + topPos, menu.tile.energyStored.getInt(), mouseX, mouseY);
		WidgetAPI.drawFluidBarOverlay(this, pose, 97 + leftPos, 10 + topPos, menu.tile.fluidTank, true, mouseX, mouseY);
		pose.popPose();
		
		return false;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		menu.tile.fluidTank.setFluid(menu.tile.tankSmooth.getClientAverage(minecraft.getPartialTick()));
		
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/processing/gui/fluid_pump.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		var tile = menu.tile;
		
		WidgetAPI.drawPowerBar(pose, leftPos + 7, topPos + 8, tile.energy.getFillRate());
		WidgetAPI.drawFluidBar(pose, leftPos + 97, topPos + 10, tile.fluidTank);
		
		var output = BlocksZT_Processing.MINING_PIPE.asItem().getDefaultInstance();
		int x = leftPos + 62;
		int y = topPos + 27;
		WidgetAPI.drawGhostItem(pose, x, y, output);
		WidgetAPI.drawGhostItem(pose, x, y + 18, output);
	}
}