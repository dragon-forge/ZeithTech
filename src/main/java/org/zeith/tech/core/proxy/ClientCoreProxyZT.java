package org.zeith.tech.core.proxy;

import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.zeith.tech.api.item.tooltip.*;
import org.zeith.tech.core.client.renderer.tooltip.*;

public class ClientCoreProxyZT
		extends CommonCoreProxyZT
{
	@Override
	public void construct(IEventBus modBus)
	{
		super.construct(modBus);
		modBus.addListener(this::registerClientTooltips);
	}
	
	private void registerClientTooltips(RegisterClientTooltipComponentFactoriesEvent e)
	{
		e.register(TooltipStack.class, ClientTooltipStack::new);
		e.register(TooltipEnergyBar.class, ClientTooltipEnergy::new);
		e.register(TooltipImage.class, ClientTooltipImage::new);
	}
}