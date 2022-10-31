package org.zeith.tech.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface IBlockStatePredicate
{
	boolean test(BlockState state, BlockGetter getter, BlockPos pos);
	
	default IBlockStatePredicate or(IBlockStatePredicate other)
	{
		return (state, getter, pos) -> test(state, getter, pos) || other.test(state, getter, pos);
	}
}