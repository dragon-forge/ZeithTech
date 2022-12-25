package org.zeith.tech.api.block.multiblock.base;

import com.mojang.datafixers.util.Function3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.zeith.hammerlib.util.java.consumers.Consumer3;
import org.zeith.hammerlib.util.java.consumers.Consumer4;

import java.util.List;
import java.util.function.Predicate;

/**
 * A class that holds metadata for multi-block structures.
 *
 * @param <DATA>
 * 		The type of data associated with the multi-block structure.
 * @param dataGen
 * 		A function that generates the data for the multi-block structure based on the given level, origin position, and visible block positions.
 * @param dataValidator
 * 		A predicate that determines whether the given data is valid for the multi-block structure.
 * @param originBuilder
 * 		A consumer that builds the origin block of the multi-block structure at the given level, position, and orientation, using the given data.
 */
public record MultiBlockMetadata<DATA>(
		Function3<Level, BlockPos, List<MultiBlockFormer.VisibleBlockPos>, DATA> dataGen,
		Predicate<DATA> dataValidator,
		Consumer4<Level, BlockPos, Direction, DATA> originBuilder
)
{
	/**
	 * Creates a new {@code MultiBlockMetadata} instance with no data, and the given origin builder.
	 *
	 * @param originBuilder
	 * 		The origin builder for the multi-block structure.
	 *
	 * @return A new {@code MultiBlockMetadata} instance.
	 */
	public static MultiBlockMetadata<Object> noData(Consumer3<Level, BlockPos, Direction> originBuilder)
	{
		return new MultiBlockMetadata<>(
				(level, blockPos, visibleBlockPos) -> blockPos,
				obj -> true,
				(a, b, c, d) -> originBuilder.accept(a, b, c)
		);
	}
}