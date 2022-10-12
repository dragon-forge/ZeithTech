package org.zeith.tech.modules.shared.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.client.render.FluidRendererHelper;
import org.zeith.hammerlib.client.utils.*;
import org.zeith.tech.core.ZeithTech;

import javax.annotation.Nullable;
import java.util.*;

public class WidgetAPI
{
	public static void bind()
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/gui/widgets.png");
	}
	
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
	
	public static void drawFluidBarOverlay(Screen screen, PoseStack pose, int x, int y, IFluidTank tank, boolean showCapacity, int mouseX, int mouseY)
	{
		if(mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 66)
		{
			renderSlotHighlight(pose, x + 1, y + 1, 16, 64, 0);
			
			var player = Minecraft.getInstance().player;
			if(player != null && player.containerMenu != null && !player.containerMenu.getCarried().isEmpty())
				return;
			
			var fluid = tank.getFluid();
			
			List<Component> drawables = new ArrayList<>();
			
			if(!fluid.isEmpty())
			{
				drawables.add(fluid.getDisplayName());
				
				drawables.add((showCapacity
						? Component.literal(I18n.get("info.zeithtech.fluid_capped", tank.getFluidAmount(), tank.getCapacity()))
						: Component.literal(I18n.get("info.zeithtech.fluid_uncapped", tank.getFluidAmount())))
						.withStyle(ChatFormatting.GRAY));
				
				if(screen.getMinecraft().options.advancedItemTooltips)
					drawables.add(Component.literal(ForgeRegistries.FLUID_TYPES.get().getKey(fluid.getFluid().getFluidType()).toString())
							.withStyle(ChatFormatting.DARK_GRAY));
			} else
				drawables.add(Component.translatable("info.zeithtech.empty"));
			
			screen.renderTooltip(pose, drawables,
					Optional.empty(), mouseX, mouseY);
		}
	}
	
	public static void drawFluidBar(PoseStack pose, int x, int y, IFluidTank tank)
	{
		bind();
		RenderUtils.drawTexturedModalRect(pose, x, y, 0, 66, 18, 66); // draw the base
		
		var fluid = tank.getFluid();
		float full = tank.getFluidAmount() / (float) tank.getCapacity();
		FluidRendererHelper.renderFluidInGui(pose, fluid, FluidTextureType.STILL, full, x + 1, y + 1, 16, 64);
		
		bind();
		RenderUtils.drawTexturedModalRect(pose, x, y, 18, 66, 18, 66); // draw glass
	}
	
	public static void drawSprite(PoseStack pose, float xCoord, float yCoord, @Nullable TextureAtlasSprite textureSprite, float widthIn, float heightIn, float yFull)
	{
		Matrix4f pose4f = pose.last().pose();
		float minX = textureSprite == null ? 0 : textureSprite.getU(0);
		float minY = textureSprite == null ? 0 : textureSprite.getV(16 - yFull);
		float maxX = textureSprite == null ? 1 : textureSprite.getU(16);
		float maxY = textureSprite == null ? 1 : textureSprite.getV(16);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuilder();
		vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		vertexbuffer.vertex(pose4f, xCoord, yCoord + heightIn, 0).uv(minX, maxY).endVertex();
		vertexbuffer.vertex(pose4f, xCoord + widthIn, yCoord + heightIn, 0).uv(maxX, maxY).endVertex();
		vertexbuffer.vertex(pose4f, xCoord + widthIn, yCoord, 0).uv(maxX, minY).endVertex();
		vertexbuffer.vertex(pose4f, xCoord, yCoord, 0).uv(minX, minY).endVertex();
		tessellator.end();
	}
	
	public static void drawPowerBar(PoseStack pose, int x, int y, float full)
	{
		bind();
		
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
		bind();
		
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