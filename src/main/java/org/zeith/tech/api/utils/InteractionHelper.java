package org.zeith.tech.api.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

public class InteractionHelper
{
	@Nullable
	public static BlockState getTilledState(ServerLevel level, Player player, BlockPos pos)
	{
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WOODEN_HOE));
		var dirtState = level.getBlockState(pos);
		return dirtState.getBlock().getToolModifiedState(dirtState, new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)), ToolActions.HOE_TILL, true);
	}
}