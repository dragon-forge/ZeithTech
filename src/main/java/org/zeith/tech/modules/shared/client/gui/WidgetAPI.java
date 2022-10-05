package org.zeith.tech.modules.shared.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;

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
	
	public static void drawPowerBarOverlay(Screen screen, PoseStack pose, int x, int y, int displayFE, int mouseX, int mouseY)
	{
		if(mouseX >= x && mouseY >= y && mouseX < x + 13 && mouseY < y + 66)
		{
			renderSlotHighlight(pose, x + 1, y + 1, 11, 64, 0);
			
			var player = Minecraft.getInstance().player;
			if(player != null && player.containerMenu != null && !player.containerMenu.getCarried().isEmpty())
				return;
			
			screen.renderTooltip(pose, Component.literal(I18n.get("info.zeithtech.fe", displayFE)), mouseX, mouseY);
		}
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
	
	public static void renderSlotHighlight(PoseStack pose, int x, int y, int width, int height, int blitOffset)
	{
		renderSlotHighlight(pose, x, y, width, height, blitOffset, -2130706433);
	}
	
	public static void renderSlotHighlight(PoseStack pose, int x, int y, int width, int height, int blitOffset, int slotColor)
	{
		RenderSystem.disableDepthTest();
		RenderSystem.colorMask(true, true, true, false);
		fillGradient(pose, x, y, x + width, y + height, slotColor, slotColor, blitOffset);
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.enableDepthTest();
	}
	
	protected static void fillGradient(PoseStack pose, int x1, int y1, int x2, int y2, int color1, int color2, int blitOffset)
	{
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		fillGradient(pose.last().pose(), bufferbuilder, x1, y1, x2, y2, blitOffset, color1, color2);
		tesselator.end();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}
	
	protected static void fillGradient(Matrix4f pose, BufferBuilder buf, int x1, int y1, int x2, int y2, int z, int color1, int color2)
	{
		float f = (float) (color1 >> 24 & 255) / 255.0F;
		float f1 = (float) (color1 >> 16 & 255) / 255.0F;
		float f2 = (float) (color1 >> 8 & 255) / 255.0F;
		float f3 = (float) (color1 & 255) / 255.0F;
		
		float f4 = (float) (color2 >> 24 & 255) / 255.0F;
		float f5 = (float) (color2 >> 16 & 255) / 255.0F;
		float f6 = (float) (color2 >> 8 & 255) / 255.0F;
		float f7 = (float) (color2 & 255) / 255.0F;
		
		buf.vertex(pose, (float) x2, (float) y1, (float) z).color(f1, f2, f3, f).endVertex();
		buf.vertex(pose, (float) x1, (float) y1, (float) z).color(f1, f2, f3, f).endVertex();
		buf.vertex(pose, (float) x1, (float) y2, (float) z).color(f5, f6, f7, f4).endVertex();
		buf.vertex(pose, (float) x2, (float) y2, (float) z).color(f5, f6, f7, f4).endVertex();
	}
}