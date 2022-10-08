package org.zeith.tech.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.zeith.hammerlib.api.crafting.AbstractRecipeRegistry;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;

@Mixin(ReloadRecipeRegistryEvent.AddRecipes.class)
public interface AddRecipesAccessor
{
	@Accessor
	AbstractRecipeRegistry<?, ?, ?> getRegistry();
}
