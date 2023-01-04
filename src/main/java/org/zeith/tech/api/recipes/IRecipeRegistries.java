package org.zeith.tech.api.recipes;

import org.jetbrains.annotations.ApiStatus;
import org.zeith.hammerlib.api.crafting.INameableRecipe;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.ITieredRecipe;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.compat._base.abils.IRecipeLifecycleListener;

import java.util.List;

public interface IRecipeRegistries
{
	NamespacedRecipeRegistry<RecipeHammering> hammering();
	
	NamespacedRecipeRegistry<RecipeMachineAssembler> machineAssembly();
	
	NamespacedRecipeRegistry<RecipeGrinding> grinding();
	
	NamespacedRecipeRegistry<RecipeSawmill> sawmill();
	
	NamespacedRecipeRegistry<RecipeFluidCentrifuge> fluidCentrifuge();
	
	NamespacedRecipeRegistry<RecipeLiquidFuel> liquidFuel();
	
	NamespacedRecipeRegistry<RecipeWasteProcessor> wasteProcessing();
	
	NamespacedRecipeRegistry<RecipeBlastFurnace> blastFurnace();
	
	<T extends ITieredRecipe & INameableRecipe> List<T> getRecipesUpToTier(NamespacedRecipeRegistry<T> registry, TechTier tier);
	
	@ApiStatus.Internal
	IRecipeLifecycleListener getRecipeLifecycleListener();
}