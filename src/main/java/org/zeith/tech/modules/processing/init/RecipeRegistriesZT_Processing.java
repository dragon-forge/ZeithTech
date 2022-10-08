package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.hammerlib.api.crafting.RecipeRegistryFactory;
import org.zeith.hammerlib.api.crafting.itf.IRecipeReceiver;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;
import org.zeith.tech.api.recipes.base.RecipeUnaryBase;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.recipes.*;

public class RecipeRegistriesZT_Processing
{
	private static <R extends IZeithTechRecipe> IRecipeReceiver<R> broadcast()
	{
		return clientRecipe -> ZeithTechAPI.ifPresent(api -> api.getRecipeRegistries().getRecipeLifecycleListener().onRecipeRegistered(clientRecipe));
	}
	
	public static final NamespacedRecipeRegistry<RecipeHammering> HAMMERING =
			RecipeRegistryFactory.namespacedBuilder(RecipeHammering.class)
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "hammering"))
					.recipeBuilderFactory(RecipeHammering.Builder::new)
					.customRecipes(CustomHammeringRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	public static final NamespacedRecipeRegistry<RecipeMachineAssembler> MACHINE_ASSEBMLY =
			RecipeRegistryFactory.namespacedBuilder(RecipeMachineAssembler.class)
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "machine_assembly"))
					.recipeBuilderFactory(RecipeMachineAssembler.Builder::new)
					.customRecipes(CustomMachineAssemblyRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	public static final NamespacedRecipeRegistry<RecipeGrinding> GRINDING =
			RecipeRegistryFactory.namespacedBuilder(RecipeGrinding.class)
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "grinding"))
					.recipeBuilderFactory(RecipeUnaryBase.makeBuilder(RecipeGrinding::new))
					.customRecipes(CustomUnaryRecipeGenerator.make(RecipeGrinding::new))
					.onClientRecipeReceive(broadcast())
					.build();
	
	public static final NamespacedRecipeRegistry<RecipeSawmill> SAWMILL =
			RecipeRegistryFactory.namespacedBuilder(RecipeSawmill.class)
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "sawmill"))
					.recipeBuilderFactory(RecipeSawmill.SawmillRecipeBuilder::new)
					.customRecipes(CustomSawmillRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	public static final NamespacedRecipeRegistry<RecipeFluidCentrifuge> FLUID_CENTRIFUGE =
			RecipeRegistryFactory.namespacedBuilder(RecipeFluidCentrifuge.class)
					.registryId(new ResourceLocation(ZeithTech.MOD_ID, "fluid_centrifuge"))
					.recipeBuilderFactory(RecipeFluidCentrifuge.FluidCentrifugeRecipeBuilder::new)
					.customRecipes(CustomFluidCentrifugeRecipeGenerator::new)
					.onClientRecipeReceive(broadcast())
					.build();
	
	public static void setup(FMLCommonSetupEvent e)
	{
		ZeithTech.LOG.info("Setup recipe registries.");
	}
}