package org.zeith.tech.modules.processing.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.client.utils.RenderUtils;
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
	}
}