package org.zeith.tech.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import org.zeith.tech.api.compat.jei.ITieredRecipeType;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.core.ZeithTech;

public class RecipeTypesZT
{
	public static final RecipeType<RecipeHammering> MANUAL_HAMMERING = ITieredRecipeType.setMaxTier(RecipeType.create(ZeithTech.MOD_ID, "manual_hammering", RecipeHammering.class), TechTier.BASIC);
	public static final RecipeType<RecipeMachineAssembler> MACHINE_ASSEMBLY_BASIC = ITieredRecipeType.setMaxTier(RecipeType.create(ZeithTech.MOD_ID, "machine_assembly_basic", RecipeMachineAssembler.class), TechTier.BASIC);
	public static final RecipeType<RecipeGrinding> GRINDER_BASIC = ITieredRecipeType.setMaxTier(RecipeType.create(ZeithTech.MOD_ID, "grinding_basic", RecipeGrinding.class), TechTier.BASIC);
	public static final RecipeType<RecipeSawmill> SAWMILL = RecipeType.create(ZeithTech.MOD_ID, "sawmill", RecipeSawmill.class);
}
