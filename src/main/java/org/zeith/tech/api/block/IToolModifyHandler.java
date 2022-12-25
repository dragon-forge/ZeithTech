package org.zeith.tech.api.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for handling tool modification of blocks.
 */
@FunctionalInterface
public interface IToolModifyHandler
{
	/**
	 * A constant {@code IToolModifyHandler} instance that does not modify the block state.
	 */
	IToolModifyHandler NONE = ctx -> null;
	
	/**
	 * Gets the modified block state after the tool has been used on the block.
	 *
	 * @param context
	 * 		The context in which the tool was used.
	 *
	 * @return The modified block state, or {@code null} if the block state was not modified.
	 */
	@Nullable BlockState getToolModifiedState(ToolModifyContext context);
	
	/**
	 * Returns a new {@code IToolModifyHandler} instance that first attempts to get the modified block state from
	 * this instance, and if that fails, attempts to get the modified block state from the given handler.
	 *
	 * @param other
	 * 		The other {@code IToolModifyHandler} instance to use as a fallback.
	 *
	 * @return The new {@code IToolModifyHandler} instance.
	 */
	default IToolModifyHandler or(IToolModifyHandler other)
	{
		return ctx ->
		{
			BlockState res = getToolModifiedState(ctx);
			if(res != null) return res;
			return other.getToolModifiedState(ctx);
		};
	}
	
	/**
	 * Returns an {@code IToolModifyHandler} that strips the target block when an axe is used.
	 *
	 * @param targetBlock
	 * 		The target block to be stripped.
	 *
	 * @return An {@code IToolModifyHandler} that strips the target block when an axe is used.
	 */
	static IToolModifyHandler axeStripping(Block targetBlock)
	{
		return ctx ->
		{
			if(ToolActions.AXE_STRIP == ctx.toolAction())
				return targetBlock.defaultBlockState();
			return null;
		};
	}
	
	/**
	 * Represents a context in which a tool is being used to modify a block.
	 *
	 * @param state
	 * 		The current state of the block being modified.
	 * @param context
	 * 		The use-on context of the tool.
	 * @param toolAction
	 * 		The action being performed by the tool.
	 * @param simulate
	 * 		Whether the action is being simulated or not.
	 */
	record ToolModifyContext(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate)
	{
	}
}