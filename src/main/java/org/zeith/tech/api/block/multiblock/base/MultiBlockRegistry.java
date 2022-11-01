package org.zeith.tech.api.block.multiblock.base;

import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class MultiBlockRegistry
{
	private static final List<MultiBlockFormer<?>> REGISTRY = new ArrayList<>();
	private static final Map<ResourceLocation, MultiBlockFormer<?>> NAMED_REGISTRY = new ConcurrentHashMap<>();
	
	public static <T extends MultiBlockFormer<?>> T register(ResourceLocation id, T former)
	{
		REGISTRY.remove(NAMED_REGISTRY.put(id, former));
		if(!REGISTRY.contains(former))
			REGISTRY.add(former);
		return former;
	}
	
	public static MultiBlockFormer<?> get(ResourceLocation key)
	{
		return NAMED_REGISTRY.get(key);
	}
	
	public static Stream<ResourceLocation> keys()
	{
		return NAMED_REGISTRY.keySet().stream();
	}
	
	public static Stream<MultiBlockFormer<?>> registered()
	{
		return REGISTRY.stream();
	}
}