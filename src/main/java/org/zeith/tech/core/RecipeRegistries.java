package org.zeith.tech.core;

import org.zeith.hammerlib.api.crafting.INameableRecipe;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.IRecipeLifecycleListener;
import org.zeith.tech.api.recipes.IRecipeRegistries;
import org.zeith.tech.api.recipes.base.ITieredRecipe;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.modules.shared.init.RecipeRegistriesZT;

import java.util.List;

class RecipeRegistries
		implements IRecipeRegistries, IRecipeLifecycleListener
{
	@Override
	public NamespacedRecipeRegistry<RecipeHammering> hammering()
	{
		return RecipeRegistriesZT.HAMMERING;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeMachineAssembler> machineAssembly()
	{
		return RecipeRegistriesZT.MACHINE_ASSEMBLY;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeGrinding> grinding()
	{
		return RecipeRegistriesZT.GRINDING;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeSawmill> sawmill()
	{
		return RecipeRegistriesZT.SAWMILL;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeFluidCentrifuge> fluidCentrifuge()
	{
		return RecipeRegistriesZT.FLUID_CENTRIFUGE;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeLiquidFuel> liquidFuel()
	{
		return RecipeRegistriesZT.LIQUID_FUEL;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeWasteProcessor> wasteProcessing()
	{
		return RecipeRegistriesZT.WASTE_PROCESSING;
	}
	
	@Override
	public NamespacedRecipeRegistry<RecipeBlastFurnace> blastFurnace()
	{
		return RecipeRegistriesZT.BLAST_FURNACE;
	}
	
	@Override
	public <T extends ITieredRecipe & INameableRecipe> List<T> getRecipesUpToTier(NamespacedRecipeRegistry<T> registry, TechTier tier)
	{
		return registry
				.getRecipes()
				.stream()
				.filter(t -> t.isTierGoodEnough(tier))
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