package org.zeith.tech.api.misc;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class RecursionGuard
		implements AutoCloseable
{
	private final AtomicBoolean guardian;
	
	public RecursionGuard(AtomicBoolean guardian)
	{
		this.guardian = guardian;
	}
	
	@Nullable
	public <T> T call(Supplier<T> src)
	{
		if(!guardian.getAndSet(true))
			return src.get();
		return null;
	}
	
	public <T> T callOrDefault(Supplier<T> src, T fallback)
	{
		if(!guardian.getAndSet(true))
			return src.get();
		return fallback;
	}
	
	@Override
	public void close()
	{
		guardian.set(false);
	}
}