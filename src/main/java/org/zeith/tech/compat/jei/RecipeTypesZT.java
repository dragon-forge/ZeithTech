package org.zeith.tech.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import org.zeith.tech.api.compat.jei.ITieredRecipeType;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.core.ZeithTech;

public class RecipeTypesZT
{
	public static final RecipeType<RecipeHammering> MANUAL_HAMMERING = ITieredRecipeType.setMaxTier(RecipeType.create(ZeithTech.MOD_ID, "manual_hammering", RecipeHammering.class), TechTier.BASIC);
	public static final RecipeType<RecipeHammering> ADVANCED_HAMMERING = ITieredRecipeType.setMaxTier(RecipeType.create(ZeithTech.MOD_ID, "advanced_hammering", RecipeHammering.class), TechTier.ADVANCED);
	public static final RecipeType<RecipeMachineAssembler> MACHINE_ASSEMBLY_BASIC = ITieredRecipeType.setMaxTier(RecipeType.create(ZeithTech.MOD_ID, "machine_assembly_basic", RecipeMachineAssembler.class), TechTier.BASIC);
	public static final RecipeType<RecipeMachineAssembler> MACHINE_ASSEMBLY_ADVANCED = ITieredRecipeType.setMaxTier(RecipeType.create(ZeithTech.MOD_ID, "machine_assembly_advanced", RecipeMachineAssembler.class), TechTier.ADVANCED);
	public static final RecipeType<RecipeGrinding> GRINDER_BASIC = ITieredRecipeType.setMaxTier(RecipeType.create(ZeithTech.MOD_ID, "grinding_basic", RecipeGrinding.class), TechTier.BASIC);
	public static final RecipeType<RecipeSawmill> SAWMILL = RecipeType.create(ZeithTech.MOD_ID, "sawmill", RecipeSawmill.class);
	public static final RecipeType<RecipeFluidCentrifuge> FLUID_CENTRIFUGE = RecipeType.create(ZeithTech.MOD_ID, "fluid_centrifuge", RecipeFluidCentrifuge.class);
	public static final RecipeType<RecipeLiquidFuel> LIQUID_FUEL = RecipeType.create(ZeithTech.MOD_ID, "liquid_fuel", RecipeLiquidFuel.class);
	public static final RecipeType<RecipeWasteProcessor> WASTE_PROCESSING = RecipeType.create(ZeithTech.MOD_ID, "waste_processing", RecipeWasteProcessor.class);
}
