package org.zeith.tech.utils;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XYZMap<T>
{
	private final Map<Long, T> holder;
	
	public XYZMap(boolean concurrent)
	{
		this.holder = concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
	}
	
	public T get(BlockPos pos)
	{
		return holder.get(pos.asLong());
	}
	
	public T getOrDefault(BlockPos pos, T def)
	{
		return holder.getOrDefault(pos.asLong(), def);
	}
	
	public T get(int x, int y, int z)
	{
		return holder.get(BlockPos.asLong(x, y, z));
	}
	
	public T getOrDefault(int x, int y, int z, T def)
	{
		return holder.getOrDefault(BlockPos.asLong(x, y, z), def);
	}
	
	public T put(BlockPos pos, T value)
	{
		return holder.put(pos.asLong(), value);
	}
	
	public T put(int x, int y, int z, T value)
	{
		return holder.put(BlockPos.asLong(x, y, z), value);
	}
}