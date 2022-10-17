package org.zeith.tech.modules.processing.blocks.waste_processor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

import java.util.List;

public class GuiWasteProcessor
		extends GuiBaseMachine<ContainerWasteProcessor>
{
	public GuiWasteProcessor(ContainerWasteProcessor container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		setSize(176, 172);
		this.titleLabelY -= 2;
		this.inventoryLabelY += 8;
	}
	
	Rect2i rect;
	
	@Override
	public List<Rect2i> getExtraAreas()
	{
		if(rect != null)
			return List.of(rect);
		return List.of();
	}
	
	@Override
	protected void init()
	{
		super.init();
		rect = new Rect2i(leftPos + 176, topPos, 26, 86);
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		WidgetAPI.drawPowerBarOverlay(this, pose, 9 + leftPos, 13 + topPos, menu.tile.energyStored.getInt(), mouseX, mouseY);
		pose.popPose();
		
		return false;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		partialTime = minecraft.getPartialTick();
		
		menu.tile.input_a.setFluid(menu.tile.tank1.getClientAverage(partialTime));
		menu.tile.input_b.setFluid(menu.tile.tank2.getClientAverage(partialTime));
		menu.tile.output_a.setFluid(menu.tile.tank3.getClientAverage(partialTime));
		menu.tile.output_b.setFluid(menu.tile.tank4.getClientAverage(partialTime));
		
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/processing/gui/waste_processor.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, 202, 172);
		
		var tile = menu.tile;
		
		if(tile._maxProgress > 0 && tile._progress > 0)
		{
			float prog = tile._progress / (float) tile._maxProgress;
			RenderUtils.drawTexturedModalRect(pose, leftPos + 103, topPos + 38, 0, 172, 22 * prog, 16);
		}
		
		WidgetAPI.drawPowerBar(pose, leftPos + 9, topPos + 13, tile.energy.getFillRate());
	}
}