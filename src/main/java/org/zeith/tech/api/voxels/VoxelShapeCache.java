package org.zeith.tech.api.voxels;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VoxelShapeCache
{
	private final Map<BlockState, VoxelShape> cache = new ConcurrentHashMap<>();
	private final Function<BlockState, VoxelShape> generator;
	
	public VoxelShapeCache(Block block, BiFunction<BlockState, VoxelShapeCacheBuilder, VoxelShape> generator)
	{
		this.generator = state -> generator.apply(state, VoxelShapeCacheBuilder.INSTANCE);
		build(block);
	}
	
	public void reset()
	{
		cache.clear();
	}
	
	public void build(Block block)
	{
		for(BlockState possibleState : block.getStateDefinition().getPossibleStates())
			get(possibleState);
	}
	
	public VoxelShape get(BlockState state)
	{
		return cache.computeIfAbsent(state, generator);
	}
}