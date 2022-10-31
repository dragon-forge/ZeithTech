package org.zeith.tech.modules.generators.init;

import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.hammerlib.api.crafting.RecipeRegistryFactory;
import org.zeith.hammerlib.api.crafting.itf.IRecipeReceiver;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;
import org.zeith.tech.api.recipes.processing.RecipeLiquidFuel;
import org.zeith.tech.modules.generators.GeneratorsModule;
import org.zeith.tech.modules.processing.recipes.CustomLiquidFluidRecipeGenerator;

public interface RecipeRegistriesZT_Generators
{
	private static <R extends IZeithTechRecipe> IRecipeReceiver<R> broadcast()
	{
		return clientRecipe -> ZeithTechAPI.ifPresent(api -> api.getRecipeRegistries().getRecipeLifecycleListener().onRecipeRegistered(clientRecipe));
	}
	
	NamespacedRecipeRegistry<RecipeLiquidFuel> LIQUID_FUEL =
			RecipeRegistryFactory.namespacedBuilder(RecipeLiquidFuel.class)
					.registryId(ZeithTechAPI.id("liquid_fuel"))
					.recipeBuilderFactory(RecipeLiquidFuel.LiquidFuelRecipeBuilder::new)
					.customRecipes(CustomLiquidFluidRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	static void construct()
	{
		GeneratorsModule.LOG.info("Setup recipe registries.");
	}
}