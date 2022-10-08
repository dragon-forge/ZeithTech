package org.zeith.tech.api.recipes;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.hammerlib.util.java.Cast;

public class RegistrationAllocator
{
	public static ResourceLocation findFreeLocation(ReloadRecipeRegistryEvent.AddRecipes<?> event, ResourceLocation rl)
	{
		var registry = event.getRegistry();
		if(registry.getRecipe(Cast.cast(rl)) == null) return rl;
		int lastIdx = 1;
		while(true)
		{
			rl = new ResourceLocation(rl.getNamespace(), rl.getPath() + "_" + (lastIdx++));
			if(registry.getRecipe(Cast.cast(rl)) == null) return rl;
		}
	}
}