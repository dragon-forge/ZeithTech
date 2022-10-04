package org.zeith.tech.api.recipes;

import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.tech.api.recipes.processing.*;

public interface IRecipeRegistries
{
	NamespacedRecipeRegistry<RecipeHammering> hammering();
	
	NamespacedRecipeRegistry<RecipeMachineAssembler> machineAssembly();
	
	NamespacedRecipeRegistry<RecipeGrinding> grinding();
}