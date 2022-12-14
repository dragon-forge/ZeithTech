package org.zeith.tech.api.block.multiblock.base;

import net.minecraft.core.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.util.java.tuples.*;
import org.zeith.hammerlib.util.mcf.RotationHelper;
import org.zeith.tech.api.block.multiblock.BlockStatePredicate;
import org.zeith.tech.api.utils.LazyValue;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A utility class for creating multi-block structures.
 *
 * @param <DATA>
 * 		The type of data to be associated with the multi-block structure.
 */
public class MultiBlockFormer<DATA>
{
	private static final RotationHelper.PivotRotation[][] ROTATION_MATRIX = {
			RotationHelper.PivotRotation.values(),
			{ RotationHelper.PivotRotation.Y_0 }
	};
	
	private final MultiBlockMetadata<DATA> metadata;
	private final List<MultiblockPart> components;
	private final BlockPos size;
	private final boolean isSymmetrical;
	
	/**
	 * Creates a new {@code MultiBlockFormer} instance with the given metadata, component offsets, and symmetry flag.
	 *
	 * @param isSymmetrical
	 * 		Whether the multi-block structure is symmetrical or not.
	 * @param metadata
	 * 		The metadata to be associated with the multi-block structure.
	 * @param offsets
	 * 		The components of the multi-block structure.
	 */
	public MultiBlockFormer(boolean isSymmetrical, MultiBlockMetadata<DATA> metadata, MultiblockPart... offsets)
	{
		this(isSymmetrical, metadata, List.of(offsets));
	}
	
	/**
	 * Creates a new {@code MultiBlockFormer} instance with the given metadata, component offsets, and symmetry flag.
	 *
	 * @param isSymmetrical
	 * 		Whether the multi-block structure is symmetrical or not.
	 * @param metadata
	 * 		The metadata to be associated with the multi-block structure.
	 * @param offsets
	 * 		The components of the multi-block structure.
	 */
	public MultiBlockFormer(boolean isSymmetrical, MultiBlockMetadata<DATA> metadata, MultiblockPart[]... offsets)
	{
		this(isSymmetrical, metadata, Stream.of(offsets).flatMap(Arrays::stream).toList());
	}
	
	/**
	 * Creates a new {@code MultiBlockFormer} instance with the given metadata, component offsets, and symmetry flag.
	 *
	 * @param isSymmetrical
	 * 		Whether the multi-block structure is symmetrical or not.
	 * @param metadata
	 * 		The metadata to be associated with the multi-block structure.
	 * @param offsets
	 * 		The components of the multi-block structure.
	 */
	public MultiBlockFormer(boolean isSymmetrical, MultiBlockMetadata<DATA> metadata, Collection<MultiblockPart> offsets)
	{
		this.isSymmetrical = isSymmetrical;
		this.metadata = metadata;
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
	
	/**
	 * Returns the components of the multi-block structure.
	 *
	 * @return The components of the multi-block structure.
	 */
	public List<MultiblockPart> getComponents()
	{
		return components;
	}
	
	/**
	 * Places a multi-block structure at the given origin with the given orientation and data.
	 *
	 * @param level
	 * 		The level in which the multi-block structure will be placed.
	 * @param origin
	 * 		The origin position of the multi-block structure.
	 * @param direction
	 * 		The orientation of the multi-block structure.
	 * @param data
	 * 		The data to be associated with the multi-block structure.
	 */
	public void placeMultiBlock(Level level, BlockPos origin, Direction direction, DATA data)
	{
		var positions = getPositionsFrom(origin, direction);
		for(VisibleBlockPos rel : positions)
			if(!rel.equals(origin))
				TileMultiBlockPart.wrap(level, rel, origin, rel.isVisible);
		metadata.originBuilder().accept(level, origin, direction, data);
	}
	
	/**
	 * Deforms the multi-block structure at the given origin with the given orientation.
	 * This removes any tile entities associated with the multi-block structure and restores the original blocks.
	 *
	 * @param level
	 * 		The level in which the multi-block structure is located.
	 * @param origin
	 * 		The origin position of the multi-block structure.
	 * @param direction
	 * 		The orientation of the multi-block structure.
	 */
	public void deform(Level level, BlockPos origin, Direction direction)
	{
		var positions = getPositionsFrom(origin, direction);
		for(BlockPos rel : positions) TileMultiBlockPart.unwrap(level, rel);
	}
	
	/**
	 * Returns the positions of the blocks in the multi-block structure relative to the given origin and orientation.
	 *
	 * @param origin
	 * 		The origin position of the multi-block structure.
	 * @param horizontal
	 * 		The horizontal orientation of the multi-block structure.
	 *
	 * @return The positions of the blocks in the multi-block structure.
	 */
	public List<VisibleBlockPos> getPositionsFrom(BlockPos origin, Direction horizontal)
	{
		var rotator = RotationHelper.getRotationFromHorizontal(horizontal);
		return components
				.stream()
				.map(MultiblockPart::a)
				.map(pos -> new VisibleBlockPos(rotator.transform(BlockPos.ZERO, pos).offset(origin), pos.isVisible))
				.toList();
	}
	
	/**
	 * Finds the center position of the multi-block structure within the given level at the given position,
	 * with the given rotation applied.
	 *
	 * @param level
	 * 		The level to search within.
	 * @param pos
	 * 		The position to start searching from.
	 * @param rot
	 * 		The rotation to apply to the multi-block structure.
	 *
	 * @return The center position of the multi-block structure, or {@code Optional.empty()} if the center could not be found.
	 */
	public Optional<Tuple2<DATA, BlockPos>> findCenter(Level level, BlockPos pos, RotationHelper.PivotRotation rot)
	{
		var identity = rot == RotationHelper.PivotRotation.Y_0 || rot == RotationHelper.PivotRotation.Y_180;
		
		int xSize = size.get(identity ? Direction.Axis.X : Direction.Axis.Z) / 2;
		int ySize = size.getY() / 2;
		int zSize = size.get(identity ? Direction.Axis.Z : Direction.Axis.X) / 2;
		
		return BlockPos.betweenClosedStream(pos.offset(-xSize, -ySize, -zSize), pos.offset(xSize, ySize, zSize))
				.map(BlockPos::immutable)
				.filter(off -> test(level, off, rot))
				.map(off -> Tuples.immutable(metadata.dataGen().apply(level, off, getPositionsFrom(off, rot.toHorizontal())), off))
				.filter(tup -> metadata.dataValidator().test(tup.a()))
				.findFirst();
	}
	
	/**
	 * Finds the center position and orientation of the multi-block structure within the given level,
	 * starting from the given position and using the given preferred orientation as a hint.
	 *
	 * @param level
	 * 		The level to search in.
	 * @param pos
	 * 		The starting position for the search.
	 * @param preferred
	 * 		The preferred orientation to use as a hint.
	 *
	 * @return An optional tuple containing the center position, data, and orientation of the multi-block structure, or an empty optional if the center could not be found.
	 */
	public Optional<Tuple3<DATA, BlockPos, Direction>> findCenterAndRotation(Level level, BlockPos pos, Direction preferred)
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
	
	/**
	 * Returns whether the multi-block structure is symmetrical or not.
	 *
	 * @return {@code true} if the multi-block structure is symmetrical, {@code false} otherwise.
	 */
	public boolean isSymmetrical()
	{
		return isSymmetrical;
	}
	
	/**
	 * Returns the size of the multi-block structure.
	 *
	 * @return The size of the multi-block structure.
	 */
	public Vec3i getSize()
	{
		return size;
	}
	
	/**
	 * Tests whether the multi-block structure can be placed at the given origin with the given orientation.
	 *
	 * @param level
	 * 		The level in which the multi-block structure will be placed.
	 * @param origin
	 * 		The origin position of the multi-block structure.
	 * @param horizontal
	 * 		The horizontal orientation of the multi-block structure.
	 *
	 * @return {@code true} if the multi-block structure can be placed, {@code false} otherwise.
	 */
	public boolean test(Level level, BlockPos origin, Direction horizontal)
	{
		return test(level, origin, RotationHelper.getRotationFromHorizontal(horizontal));
	}
	
	/**
	 * Tests whether the multi-block structure can be placed at the given origin with the given rotation.
	 *
	 * @param level
	 * 		The level in which the multi-block structure will be tested.
	 * @param origin
	 * 		The origin position of the multi-block structure.
	 * @param rotator
	 * 		The orientation of the multi-block structure.
	 *
	 * @return {@code true} if the multi-block structure can be placed, {@code false} otherwise.
	 */
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
		return ofBlocksInvisible(x, y, z, AirBlock.class::isInstance, LazyValue.of(() -> new Block[] {
				Blocks.AIR,
				Blocks.CAVE_AIR,
				Blocks.VOID_AIR
		}));
	}
	
	public static MultiblockPart ofBlock(int x, int y, int z, Block block)
	{
		return ofBlocks(x, y, z, b -> b == block, LazyValue.of(() -> new Block[] { block }));
	}
	
	public static MultiblockPart ofBlocks(int x, int y, int z, Predicate<Block> filter, LazyValue<Block[]> allBlocks)
	{
		return ofBlockStates(x, y, z,
				state -> filter.test(state.getBlock()),
				LazyValue.xmapFlat(allBlocks, BlockState.class, b -> b.getStateDefinition().getPossibleStates().stream())
		);
	}
	
	public static MultiblockPart ofBlockTag(int x, int y, int z, TagKey<Block> tag)
	{
		return ofBlockStates(x, y, z,
				state -> state.is(tag),
				LazyValue.of(() -> ForgeRegistries.BLOCKS.tags().getTag(tag).stream().flatMap(b -> b.getStateDefinition().getPossibleStates().stream()).toArray(BlockState[]::new))
		);
	}
	
	public static MultiblockPart ofBlockStates(int x, int y, int z, Predicate<BlockState> filter, LazyValue<BlockState[]> allStates)
	{
		return new MultiblockPart(new VisibleBlockPos(x, y, z, true), new BlockStatePredicate((state, level, pos) -> filter.test(state), allStates));
	}
	
	public static MultiblockPart ofBlockInvisible(int x, int y, int z, Block block)
	{
		return ofBlocksInvisible(x, y, z, b -> b == block, LazyValue.of(() -> new Block[] { block }));
	}
	
	public static MultiblockPart ofBlocksInvisible(int x, int y, int z, Predicate<Block> filter, LazyValue<Block[]> allBlocks)
	{
		return ofBlockStatesInvisible(x, y, z,
				state -> filter.test(state.getBlock()),
				LazyValue.xmapFlat(allBlocks, BlockState.class, b -> b.getStateDefinition().getPossibleStates().stream())
		);
	}
	
	public static MultiblockPart ofBlockTagInvisible(int x, int y, int z, TagKey<Block> tag)
	{
		return ofBlockStatesInvisible(x, y, z,
				state -> state.is(tag),
				LazyValue.of(() -> ForgeRegistries.BLOCKS.tags().getTag(tag).stream().flatMap(b -> b.getStateDefinition().getPossibleStates().stream()).toArray(BlockState[]::new))
		);
	}
	
	public static MultiblockPart ofBlockStatesInvisible(int x, int y, int z, Predicate<BlockState> filter, LazyValue<BlockState[]> allBlocks)
	{
		return new MultiblockPart(new VisibleBlockPos(x, y, z, false), new BlockStatePredicate((state, level, pos) -> filter.test(state), allBlocks));
	}
	
	public static class MultiblockPart
			extends Tuple2<VisibleBlockPos, BlockStatePredicate>
	{
		public MultiblockPart(VisibleBlockPos blockPos, BlockStatePredicate filter)
		{
			super(blockPos, filter);
		}
		
		public boolean test(BlockPos origin, RotationHelper.PivotRotation rotation, Level level, BlockPos pos)
		{
			return b.test(TileMultiBlockPart.getPartState(level, pos), level, pos);
		}
		
		public MultiblockPart or(BlockStatePredicate filter)
		{
			return new MultiblockPart(a(), b().or(filter));
		}
	}
	
	public static class VisibleBlockPos
			extends BlockPos
	{
		public final boolean isVisible;
		
		public VisibleBlockPos(int x, int y, int z, boolean isVisible)
		{
			super(x, y, z);
			this.isVisible = isVisible;
		}
		
		public VisibleBlockPos(double x, double y, double z, boolean isVisible)
		{
			super(x, y, z);
			this.isVisible = isVisible;
		}
		
		public VisibleBlockPos(Vec3 vec, boolean isVisible)
		{
			super(vec);
			this.isVisible = isVisible;
		}
		
		public VisibleBlockPos(Position pos, boolean isVisible)
		{
			super(pos);
			this.isVisible = isVisible;
		}
		
		public VisibleBlockPos(Vec3i vec, boolean isVisible)
		{
			super(vec);
			this.isVisible = isVisible;
		}
	}
}