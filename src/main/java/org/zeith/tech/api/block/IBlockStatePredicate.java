package org.zeith.tech.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A functional interface for testing a block state for a certain condition.
 */
@FunctionalInterface
public interface IBlockStatePredicate
{
	/**
	 * Tests the given block state for the specified condition.
	 *
	 * @param state
	 * 		The block state to test.
	 * @param getter
	 * 		The block getter to use for obtaining additional block states.
	 * @param pos
	 * 		The position of the block.
	 *
	 * @return {@code true} if the block state satisfies the specified condition, {@code false} otherwise.
	 */
	boolean test(BlockState state, BlockGetter getter, BlockPos pos);
	
	/**
	 * Returns a new {@code IBlockStatePredicate} that represents a logical OR of this predicate and another.
	 * When evaluating the resulting predicate, if this predicate is {@code true}, then the other predicate is not evaluated.
	 *
	 * @param other
	 * 		The other predicate to OR with this one.
	 *
	 * @return A new {@code IBlockStatePredicate} that represents the logical OR of this predicate and the other predicate.
	 */
	default IBlockStatePredicate or(IBlockStatePredicate other)
	{
		return (state, getter, pos) -> test(state, getter, pos) || other.test(state, getter, pos);
	}
}