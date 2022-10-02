package org.zeith.tech.api.tile;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.LogicalSide;

public interface IHammerable
{
	boolean onHammerLeftClicked(ItemStack hammerStack, LogicalSide side, Direction face, Player player, InteractionHand hand, BlockHitResult vec);
}