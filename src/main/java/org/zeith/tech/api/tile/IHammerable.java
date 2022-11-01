package org.zeith.tech.api.tile;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.LogicalSide;

public interface IHammerable
{
	boolean onHammerLeftClicked(ItemStack hammerStack, LogicalSide side, Direction face, Player player, InteractionHand hand, BlockHitResult vec);
	
	default SoundEvent getHammeredSound(ItemStack hammerStack, LogicalSide side, Direction face, Player player, InteractionHand hand, BlockHitResult vec)
	{
		return SoundEvents.ANVIL_PLACE;
	}
}