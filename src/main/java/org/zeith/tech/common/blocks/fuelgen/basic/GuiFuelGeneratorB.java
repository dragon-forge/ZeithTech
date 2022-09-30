package org.zeith.tech.common.blocks.fuelgen.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.client.gui.WidgetAPI;
import org.zeith.tech.common.blocks.base.machine.GuiBaseMachine;

public class GuiFuelGeneratorB
		extends GuiBaseMachine<ContainerFuelGeneratorB>
{
	public GuiFuelGeneratorB(ContainerFuelGeneratorB container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		
		setSize(176, 166);

//		this.titleLabelX += 15;
//		this.inventoryLabelX = 8;
//		this.inventoryLabelY = this.imageHeight - 92;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/gui/single_slot.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		var tile = menu.tile;
		
		WidgetAPI.drawPowerBar(pose, leftPos + 135, topPos + 8, tile.energy.getFillRate());
		
		float burnTime = tile.fuelTicksLeft.getFloat();
		int totalBurnTime = tile.fuelTicksTotal.getInt();
		
		WidgetAPI.drawFuelBar(pose, leftPos + 81, topPos + 29, burnTime > 0 && totalBurnTime > 0 ? burnTime / totalBurnTime : 0F);
	}
}
