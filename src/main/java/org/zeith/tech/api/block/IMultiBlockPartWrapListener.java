package org.zeith.tech.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;

public interface IMultiBlockPartWrapListener
{
	BlockState formToPart(TileMultiBlockPart part, Level level, BlockPos pos, BlockState originState);
	
	BlockState deformFromPart(TileMultiBlockPart part, Level level, BlockPos pos, BlockState formedState);
}