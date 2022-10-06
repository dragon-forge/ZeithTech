package org.zeith.tech.utils;

import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.Consumer;

public record LegacyEventBus(IEventBus modBus, IEventBus forgeBus)
{
	public <T extends Event> void addListener(Class<T> eventType, Consumer<T> consumer)
	{
		if(isModEvent(eventType))
			modBus.addListener(consumer);
		else
			forgeBus.addListener(consumer);
	}
	
	public <T extends GenericEvent<? extends F>, F> void addGenericListener(Class<T> eventType, Class<F> genericClassFilter, Consumer<T> consumer)
	{
		if(isModEvent(eventType))
			modBus.addGenericListener(genericClassFilter, consumer);
		else
			forgeBus.addGenericListener(genericClassFilter, consumer);
	}
	
	public static boolean isModEvent(Class<? extends Event> type)
	{
		return IModBusEvent.class.isAssignableFrom(type);
	}
}