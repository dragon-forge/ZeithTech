package org.zeith.tech.api.block.multiblock.base;

import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.hammerlib.util.mcf.RotationHelper;
import org.zeith.tech.api.block.IBlockStatePredicate;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MultiBlockFormer
{
	private static final RotationHelper.PivotRotation[][] ROTATION_MATRIX = {
			RotationHelper.PivotRotation.values(),
			{ RotationHelper.PivotRotation.Y_0 }
	};
	
	private final List<MultiblockPart> components;
	private final BlockPos size;
	private final boolean isSymmetrical;
	
	public MultiBlockFormer(boolean isSymmetrical, MultiblockPart... offsets)
	{
		this(isSymmetrical, List.of(offsets));
	}
	
	public MultiBlockFormer(boolean isSymmetrical, Collection<MultiblockPart> offsets)
	{
		this.isSymmetrical = isSymmetrical;
		this.components = List.copyOf(offsets);
		
		var minPos = new BlockPos.MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		var maxPos = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		
		for(MultiblockPart offset : offsets)
		{
			var rel = offset.a();
			
			minPos.setX(Math.min(minPos.getX(), rel.getX()));
			minPos.setY(Math.min(minPos.getY(), rel.getY()));
			minPos.setZ(Math.min(minPos.getZ(), rel.getZ()));
			
			maxPos.setX(Math.max(maxPos.getX(), rel.getX()));
			maxPos.setY(Math.max(maxPos.getY(), rel.getY()));
			maxPos.setZ(Math.max(maxPos.getZ(), rel.getZ()));
		}
		
		BlockPos minOffset = minPos.immutable();
		BlockPos maxOffset = maxPos.immutable();
		
		this.size = new BlockPos(
				maxOffset.getX() - minOffset.getX() + 1,
				maxOffset.getY() - minOffset.getY() + 1,
				maxOffset.getZ() - minOffset.getZ() + 1
		);
	}
	
	public List<BlockPos> getPositionsFrom(BlockPos origin, Direction horizontal)
	{
		var rotator = RotationHelper.getRotationFromHorizontal(horizontal);
		return components
				.stream()
				.map(MultiblockPart::a)
				.map(pos -> rotator.transform(origin, pos))
				.toList();
	}
	
	public Optional<Tuple2<BlockPos, Direction>> findCenterAndRotation(Level level, BlockPos pos)
	{
		for(var rot : ROTATION_MATRIX[isSymmetrical ? 1 : 0])
		{
			var identity = rot == RotationHelper.PivotRotation.Y_0 || rot == RotationHelper.PivotRotation.Y_180;
			
			int xSize = size.get(identity ? Direction.Axis.X : Direction.Axis.Z);
			int ySize = size.getY();
			int zSize = size.get(identity ? Direction.Axis.Z : Direction.Axis.X);
			
			var match =
					BlockPos.betweenClosedStream(pos.offset(-xSize, -ySize, -zSize), pos.offset(xSize, ySize, zSize))
							.map(BlockPos::immutable)
							.filter(off -> test(level, off, rot))
							.map(off -> Tuples.immutable(off, rot.toHorizontal()))
							.findFirst();
			
			if(match.isPresent()) return match;
		}
		
		return Optional.empty();
	}
	
	public Stream<Tuple2<BlockPos, Direction>> findCenterAndRotationAsStream(Level level, BlockPos pos)
	{
		return findCenterAndRotation(level, pos).stream();
	}
	
	public Vec3i getSize()
	{
		return size;
	}
	
	public boolean test(Level level, BlockPos origin, Direction horizontal)
	{
		return test(level, origin, RotationHelper.getRotationFromHorizontal(horizontal));
	}
	
	public boolean test(Level level, BlockPos origin, RotationHelper.PivotRotation rotator)
	{
		if(rotator == null) rotator = RotationHelper.PivotRotation.Y_0;
		for(var part : components)
		{
			var translated = rotator.transform(origin, part.a());
			if(!part.test(origin, rotator, level, translated)) return false;
		}
		return true;
	}
	
	public static MultiblockPart ofBlocks(BlockPos offset, Predicate<Block> filter)
	{
		return ofBlockStates(offset, state -> filter.test(state.getBlock()));
	}
	
	public static MultiblockPart ofBlockStates(BlockPos offset, Predicate<BlockState> filter)
	{
		return new MultiblockPart(offset, (state, level, pos) -> filter.test(state));
	}
	
	public static class MultiblockPart
			extends Tuple2<BlockPos, IBlockStatePredicate>
	{
		public MultiblockPart(BlockPos blockPos, IBlockStatePredicate filter)
		{
			super(blockPos, filter);
		}
		
		public boolean test(BlockPos origin, RotationHelper.PivotRotation rotation, Level level, BlockPos pos)
		{
			return b.test(level.getBlockState(pos), level, pos);
		}
	}
}