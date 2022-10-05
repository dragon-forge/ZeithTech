package org.zeith.tech.compat;

import org.zeith.tech.api.recipes.IRecipeLifecycleListener;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;

public class BaseCompat
		implements IRecipeLifecycleListener
{
	@Override
	public <T extends IZeithTechRecipe> void onRecipeDeRegistered(T recipe)
	{
		IRecipeLifecycleListener.super.onRecipeDeRegistered(recipe);
	}
	
	@Override
	public <T extends IZeithTechRecipe> void onRecipeRegistered(T recipe)
	{
		IRecipeLifecycleListener.super.onRecipeRegistered(recipe);
	}
}