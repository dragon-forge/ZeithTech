package org.zeith.tech.api.recipes.processing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.RecipeUnaryBase;

public class RecipeGrinding
		extends RecipeUnaryBase
{
	public RecipeGrinding(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier)
	{
		super(id, input, inputCount, output, time, tier);
	}
	
	@Override
	public void onDeregistered()
	{
		ZeithTech.forCompats(c -> c.deregisterRecipe(this));
	}
}