package org.zeith.tech.compat._base.abils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

@FunctionalInterface
public interface IFacadeObtainer
{
	Optional<BlockState> getFacadeFromItem(ItemStack stack);
}