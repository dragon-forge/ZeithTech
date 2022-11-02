package org.zeith.tech.modules.processing.proxy;

import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.util.colors.ColorHelper;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.modules.processing.client.renderer.item.ItemPropertyAlt;
import org.zeith.tech.modules.processing.init.FluidsZT_Processing;
import org.zeith.tech.modules.processing.init.ItemsZT_Processing;
import org.zeith.tech.modules.processing.items.redstone_control_tool.ContainerRedstoneControl;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;
import org.zeith.tech.utils.LegacyEventBus;

public class ClientProcessingProxyZT
		extends CommonProcessingProxyZT
{
	@Override
	public void subEvents(LegacyEventBus modBus)
	{
		modBus.addListener(FMLClientSetupEvent.class, this::clientSetup);
		modBus.addListener(RegisterGuiOverlaysEvent.class, this::registerOverlays);
		modBus.addListener(RegisterColorHandlersEvent.Item.class, this::registerItemColors);
	}
	
	private void clientSetup(FMLClientSetupEvent e)
	{
		ItemBlockRenderTypes.setRenderLayer(FluidsZT_Processing.SULFURIC_ACID.getSource(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidsZT_Processing.SULFURIC_ACID.getFlowing(), RenderType.translucent());
		
		ItemBlockRenderTypes.setRenderLayer(FluidsZT_Processing.DIESEL_FUEL.getSource(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidsZT_Processing.DIESEL_FUEL.getFlowing(), RenderType.translucent());
		
		ItemBlockRenderTypes.setRenderLayer(FluidsZT_Processing.GAS.getSource(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidsZT_Processing.GAS.getFlowing(), RenderType.translucent());
		
		MenuScreens.register(ContainerRedstoneControl.REDSTONE_CONTROL, (MenuScreens.ScreenConstructor) (ctr, inv, txt) -> Cast
				.optionally(ctr, IScreenContainer.class)
				.map(c -> c.openScreen(inv, txt))
				.orElse(null));
		
		ItemProperties.register(ItemsZT_Processing.SOC_PROGRAMMER, new ResourceLocation("active"),
				new ItemPropertyAlt(stack -> stack.getTag() != null && stack.getTag().contains("soc_programmer_target", Tag.TAG_STRING)));
	}
	
	private void registerItemColors(RegisterColorHandlersEvent.Item e)
	{
		e.register((stack, i) -> i > 0 ? ItemsZT_Processing.FARM_SOC.getBarColor(stack) : 0xFFFFFF, ItemsZT_Processing.FARM_SOC);
		e.register((stack, i) -> i > 0 ? ItemsZT_Processing.SOC_PROGRAMMER.getBarColor(stack) : 0xFFFFFF, ItemsZT_Processing.SOC_PROGRAMMER);
	}
	
	private void registerOverlays(RegisterGuiOverlaysEvent e)
	{
		var mc = Minecraft.getInstance();
		
		e.registerAboveAll("redstone_probe_suggestion", (gui, poseStack, partialTick, screenWidth, screenHeight) ->
		{
			BlockEntity be;
			if(mc.player != null
					&& !mc.options.hideGui
					&& (mc.player.getMainHandItem().is(ItemsZT_Processing.REDSTONE_CONTROL_TOOL) || mc.player.getOffhandItem().is(ItemsZT_Processing.REDSTONE_CONTROL_TOOL))
					&& mc.hitResult instanceof BlockHitResult res
					&& mc.level != null
					&& (be = mc.level.getBlockEntity(res.getBlockPos())) != null
					&& be.getCapability(ZeithTechCapabilities.REDSTONE_CONTROL).isPresent())
			{
				var wnd = mc.getWindow();
				
				WidgetAPI.bind();
				
				float scale = (float) wnd.getGuiScale();
				var w = wnd.getWidth() / scale;
				var h = wnd.getHeight() / scale;
				
				RenderUtils.drawTexturedModalRect(poseStack, (w - 9F) / 2, (h - 9F) / 2, 52, 0, 4, 4);
			}
		});
		
		e.registerAboveAll("farm_soc_programmer", (gui, poseStack, partialTick, screenWidth, screenHeight) ->
		{
			ItemStack programmer;
			if(mc.player != null
					&& !mc.options.hideGui
					&& ((programmer = mc.player.getMainHandItem()).is(ItemsZT_Processing.SOC_PROGRAMMER) || (programmer = mc.player.getOffhandItem()).is(ItemsZT_Processing.SOC_PROGRAMMER))
			)
			{
				var wnd = mc.getWindow();
				var algorithm = ItemsZT_Processing.SOC_PROGRAMMER.getAlgorithm(programmer);
				
				float scale = (float) wnd.getGuiScale();
				var w = wnd.getWidth() / scale;
				var h = wnd.getHeight() / scale;
				
				if(algorithm != null)
				{
					int x = (int) (w / 2 + 4);
					int y = (int) (h / 2 - 28);
					algorithm.getIcon().render(poseStack, x, y, 24, 24);
					var font = gui.getFont();
					
					int mainColor = algorithm.getColor();
					float luma = (float) ColorHelper.luma(mainColor);
					
					int outlineColor;
					
					if(luma > 0.5F) outlineColor = ColorHelper.multiply(mainColor, 2 / 4F);
					else
					{
						var multi = 4 / 2F;
						float r = ColorHelper.getRed(mainColor) * multi, g = ColorHelper.getGreen(mainColor) * multi, b = ColorHelper.getBlue(mainColor) * multi;
						if(r > 1 || g > 1 || b > 1)
						{
							float max = Math.max(r, Math.max(g, b));
							r /= max;
							g /= max;
							b /= max;
						}
						outlineColor = ColorHelper.packRGB(r, g, b);
					}
					
					MultiBufferSource.BufferSource src = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
					font.drawInBatch8xOutline(algorithm.getDisplayName().getVisualOrderText(), x + 26, y + (24 - font.lineHeight) / 2, mainColor, outlineColor, poseStack.last().pose(), src, 0xf000f0);
					src.endBatch();
				}
			}
		});
	}
}