package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.hammerlib.api.crafting.RecipeRegistryFactory;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.recipes.RecipeHammering;
import org.zeith.tech.api.recipes.RecipeMachineAssembler;
import org.zeith.tech.modules.processing.recipes.CustomHammeringRecipeGenerator;
import org.zeith.tech.modules.processing.recipes.CustomMachineAssemblyRecipeGenerator;

public class RecipeRegistriesZT_Processing
{
	public static final NamespacedRecipeRegistry<RecipeMachineAssembler> MACHINE_ASSEBMLY =
			RecipeRegistryFactory.namespacedBuilder(RecipeMachineAssembler.class)
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "machine_assembly"))
					.recipeBuilderFactory(RecipeMachineAssembler.Builder::new)
					.customRecipes(CustomMachineAssemblyRecipeGenerator::new)
					.onClientRecipeReceive(clientRecipe ->
					{
						ZeithTech.forCompats(c -> c.acceptRecipe(clientRecipe));
					})
					.build();
	
	public static final NamespacedRecipeRegistry<RecipeHammering> HAMMERING =
			RecipeRegistryFactory.namespacedBuilder(RecipeHammering.class)
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "hammering"))
					.recipeBuilderFactory(RecipeHammering.Builder::new)
					.customRecipes(CustomHammeringRecipeGenerator::new)
					.onClientRecipeReceive(clientRecipe ->
					{
						ZeithTech.forCompats(c -> c.acceptRecipe(clientRecipe));
					})
					.build();
	
	public static void setup(FMLCommonSetupEvent e)
	{
		ZeithTech.LOG.info("Setup recipe registries.");
	}
}