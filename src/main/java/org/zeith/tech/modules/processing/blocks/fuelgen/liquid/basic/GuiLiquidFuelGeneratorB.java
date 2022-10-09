package org.zeith.tech.modules.processing.blocks.fuelgen.liquid.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.IAdvancedGui;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

import java.awt.*;

@IAdvancedGui.ApplyToJEI
public class GuiLiquidFuelGeneratorB
		extends GuiBaseMachine<ContainerLiquidFuelGeneratorB>
		implements IAdvancedGui<GuiLiquidFuelGeneratorB>
{
	public GuiLiquidFuelGeneratorB(ContainerLiquidFuelGeneratorB container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		setSize(176, 166);
		this.titleLabelX += 16;
		this.inventoryLabelX += 16;
	}
	
	Rectangle fluidRect = new Rectangle(101, 14, 18, 66);
	
	@Override
	public Object getIngredientUnderMouse(double mouseX, double mouseY)
	{
		mouseX -= leftPos;
		mouseY -= topPos;
		if(fluidRect.contains(mouseX, mouseY))
			return menu.tile.storage.getFluid();
		return null;
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		WidgetAPI.drawPowerBarOverlay(this, pose, 7 + leftPos, 8 + topPos, menu.tile.energyStored.getInt(), mouseX, mouseY);
		WidgetAPI.drawFluidBarOverlay(this, pose, fluidRect.x + leftPos, fluidRect.y + topPos, menu.tile.storage, true, mouseX, mouseY);
		pose.popPose();
		
		return false;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		menu.tile.storage.setFluid(menu.tile.tankSmooth.getClientAverage(minecraft.getPartialTick()));
		
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/processing/gui/liquid_fuel_generator.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		var tile = menu.tile;
		
		WidgetAPI.drawFluidBar(pose, leftPos + fluidRect.x, topPos + fluidRect.y, tile.storage);
		
		WidgetAPI.drawPowerBar(pose, leftPos + 7, topPos + 8, tile.energy.getFillRate());
		
		float burnTime = tile.fuelTicksLeft.getFloat();
		int totalBurnTime = tile.fuelTicksTotal.getInt();
		
		WidgetAPI.drawFuelBar(pose, leftPos + 84, topPos + 36, burnTime > 0 && totalBurnTime > 0 ? burnTime / totalBurnTime : 0F);
	}
}