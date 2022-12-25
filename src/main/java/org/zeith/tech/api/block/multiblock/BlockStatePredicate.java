package org.zeith.tech.api.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.api.block.IBlockStatePredicate;
import org.zeith.tech.api.utils.LazyValue;

import java.util.List;
import java.util.stream.Stream;

/**
 * A utility class for testing whether a block state matches a given predicate.
 *
 * @see IBlockStatePredicate
 */
public record BlockStatePredicate(IBlockStatePredicate predicate, LazyValue<BlockState[]> allStates)
		implements IBlockStatePredicate
{
	/**
	 * Tests whether the given block state matches the predicate.
	 *
	 * @param state
	 * 		The block state to test.
	 * @param getter
	 * 		The block getter to use for testing.
	 * @param pos
	 * 		The position of the block state.
	 *
	 * @return {@code true} if the block state matches the predicate, {@code false} otherwise.
	 */
	@Override
	public boolean test(BlockState state, BlockGetter getter, BlockPos pos)
	{
		return predicate.test(state, getter, pos);
	}
	
	/**
	 * Returns all possible block states that match the predicate.
	 *
	 * @return All possible block states that match the predicate.
	 */
	public BlockState[] getAllStates()
	{
		return allStates.getValue();
	}
	
	/**
	 * Returns a new {@code BlockStatePredicate} that matches either this predicate or the given predicate.
	 *
	 * @param other
	 * 		The predicate to OR with this predicate.
	 *
	 * @return A new {@code BlockStatePredicate} that matches either this predicate or the given predicate.
	 */
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
	
	/**
	 * Returns a new {@code BlockStatePredicate} that matches the given block.
	 *
	 * @param block
	 * 		The block to match.
	 *
	 * @return A new {@code BlockStatePredicate} that matches the given block.
	 */
	public static BlockStatePredicate block(Block block)
	{
		return blockList(block);
	}
	
	/**
	 * Returns a new {@code BlockStatePredicate} that matches any of the given blocks.
	 *
	 * @param blocks
	 * 		The blocks to match.
	 *
	 * @return A new {@code BlockStatePredicate} that matches any of the given blocks.
	 */
	public static BlockStatePredicate blockList(Block... blocks)
	{
		return blockList(List.of(blocks));
	}
	
	/**
	 * Returns a new {@code BlockStatePredicate} that matches any of the given blocks.
	 *
	 * @param blocks
	 * 		The blocks to match.
	 *
	 * @return A new {@code BlockStatePredicate} that matches any of the given blocks.
	 */
	public static BlockStatePredicate blockList(List<Block> blocks)
	{
		return new BlockStatePredicate(
				(state, getter, pos) -> blocks.contains(state.getBlock()),
				LazyValue.of(() -> blocks.stream().flatMap(b -> b.getStateDefinition().getPossibleStates().stream()).toArray(BlockState[]::new))
		);
	}
}