package org.zeith.tech.api.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public interface IPipeCuttable
{
	VoxelShape getConnectionBoundary(BlockState state, Direction to);
	
	default Optional<Direction> getClickedPipePart(BlockState state, UseOnContext ctx)
	{
		for(Direction dir : Direction.values())
		{
			var pos = ctx.getClickedPos();
			
			var bound = Optional.ofNullable(getConnectionBoundary(state, dir))
					.map(shape -> shape.move(pos.getX(), pos.getY(), pos.getZ()))
					.orElse(null);
			
			if(bound != null
					&& bound.toAabbs()
					.stream()
					.map(a -> a.inflate(0.000001))
					.anyMatch(a -> a.contains(ctx.getClickLocation()))
			) return Optional.ofNullable(dir);
		}
		
		return Optional.empty();
	}
	
	default boolean performCut(BlockState state, UseOnContext context)
	{
		var dir = getClickedPipePart(state, context).orElse(null);
		return dir != null && performCut(state, context, dir);
	}
	
	boolean performCut(BlockState state, UseOnContext context, Direction cutPart);
}