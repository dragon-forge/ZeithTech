package org.zeith.tech.modules.generators.init;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.hammerlib.api.crafting.RecipeRegistryFactory;
import org.zeith.hammerlib.api.crafting.itf.IRecipeReceiver;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;
import org.zeith.tech.api.recipes.processing.RecipeLiquidFuel;
import org.zeith.tech.core.ZeithTech;
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
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "liquid_fuel"))
					.recipeBuilderFactory(RecipeLiquidFuel.LiquidFuelRecipeBuilder::new)
					.customRecipes(CustomLiquidFluidRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	static void construct()
	{
		GeneratorsModule.LOG.info("Setup recipe registries.");
	}
}