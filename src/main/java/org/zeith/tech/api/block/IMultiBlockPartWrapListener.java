package org.zeith.tech.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;

/**
 * An interface for listening to multi-block part wrapping events.
 */
public interface IMultiBlockPartWrapListener
{
	/**
	 * Called when a multi-block part is being wrapped into a multi-block structure.
	 *
	 * @param part
	 * 		The multi-block part being wrapped.
	 * @param level
	 * 		The level in which the wrapping is occurring.
	 * @param pos
	 * 		The position of the multi-block part.
	 * @param originState
	 * 		The original state of the multi-block part.
	 *
	 * @return The new state of the multi-block part after wrapping.
	 */
	BlockState formToPart(TileMultiBlockPart part, Level level, BlockPos pos, BlockState originState);
	
	/**
	 * Called when a multi-block part is being unwrapped from a multi-block structure.
	 *
	 * @param part
	 * 		The multi-block part being unwrapped.
	 * @param level
	 * 		The level in which the unwrapping is occurring.
	 * @param pos
	 * 		The position of the multi-block part.
	 * @param formedState
	 * 		The current state of the multi-block part within the multi-block structure.
	 *
	 * @return The new state of the multi-block part after unwrapping.
	 */
	BlockState deformFromPart(TileMultiBlockPart part, Level level, BlockPos pos, BlockState formedState);
}