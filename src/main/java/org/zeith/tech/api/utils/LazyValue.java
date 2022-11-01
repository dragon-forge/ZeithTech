package org.zeith.tech.api.utils;

import net.minecraftforge.common.util.NonNullSupplier;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.util.java.Cast;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class LazyValue<T>
{
	private final NonNullSupplier<T> supplier;
	private final Object lock = new Object();
	private Mutable<T> resolved;
	private static final @NotNull LazyValue<Void> EMPTY = new LazyValue<>(null);
	
	private List<LazyValue<?>> children = new ArrayList<>();
	
	private LazyValue(@Nullable NonNullSupplier<T> instanceSupplier)
	{
		this.supplier = instanceSupplier;
	}
	
	public static <T> LazyValue<T> of(final @Nullable NonNullSupplier<T> instanceSupplier)
	{
		return instanceSupplier == null ? empty() : new LazyValue<>(instanceSupplier);
	}
	
	public static <T> LazyValue<T> empty()
	{
		return EMPTY.cast();
	}
	
	public void reset()
	{
		resolved = null;
		children.forEach(LazyValue::reset);
	}
	
	public LazyValue<T> withChild(LazyValue<?> c)
	{
		if(!children.contains(c))
			children.add(c);
		return this;
	}
	
	public T getValue()
	{
		if(supplier == null) return null;
		
		if(resolved == null)
		{
			synchronized(lock)
			{
				// resolved == null: Double checked locking to prevent two threads from resolving
				
				if(resolved == null)
					resolved = new MutableObject<>(supplier.get());
			}
		}
		
		return resolved.getValue();
	}
	
	public <X> LazyValue<X> map(Function<T, X> mapper)
	{
		return of(() -> mapper.apply(getValue())).withChild(this);
	}
	
	public <X> LazyValue<X> cast()
	{
		return (LazyValue<X>) this;
	}
	
	public static <X, Y> LazyValue<Y[]> xmap(LazyValue<X[]> value, Class<Y> newType, Function<X, Y> mapper)
	{
		LazyValue<Y[]> res = of(() ->
				Arrays.stream(value.getValue()).map(mapper)
						.toArray(i -> Cast.cast(Array.newInstance(newType, i)))
		);
		
		return res.withChild(value);
	}
	
	public static <X, Y> LazyValue<Y[]> xmapFlat(LazyValue<X[]> value, Class<Y> newType, Function<X, Stream<Y>> mapper)
	{
		LazyValue<Y[]> res = of(() ->
				Arrays.stream(value.getValue()).flatMap(mapper)
						.toArray(i -> Cast.cast(Array.newInstance(newType, i)))
		);
		
		return res.withChild(value);
	}
}