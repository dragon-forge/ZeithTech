package org.zeith.tech.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.ZeithTech;

public class WidgetAPI
{
	public static void drawGhostItem(PoseStack pose, int x, int y, ItemStack stack)
	{
		var minecraft = Minecraft.getInstance();
		var itemrenderer = minecraft.getItemRenderer();
		minecraft.getItemRenderer().renderAndDecorateFakeItem(stack, x, y);
		RenderSystem.depthFunc(516);
		GuiComponent.fill(pose, x, y, x + 16, y + 16, 822083583);
		RenderSystem.depthFunc(515);
		itemrenderer.renderGuiItemDecorations(minecraft.font, stack, x, y);
	}
	
	public static void drawPowerBar(PoseStack pose, int x, int y, float full)
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/gui/widgets.png");
		
		RenderUtils.drawTexturedModalRect(pose, x, y, 0, 0, 13, 66);
		
		if(full > 0)
		{
			float fill64 = full * 64, fillI64 = 64 - fill64;
			RenderUtils.drawTexturedModalRect(pose, x + 1, y + 1 + fillI64, 27, 1 + fillI64, 11, fill64);
		}
		
		RenderUtils.drawTexturedModalRect(pose, x, y, 13, 0, 13, 66);
	}
	
	public static void drawFuelBar(PoseStack pose, int x, int y, float full)
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/gui/widgets.png");
		
		RenderUtils.drawTexturedModalRect(pose, x, y, 39, 0, 13, 13);
		
		if(full > 0)
		{
			float fill14 = 1 + full * 13, fillI14 = 14 - fill14;
			RenderUtils.drawTexturedModalRect(pose, x, y + fillI14, 39, 13 + fillI14, 14, fill14);
		}
	}
}