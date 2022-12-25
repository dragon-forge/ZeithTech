package org.zeith.tech.api.block.multiblock.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;

/**
 * An interface for blocks that can be part of a multi-block structure.
 */
public interface IMultiBlockElement
{
	/**
	 * Handles the interaction of a player with this multi-block element.
	 *
	 * @param state
	 * 		The current block state of this element.
	 * @param level
	 * 		The level in which this element is located.
	 * @param pos
	 * 		The position of this element.
	 * @param player
	 * 		The player interacting with this element.
	 * @param hand
	 * 		The hand being used by the player.
	 * @param hit
	 * 		The result of the player's ray trace.
	 * @param partTile
	 * 		The tile entity associated with this element, if present.
	 *
	 * @return The result of the interaction.
	 */
	default InteractionResult useAsPart(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, TileMultiBlockPart partTile)
	{
		return InteractionResult.PASS;
	}
}