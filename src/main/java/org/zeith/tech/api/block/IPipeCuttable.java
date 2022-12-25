package org.zeith.tech.api.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

/**
 * An interface for blocks that can be cut by players with {@link org.zeith.tech.modules.transport.items.ItemPipeCutter}.
 */
public interface IPipeCuttable
{
	/**
	 * Returns the bounding box for the given connection in the given block state.
	 *
	 * @param state
	 * 		The block state.
	 * @param to
	 * 		The direction of the connection.
	 *
	 * @return The bounding box for the connection.
	 */
	VoxelShape getConnectionBoundary(BlockState state, Direction to);
	
	/**
	 * Returns the direction of the pipe part that was clicked by the player, if any.
	 *
	 * @param state
	 * 		The block state.
	 * @param ctx
	 * 		The context in which the block was clicked.
	 *
	 * @return The direction of the clicked pipe part, or {@code null} if no pipe part was clicked.
	 */
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
	
	/**
	 * Attempts to cut the pipe at the clicked location.
	 *
	 * @param state
	 * 		The block state.
	 * @param context
	 * 		The context in which the block was clicked.
	 *
	 * @return {@code true} if the pipe was successfully cut, {@code false} otherwise.
	 */
	default boolean performCut(BlockState state, UseOnContext context)
	{
		var dir = getClickedPipePart(state, context).orElse(null);
		return dir != null && performCut(state, context, dir);
	}
	
	/**
	 * Attempts to cut the pipe at the given location.
	 *
	 * @param state The block state.
	 * @param context The context in which the block was clicked.
	 * @param cutPart The direction of the pipe part to be cut.
	 * @return {@code true} if the pipe was successfully cut, {@code false} otherwise.
	 */
	boolean performCut(BlockState state, UseOnContext context, Direction cutPart);
}