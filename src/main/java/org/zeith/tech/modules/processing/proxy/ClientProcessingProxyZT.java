package org.zeith.tech.modules.processing.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.ZeithTechCapabilities;
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
	}
	
	private void clientSetup(FMLClientSetupEvent e)
	{
		MenuScreens.register(ContainerRedstoneControl.REDSTONE_CONTROL, (MenuScreens.ScreenConstructor) (ctr, inv, txt) -> Cast
				.optionally(ctr, IScreenContainer.class)
				.map(c -> c.openScreen(inv, txt))
				.orElse(null));
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