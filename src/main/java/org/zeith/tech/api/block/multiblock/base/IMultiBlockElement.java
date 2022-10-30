package org.zeith.tech.api.block.multiblock.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;

public interface IMultiBlockElement
{
	default InteractionResult useAsPart(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, TileMultiBlockPart partTile)
	{
		return InteractionResult.PASS;
	}
}