package org.zeith.tech.modules.shared.proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.zeith.tech.modules.shared.init.BlocksZT;

public class ClientSharedProxyZT
		extends CommonSharedProxyZT
{
	@Override
	public void subEvents(IEventBus modBus)
	{
		modBus.addListener(this::registerBlockColors);
	}
	
	@Override
	public float getPartialTick()
	{
		return Minecraft.getInstance().getPartialTick();
	}
	
	private void registerBlockColors(RegisterColorHandlersEvent.Block e)
	{
		e.register((state, blockAndTintGetter, pos, layer) -> blockAndTintGetter != null && pos != null && layer != 0 ? layer : 0xFFFFFFFF,
				BlocksZT.AUXILIARY_IO_PORT
		);
	}
}