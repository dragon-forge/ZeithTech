package org.zeith.tech.core;

import org.zeith.hammerlib.api.crafting.INameableRecipe;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.IRecipeLifecycleListener;
import org.zeith.tech.api.recipes.IRecipeRegistries;
import org.zeith.tech.api.recipes.base.ITieredRecipe;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;

import java.util.List;

class RecipeRegistries
		implements IRecipeRegistries, IRecipeLifecycleListener
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
	
	@Override
	public NamespacedRecipeRegistry<RecipeSawmill> sawmill()
	{
		return RecipeRegistriesZT_Processing.SAWMILL;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeFluidCentrifuge> fluidCentrifuge()
	{
		return RecipeRegistriesZT_Processing.FLUID_CENTRIFUGE;
	}
	
	@Override
	public <T extends ITieredRecipe & INameableRecipe> List<T> getRecipesUpToTier(NamespacedRecipeRegistry<T> registry, TechTier tier)
	{
		return registry
				.getRecipes()
				.stream()
				.filter(t -> t.isTierGoodEnough(TechTier.BASIC))
				.toList();
	}
	
	@Override
	public IRecipeLifecycleListener getRecipeLifecycleListener()
	{
		return this;
	}
	
	@Override
	public <T extends IZeithTechRecipe> void onRecipeRegistered(T recipe)
	{
		ZeithTech.forCompats(compat -> compat.onRecipeRegistered(recipe));
	}
	
	@Override
	public <T extends IZeithTechRecipe> void onRecipeDeRegistered(T recipe)
	{
		ZeithTech.forCompats(compat -> compat.onRecipeDeRegistered(recipe));
	}
}