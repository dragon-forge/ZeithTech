package org.zeith.tech.api.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IBurnableItem
{
	int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType);
	
	static IBurnableItem constantBurnTime(int time)
	{
		return (stack, type) -> time;
	}
}