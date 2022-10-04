package org.zeith.tech.api.recipes.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.tech.api.enums.TechTier;

@FunctionalInterface
public interface IUnaryRecipeConstructor<R extends RecipeUnaryBase>
{
	R newInstance(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier);
}