package org.zeith.tech.api.recipes.processing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.base.RecipeUnaryWithExtra;

public class RecipeSawmill
		extends RecipeUnaryWithExtra
{
	public RecipeSawmill(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier, ExtraOutput extra)
	{
		super(id, input, inputCount, output, time, tier, extra);
	}
	
	public static class SawmillRecipeBuilder
			extends RecipeUnaryWithExtra.Builder<RecipeSawmill>
	{
		public SawmillRecipeBuilder(IRecipeRegistrationEvent<RecipeSawmill> event)
		{
			super(event, RecipeSawmill::new);
		}
	}
}