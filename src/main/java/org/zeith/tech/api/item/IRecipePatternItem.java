package org.zeith.tech.api.item;

import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.api.crafting.INameableRecipe;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;

public interface IRecipePatternItem
{
	NamespacedRecipeRegistry<?> getProvidedRecipeRegistry(ItemStack stack);
	
	INameableRecipe getProvidedRecipe(ItemStack stack);
	
	<T extends INameableRecipe> ItemStack createEncoded(NamespacedRecipeRegistry<T> registry, T recipe);
}