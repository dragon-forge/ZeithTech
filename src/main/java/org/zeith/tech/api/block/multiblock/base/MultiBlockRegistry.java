package org.zeith.tech.api.block.multiblock.base;

import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * A registry for storing {@link MultiBlockFormer} instances.
 */
public class MultiBlockRegistry
{
	/**
	 * The list of registered {@link MultiBlockFormer} instances.
	 */
	private static final List<MultiBlockFormer<?>> REGISTRY = new ArrayList<>();
	
	/**
	 * A map of registered {@link MultiBlockFormer} instances, keyed by their resource location.
	 */
	private static final Map<ResourceLocation, MultiBlockFormer<?>> NAMED_REGISTRY = new ConcurrentHashMap<>();
	
	/**
	 * Registers the given {@link MultiBlockFormer} instance under the given resource location.
	 * If the resource location is already in use, the previous {@link MultiBlockFormer} instance is replaced.
	 *
	 * @param id
	 * 		The resource location to register the {@link MultiBlockFormer} instance under.
	 * @param former
	 * 		The {@link MultiBlockFormer} instance to register.
	 * @param <T>
	 * 		The type of the {@link MultiBlockFormer} instance.
	 *
	 * @return The registered {@link MultiBlockFormer} instance.
	 */
	public static <T extends MultiBlockFormer<?>> T register(ResourceLocation id, T former)
	{
		REGISTRY.remove(NAMED_REGISTRY.put(id, former));
		if(!REGISTRY.contains(former))
			REGISTRY.add(former);
		return former;
	}
	
	/**
	 * Returns the {@link MultiBlockFormer} instance registered under the given resource location.
	 *
	 * @param key
	 * 		The resource location to search for.
	 *
	 * @return The {@link MultiBlockFormer} instance registered under the given resource location, or {@code null} if none was found.
	 */
	public static MultiBlockFormer<?> get(ResourceLocation key)
	{
		return NAMED_REGISTRY.get(key);
	}
	
	/**
	 * Returns a stream of the resource locations of all registered {@link MultiBlockFormer} instances.
	 *
	 * @return A stream of the resource locations of all registered {@link MultiBlockFormer} instances.
	 */
	public static Stream<ResourceLocation> keys()
	{
		return NAMED_REGISTRY.keySet().stream();
	}
	
	/**
	 * Returns a stream of all registered {@link MultiBlockFormer} instances.
	 *
	 * @return A stream of all registered {@link MultiBlockFormer} instances.
	 */
	public static Stream<MultiBlockFormer<?>> registered()
	{
		return REGISTRY.stream();
	}
}