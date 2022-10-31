package org.zeith.tech.api.block.multiblock.base;

import net.minecraft.core.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.util.java.consumers.Consumer3;
import org.zeith.hammerlib.util.java.tuples.*;
import org.zeith.hammerlib.util.mcf.RotationHelper;
import org.zeith.tech.api.block.IBlockStatePredicate;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MultiBlockFormer
{
	private static final RotationHelper.PivotRotation[][] ROTATION_MATRIX = {
			RotationHelper.PivotRotation.values(),
			{ RotationHelper.PivotRotation.Y_0 }
	};
	
	private final Consumer3<Level, BlockPos, Direction> placeMultiblock;
	private final List<MultiblockPart> components;
	private final BlockPos size;
	private final boolean isSymmetrical;
	
	public MultiBlockFormer(boolean isSymmetrical, Consumer3<Level, BlockPos, Direction> placeMultiblock, MultiblockPart... offsets)
	{
		this(isSymmetrical, placeMultiblock, List.of(offsets));
	}
	
	public MultiBlockFormer(boolean isSymmetrical, Consumer3<Level, BlockPos, Direction> placeMultiblock, MultiblockPart[]... offsets)
	{
		this(isSymmetrical, placeMultiblock, Stream.of(offsets).flatMap(Arrays::stream).toList());
	}
	
	public MultiBlockFormer(boolean isSymmetrical, Consumer3<Level, BlockPos, Direction> placeMultiblock, Collection<MultiblockPart> offsets)
	{
		this.isSymmetrical = isSymmetrical;
		this.placeMultiblock = placeMultiblock;
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
	
	public void placeMultiBlock(Level level, BlockPos origin, Direction direction)
	{
		var positions = getPositionsFrom(origin, direction);
		for(BlockPos rel : positions)
			if(!rel.equals(origin))
				TileMultiBlockPart.wrap(level, rel, origin);
		placeMultiblock.accept(level, origin, direction);
	}
	
	public void deform(Level level, BlockPos origin, Direction direction)
	{
		var positions = getPositionsFrom(origin, direction);
		for(BlockPos rel : positions) TileMultiBlockPart.unwrap(level, rel);
	}
	
	public List<BlockPos> getPositionsFrom(BlockPos origin, Direction horizontal)
	{
		var rotator = RotationHelper.getRotationFromHorizontal(horizontal);
		return components
				.stream()
				.map(MultiblockPart::a)
				.map(pos -> rotator.transform(BlockPos.ZERO, pos).offset(origin))
				.toList();
	}
	
	public Optional<Tuple1<BlockPos>> findCenter(Level level, BlockPos pos, RotationHelper.PivotRotation rot)
	{
		var identity = rot == RotationHelper.PivotRotation.Y_0 || rot == RotationHelper.PivotRotation.Y_180;
		
		int xSize = size.get(identity ? Direction.Axis.X : Direction.Axis.Z) / 2;
		int ySize = size.getY() / 2;
		int zSize = size.get(identity ? Direction.Axis.Z : Direction.Axis.X) / 2;
		
		return BlockPos.betweenClosedStream(pos.offset(-xSize, -ySize, -zSize), pos.offset(xSize, ySize, zSize))
				.map(BlockPos::immutable)
				.filter(off -> test(level, off, rot))
				.map(Tuples::immutable)
				.findFirst();
	}
	
	public Optional<Tuple2<BlockPos, Direction>> findCenterAndRotation(Level level, BlockPos pos, Direction preferred)
	{
		if(preferred != null)
		{
			var rot = RotationHelper.getRotationFromHorizontal(preferred);
			var found = findCenter(level, pos, rot);
			if(found.isPresent()) return found.map(t -> t.add(preferred));
		}
		
		for(var rot : ROTATION_MATRIX[isSymmetrical ? 1 : 0])
			if(rot.toHorizontal() != preferred)
			{
				var found = findCenter(level, pos, rot);
				if(found.isPresent()) return found.map(t -> t.add(rot.toHorizontal()));
			}
		
		return Optional.empty();
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
			var translated = rotator.transform(BlockPos.ZERO, part.a()).offset(origin);
			if(!part.test(origin, rotator, level, translated))
				return false;
		}
		return true;
	}
	
	public static MultiblockPart air(int x, int y, int z)
	{
		return ofBlocks(x, y, z, AirBlock.class::isInstance);
	}
	
	public static MultiblockPart ofBlock(int x, int y, int z, Block block)
	{
		return ofBlocks(x, y, z, b -> b == block);
	}
	
	public static MultiblockPart ofBlocks(int x, int y, int z, Predicate<Block> filter)
	{
		return ofBlockStates(x, y, z, state -> filter.test(state.getBlock()));
	}
	
	public static MultiblockPart ofBlockTag(int x, int y, int z, TagKey<Block> tag)
	{
		return ofBlockStates(x, y, z, state -> state.is(tag));
	}
	
	public static MultiblockPart ofBlockStates(int x, int y, int z, Predicate<BlockState> filter)
	{
		return new MultiblockPart(new BlockPos(x, y, z), (state, level, pos) -> filter.test(state));
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
			return b.test(TileMultiBlockPart.getPartState(level, pos), level, pos);
		}
		
		public MultiblockPart or(IBlockStatePredicate filter)
		{
			return new MultiblockPart(a(), b().or(filter));
		}
	}
}