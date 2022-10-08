package org.zeith.tech.modules.processing.proxy;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.tech.utils.LegacyEventBus;

public class ClientProcessingProxyZT
		extends CommonProcessingProxyZT
{
	@Override
	public void subEvents(LegacyEventBus modBus)
	{
		modBus.addListener(FMLClientSetupEvent.class, this::clientSetup);
	}
	
	private void clientSetup(FMLClientSetupEvent e)
	{
	}
}