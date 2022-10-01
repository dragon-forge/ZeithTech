package org.zeith.tech.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.recipes.RecipeHammering;

public class RecipeTypesZT
{
	public static final RecipeType<RecipeHammering> MANUAL_HAMMERING = RecipeType.create(ZeithTech.MOD_ID, "manual_hammering", RecipeHammering.class);
}
