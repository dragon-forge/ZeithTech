package org.zeith.tech.api.block.multiblock.base;

import com.mojang.datafixers.util.Function3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.zeith.hammerlib.util.java.consumers.Consumer3;
import org.zeith.hammerlib.util.java.consumers.Consumer4;

import java.util.List;
import java.util.function.Predicate;

public record MultiBlockMetadata<DATA>(
		Function3<Level, BlockPos, List<MultiBlockFormer.VisibleBlockPos>, DATA> dataGen,
		Predicate<DATA> dataValidator,
		Consumer4<Level, BlockPos, Direction, DATA> originBuilder
)
{
	public static MultiBlockMetadata<Object> noData(Consumer3<Level, BlockPos, Direction> originBuilder)
	{
		return new MultiBlockMetadata<>(
				(level, blockPos, visibleBlockPos) -> blockPos,
				obj -> true,
				(a, b, c, d) -> originBuilder.accept(a, b, c)
		);
	}
}