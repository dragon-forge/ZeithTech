package org.zeith.tech.core;

import org.zeith.tech.utils.LegacyEventBus;

public interface IInternalCode
{
	default void construct(LegacyEventBus bus)
	{
	}
	
	default void enable()
	{
	}
}