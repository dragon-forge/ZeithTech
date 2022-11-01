package org.zeith.tech.modules.processing.blocks.blast_furnace.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.util.java.Chars;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class GuiBlastFurnaceB
		extends GuiBaseMachine<ContainerBlastFurnaceB>
{
	public GuiBlastFurnaceB(ContainerBlastFurnaceB container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		setSize(176, 174);
		titleLabelY = 5;
		inventoryLabelY = 82;
	}
	
	protected final Rectangle fuelHover = new Rectangle();
	
	@Override
	protected void init()
	{
		super.init();
		fuelHover.setBounds(leftPos + 63, topPos + 64, 13, 13);
	}
	
	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		if(fuelHover.contains(mouseX, mouseY))
			renderTooltip(pose, List.of(
					Component.literal("%.01f%sC".formatted(menu.tile.temperature, Chars.DEGREE_SIGN)),
					Component.literal("F: %.01f%%".formatted(menu.tile.heatingMultiplier * 100)),
					Component.literal("C: %.01f%%".formatted(menu.tile.heatSaveMultiplier * 100))
			), Optional.empty(), mouseX, mouseY);
		pose.popPose();
		
		super.renderLabels(pose, mouseX, mouseY);
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		menu.tile.fuel.setFluid(menu.tile.tankSmooth.getClientAverage(minecraft.getPartialTick()));
		
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/processing/gui/blast_furnace/basic.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		var tile = menu.tile;
		
		if(tile._maxProgress > 0 && tile._progress > 0)
		{
			float prog = tile._progress / (float) tile._maxProgress;
			RenderUtils.drawTexturedModalRect(pose, leftPos + 88, topPos + 40, imageWidth, 0, 22 * prog, 16);
		}
		
		float burnTime = tile.burnTime;
		int totalBurnTime = tile.maxBurnTime;
		
		WidgetAPI.drawFuelBar(pose, leftPos + 63, topPos + 64, burnTime > 0 && totalBurnTime > 0 ? burnTime / totalBurnTime : 0F);
	}
}