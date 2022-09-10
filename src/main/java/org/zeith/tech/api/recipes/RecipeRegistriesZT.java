package org.zeith.tech.api.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.hammerlib.api.crafting.RecipeRegistryFactory;
import org.zeith.tech.ZeithTech;

public class RecipeRegistriesZT
{
	
	public static final NamespacedRecipeRegistry<RecipeMachineAssembler> MACHINE_ASSEBMLY =
			RecipeRegistryFactory.namespacedBuilder(RecipeMachineAssembler.class)
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "machine_assembly"))
					.recipeBuilderFactory(RecipeMachineAssembler.Builder::new)
					.build();
	
	public static final NamespacedRecipeRegistry<RecipeHammering> HAMMERING =
			RecipeRegistryFactory.namespacedBuilder(RecipeHammering.class)
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "hammering"))
					.recipeBuilderFactory(RecipeHammering.Builder::new)
					.build();
	
	public static void setup(FMLCommonSetupEvent e)
	{
		ZeithTech.LOG.info("Setup recipe registries.");
	}
}