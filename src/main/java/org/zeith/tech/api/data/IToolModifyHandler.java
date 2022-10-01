package org.zeith.tech.api.data;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IToolModifyHandler
{
	IToolModifyHandler NONE = ctx -> null;
	
	@Nullable BlockState getToolModifiedState(ToolModifyContext context);
	
	default IToolModifyHandler or(IToolModifyHandler other)
	{
		return ctx ->
		{
			BlockState res = getToolModifiedState(ctx);
			if(res != null) return res;
			return other.getToolModifiedState(ctx);
		};
	}
	
	static IToolModifyHandler axeStripping(Block targetBlock)
	{
		return ctx ->
		{
			if(ToolActions.AXE_STRIP == ctx.toolAction())
				return targetBlock.defaultBlockState();
			return null;
		};
	}
	
	record ToolModifyContext(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate)
	{
	}
}