package org.zeith.tech.api.tile.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.zeith.tech.api.block.multiblock.base.MultiBlockFormer;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;

public interface IMultiblockTile
{
	Direction getMultiblockDirection();
	
	MultiBlockFormer getFormer();
	
	boolean isMultiblockValid();
	
	default BlockEntity selfMBT()
	{
		return (BlockEntity) this;
	}
	
	default InteractionResult useFromPart(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, TileMultiBlockPart partTile)
	{
		return selfMBT().getBlockState().use(level, player, hand, hit);
	}
}