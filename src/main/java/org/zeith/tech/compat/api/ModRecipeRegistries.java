package org.zeith.tech.compat.api;

import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.tech.api.recipes.IRecipeRegistries;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;

public class ModRecipeRegistries
		implements IRecipeRegistries
{
	@Override
	public NamespacedRecipeRegistry<RecipeHammering> hammering()
	{
		return RecipeRegistriesZT_Processing.HAMMERING;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeMachineAssembler> machineAssembly()
	{
		return RecipeRegistriesZT_Processing.MACHINE_ASSEBMLY;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeGrinding> grinding()
	{
		return RecipeRegistriesZT_Processing.GRINDING;
	}
}