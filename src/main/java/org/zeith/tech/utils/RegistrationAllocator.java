package org.zeith.tech.utils;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.mixins.AddRecipesAccessor;

public class RegistrationAllocator
{
	public static ResourceLocation findFreeLocation(ReloadRecipeRegistryEvent.AddRecipes<?> event, ResourceLocation rl)
	{
		var registry = ((AddRecipesAccessor) event).getRegistry();
		if(registry.getRecipe(Cast.cast(rl)) == null) return rl;
		int lastIdx = 1;
		while(true)
		{
			rl = new ResourceLocation(rl.getNamespace(), rl.getPath() + "_" + (lastIdx++));
			if(registry.getRecipe(Cast.cast(rl)) == null) return rl;
		}
	}
}