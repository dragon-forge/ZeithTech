package org.zeith.tech.api.recipes;

import org.jetbrains.annotations.ApiStatus;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;

@ApiStatus.Internal
public interface IRecipeLifecycleListener
{
	default <T extends IZeithTechRecipe> void onRecipeRegistered(T recipe)
	{
	}
	
	default <T extends IZeithTechRecipe> void onRecipeDeRegistered(T recipe)
	{
	}
}