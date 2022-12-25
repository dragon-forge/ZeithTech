package org.zeith.tech.api.block;

import net.minecraft.world.level.block.state.BlockState;

/**
 * An interface for blocks that represent farmland.
 */
public interface IFarmlandBlock
{
	/**
	 * Returns the maximum moist level for this farmland block.
	 *
	 * @return The maximum moist level for this farmland block.
	 */
	int getMaxMoistLevel();
	
	/**
	 * Returns the moist level of the given block state.
	 *
	 * @param state
	 * 		The block state to get the moist level for.
	 *
	 * @return The moist level of the given block state.
	 */
	int getMoistLevel(BlockState state);
	
	/**
	 * Returns a new block state with the given moist level.
	 *
	 * @param state
	 * 		The block state to set the moist level for.
	 * @param moistLevel
	 * 		The moist level to set.
	 *
	 * @return A new block state with the given moist level.
	 */
	BlockState withMoistLevel(BlockState state, int moistLevel);
}