package org.zeith.tech.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.recipes.RecipeHammering;
import org.zeith.tech.api.recipes.RecipeMachineAssembler;

public class RecipeTypesZT
{
	public static final RecipeType<RecipeHammering> MANUAL_HAMMERING = RecipeType.create(ZeithTech.MOD_ID, "manual_hammering", RecipeHammering.class);
	public static final RecipeType<RecipeMachineAssembler> MACHINE_ASSEMBLY_BASIC = RecipeType.create(ZeithTech.MOD_ID, "machine_assembly_basic", RecipeMachineAssembler.class);
}
