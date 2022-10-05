package org.zeith.tech.modules.transport.proxy;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.modules.processing.client.renderer.item.ItemPropertyFunctionMultimeter;
import org.zeith.tech.modules.transport.container.ContainerMultimeter;
import org.zeith.tech.modules.transport.init.ItemsZT_Transport;

public class ClientTransportProxyZT
		extends CommonTransportProxyZT
{
	@Override
	public void subEvents(IEventBus modBus)
	{
		modBus.addListener(this::clientSetup);
	}
	
	private void clientSetup(FMLClientSetupEvent e)
	{
		MenuScreens.register(ContainerMultimeter.MULTIMETER_MT, (MenuScreens.ScreenConstructor) (ctr, inv, txt) -> Cast
				.optionally(ctr, IScreenContainer.class)
				.map(c -> c.openScreen(inv, txt))
				.orElse(null));
		
		ItemProperties.register(ItemsZT_Transport.MULTIMETER, new ResourceLocation("power"), new ItemPropertyFunctionMultimeter());
	}
}