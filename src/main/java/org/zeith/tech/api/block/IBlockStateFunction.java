package org.zeith.tech.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface IBlockStateFunction
{
	BlockState test(BlockState state, Level getter, BlockPos pos);
}
