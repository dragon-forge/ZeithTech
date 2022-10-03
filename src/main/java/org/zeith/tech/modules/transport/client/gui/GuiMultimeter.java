package org.zeith.tech.modules.transport.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.util.colors.ColorHelper;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.modules.transport.container.ContainerMultimeter;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class GuiMultimeter
		extends ScreenWTFMojang<ContainerMultimeter>
{
	public GuiMultimeter(ContainerMultimeter container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		setSize(202, 166);
	}
	
	@Override
	protected void containerTick()
	{
		menu.containerTick();
		super.containerTick();
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/transport/gui/multimeter.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		pose.pushPose();
		pose.translate(leftPos + 8, topPos + 8, 0);
		var max = Stream.of(menu.graphs).flatMapToDouble(DoubleStream::of).max().orElse(1);
		renderGraph(pose, menu.graphs[2], menu.age, 0xFF_FF00FF, max); // transfer
		renderGraph(pose, menu.graphs[0], menu.age, 0xFF_FFFF00, max); // generation
		renderGraph(pose, menu.graphs[1], menu.age, 0xFF_00FFFF, max); // consumption
		pose.popPose();
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		int y = 9;
		int lineSpacing = font.lineHeight + 3;
		var lim = Math.max(1, menu.age);
		
		drawString(pose, font, "Generation: ", 140, y, 0xFF_FFFF00);
		drawString(pose, font, "%.2f".formatted(DoubleStream.of(menu.graphs[0]).limit(lim).average().orElse(0)), 140, y + lineSpacing * 1, 0xFF_FFFF00);
		
		drawString(pose, font, "Usage: ", 140, y + lineSpacing * 2, 0xFF_00FFFF);
		drawString(pose, font, "%.2f".formatted(DoubleStream.of(menu.graphs[1]).limit(lim).average().orElse(0)), 140, y + lineSpacing * 3, 0xFF_00FFFF);
		
		drawString(pose, font, "Transfer: ", 140, y + lineSpacing * 4, 0xFF_FF00FF);
		drawString(pose, font, "%.2f".formatted(DoubleStream.of(menu.graphs[2]).limit(lim).average().orElse(0)), 140, y + lineSpacing * 5, 0xFF_FF00FF);
		
		return true;
	}
	
	private void renderGraph(PoseStack pose, double[] graph, int limit, int color, double max)
	{
		float pt = minecraft.getPartialTick();
		var scale = (float) minecraft.getWindow().getGuiScale();
		
		int elems = Math.min(graph.length, limit);
		
		float pix = 1 / scale;
		
		double min = 0;
		
		Matrix4f pose4f = pose.last().pose();
		
		float r = ColorHelper.getRed(color),
				g = ColorHelper.getGreen(color),
				b = ColorHelper.getBlue(color),
				a = ColorHelper.getAlpha(color);
		
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
		
		float pixScaled = 130F / graph.length;
		
		for(int i = 1; i < elems; ++i)
		{
			double prevValue = (graph[i - 1] - min) / max; // [0; 1]
			double value = (graph[i] - min) / max; // [0; 1]
			
			float xo = (130 - (i - 1 + pt) * pixScaled), yo = (float) (70 - prevValue * 70);
			float x = (130 - (i + pt) * pixScaled), y = (float) (70 - value * 70);
			
			bufferbuilder.vertex(pose4f, xo, yo, 0).color(r, g, b, a).endVertex();
			bufferbuilder.vertex(pose4f, x, y, 0).color(r, g, b, a).endVertex();
		}
		
		BufferUploader.drawWithShader(bufferbuilder.end());
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}