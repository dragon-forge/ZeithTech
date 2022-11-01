package org.zeith.tech.api.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.api.block.IBlockStatePredicate;
import org.zeith.tech.api.utils.LazyValue;

import java.util.List;
import java.util.stream.Stream;

public record BlockStatePredicate(IBlockStatePredicate predicate, LazyValue<BlockState[]> allStates)
		implements IBlockStatePredicate
{
	@Override
	public boolean test(BlockState state, BlockGetter getter, BlockPos pos)
	{
		return predicate.test(state, getter, pos);
	}
	
	public BlockState[] getAllStates()
	{
		return allStates.getValue();
	}
	
	public BlockStatePredicate or(BlockStatePredicate other)
	{
		return new BlockStatePredicate(
				predicate().or(other.predicate()),
				LazyValue.of(() ->
						Stream.concat(Stream.of(getAllStates()), Stream.of(getAllStates()))
								.distinct()
								.toArray(BlockState[]::new)
				)
		);
	}
	
	public static BlockStatePredicate block(Block block)
	{
		return blockList(block);
	}
	
	public static BlockStatePredicate blockList(Block... blocks)
	{
		return blockList(List.of(blocks));
	}
	
	public static BlockStatePredicate blockList(List<Block> blocks)
	{
		return new BlockStatePredicate(
				(state, getter, pos) -> blocks.contains(state.getBlock()),
				LazyValue.of(() -> blocks.stream().flatMap(b -> b.getStateDefinition().getPossibleStates().stream()).toArray(BlockState[]::new))
		);
	}
}