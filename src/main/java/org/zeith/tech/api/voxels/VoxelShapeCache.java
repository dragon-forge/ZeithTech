package org.zeith.tech.api.voxels;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VoxelShapeCache
{
	private final Map<BlockState, VoxelShape> cache = new HashMap<>();
	private final Function<BlockState, VoxelShape> generator;
	
	public VoxelShapeCache(BiFunction<BlockState, VoxelShapeCacheBuilder, VoxelShape> generator)
	{
		this.generator = state -> generator.apply(state, VoxelShapeCacheBuilder.INSTANCE);
	}
	
	public void reset()
	{
		cache.clear();
	}
	
	public VoxelShape get(BlockState state)
	{
		return cache.computeIfAbsent(state, generator);
	}
}