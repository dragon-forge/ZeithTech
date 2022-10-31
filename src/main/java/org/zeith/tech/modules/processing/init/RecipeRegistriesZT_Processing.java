package org.zeith.tech.modules.processing.init;

import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.hammerlib.api.crafting.RecipeRegistryFactory;
import org.zeith.hammerlib.api.crafting.itf.IRecipeReceiver;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.modules.processing.ProcessingModule;
import org.zeith.tech.modules.processing.recipes.*;

public interface RecipeRegistriesZT_Processing
{
	private static <R extends IZeithTechRecipe> IRecipeReceiver<R> broadcast()
	{
		return clientRecipe -> ZeithTechAPI.ifPresent(api -> api.getRecipeRegistries().getRecipeLifecycleListener().onRecipeRegistered(clientRecipe));
	}
	
	NamespacedRecipeRegistry<RecipeHammering> HAMMERING =
			RecipeRegistryFactory.namespacedBuilder(RecipeHammering.class)
					.registryId(ZeithTechAPI.id("hammering"))
					.recipeBuilderFactory(RecipeHammering.Builder::new)
					.customRecipes(CustomHammeringRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	NamespacedRecipeRegistry<RecipeMachineAssembler> MACHINE_ASSEMBLY =
			RecipeRegistryFactory.namespacedBuilder(RecipeMachineAssembler.class)
					.registryId(ZeithTechAPI.id("machine_assembly"))
					.recipeBuilderFactory(RecipeMachineAssembler.Builder::new)
					.customRecipes(CustomMachineAssemblyRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	NamespacedRecipeRegistry<RecipeGrinding> GRINDING =
			RecipeRegistryFactory.namespacedBuilder(RecipeGrinding.class)
					.registryId(ZeithTechAPI.id("grinding"))
					.recipeBuilderFactory(RecipeGrinding.GrindingRecipeBuilder::new)
					.customRecipes(CustomUnaryWithExtraRecipeGenerator.make(RecipeGrinding::new))
					.onClientRecipeReceive(broadcast())
					.build();
	
	NamespacedRecipeRegistry<RecipeSawmill> SAWMILL =
			RecipeRegistryFactory.namespacedBuilder(RecipeSawmill.class)
					.registryId(ZeithTechAPI.id("sawmill"))
					.recipeBuilderFactory(RecipeSawmill.SawmillRecipeBuilder::new)
					.customRecipes(CustomUnaryWithExtraRecipeGenerator.make(RecipeSawmill::new))
					.onClientRecipeReceive(broadcast())
					.build();
	
	NamespacedRecipeRegistry<RecipeFluidCentrifuge> FLUID_CENTRIFUGE =
			RecipeRegistryFactory.namespacedBuilder(RecipeFluidCentrifuge.class)
					.registryId(ZeithTechAPI.id("fluid_centrifuge"))
					.recipeBuilderFactory(RecipeFluidCentrifuge.FluidCentrifugeRecipeBuilder::new)
					.customRecipes(CustomFluidCentrifugeRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	NamespacedRecipeRegistry<RecipeWasteProcessor> WASTE_PROCESSING =
			RecipeRegistryFactory.namespacedBuilder(RecipeWasteProcessor.class)
					.registryId(ZeithTechAPI.id("waste_processing"))
					.recipeBuilderFactory(RecipeWasteProcessor.WasteProcessorRecipeBuilder::new)
					.customRecipes(CustomWasteProcessorCentrifugeRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	NamespacedRecipeRegistry<RecipeBlastFurnace> BLAST_FURNACE =
			RecipeRegistryFactory.namespacedBuilder(RecipeBlastFurnace.class)
					.registryId(ZeithTechAPI.id("blast_furnace"))
					.recipeBuilderFactory(RecipeBlastFurnace.BlastBuilder::new)
					.customRecipes(CustomBlastFurnaceRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	static void construct()
	{
		ProcessingModule.LOG.info("Setup recipe registries.");
	}
}