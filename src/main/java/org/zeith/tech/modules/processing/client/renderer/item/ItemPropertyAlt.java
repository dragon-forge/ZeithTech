package org.zeith.tech.modules.processing.client.renderer.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public record ItemPropertyAlt(Predicate<ItemStack> isAlt)
		implements ClampedItemPropertyFunction
{
	@Override
	public float unclampedCall(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int i)
	{
		return isAlt.test(stack) ? 1 : 0;
	}
}