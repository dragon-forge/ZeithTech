package org.zeith.tech.compat.patchouli;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.tech.api.block.multiblock.BlockStatePredicate;
import org.zeith.tech.api.block.multiblock.base.MultiBlockFormer;
import vazkii.patchouli.api.*;
import vazkii.patchouli.common.multiblock.SparseMultiblock;

import java.util.stream.Collectors;

public record PatchouliStateMatcher(BlockStatePredicate predicate)
		implements IStateMatcher
{
	@Override
	public BlockState getDisplayedState(long ticks)
	{
		var states = predicate.getAllStates();
		if(states.length > 0)
		{
			int idx = (int) (ticks / 20L % (long) states.length);
			return states[idx];
		}
		return Blocks.AIR.defaultBlockState();
	}
	
	@Override
	public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate()
	{
		return (level, pos, state) -> predicate.test(state, level, pos);
	}
	
	public static IMultiblock convertMultiblock(MultiBlockFormer<?> former)
	{
		int minX = -former.getComponents().stream().map(MultiBlockFormer.MultiblockPart::a).mapToInt(Vec3i::getX).min().getAsInt();
		int minY = -former.getComponents().stream().map(MultiBlockFormer.MultiblockPart::a).mapToInt(Vec3i::getY).min().getAsInt();
		int minZ = -former.getComponents().stream().map(MultiBlockFormer.MultiblockPart::a).mapToInt(Vec3i::getZ).min().getAsInt();
		
		var mb = new SparseMultiblock(
				former.getComponents()
						.stream()
						.map(part -> Tuples.immutable(part.a().offset(minX, minY, minZ), new PatchouliStateMatcher(part.b())))
						.collect(Collectors.toMap(Tuple2::a, Tuple2::b))
		);
		
		mb.setOffset(minX, minY, minZ);
		mb.setViewOffset(minX, minY - 1, minZ);
		mb.setSymmetrical(former.isSymmetrical());
		
		return mb;
	}
}