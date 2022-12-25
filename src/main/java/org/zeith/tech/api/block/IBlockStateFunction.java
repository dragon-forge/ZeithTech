package org.zeith.tech.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A functional interface for testing a block state and potentially returning a modified version of it.
 */
@FunctionalInterface
public interface IBlockStateFunction
{
	/**
	 * Tests the given block state and potentially returns a modified version of it.
	 *
	 * @param state
	 * 		The block state to test.
	 * @param getter
	 * 		The level in which the block state is located.
	 * @param pos
	 * 		The position of the block state.
	 *
	 * @return The modified block state, or the original block state if it should not be modified.
	 */
	BlockState test(BlockState state, Level getter, BlockPos pos);
}
