package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.hammerlib.api.crafting.RecipeRegistryFactory;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.RecipeHammering;
import org.zeith.tech.api.recipes.RecipeMachineAssembler;

public class RecipeRegistriesZT_Processing
{
	public static final NamespacedRecipeRegistry<RecipeMachineAssembler> MACHINE_ASSEBMLY =
			RecipeRegistryFactory.namespacedBuilder(RecipeMachineAssembler.class)
					.registryId(new ResourceLocation(ZeithTechAPI.MOD_ID, "machine_assembly"))
					.recipeBuilderFactory(RecipeMachineAssembler.Builder::new)
					.build();
	
	public static final NamespacedRecipeRegistry<RecipeHammering> HAMMERING =
			RecipeRegistryFactory.namespacedBuilder(RecipeHammering.class)
					.registryId(new ResourceLocation(ZeithTechAPI.MOD_ID, "hammering"))
					.recipeBuilderFactory(RecipeHammering.Builder::new)
					.build();
	
	public static void setup(FMLCommonSetupEvent e)
	{
		ZeithTech.LOG.info("Setup recipe registries.");
	}
}