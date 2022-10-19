package org.zeith.tech.api.recipes.processing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.base.RecipeUnaryWithExtra;

public class RecipeGrinding
		extends RecipeUnaryWithExtra
{
	public RecipeGrinding(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier, ExtraOutput extra)
	{
		super(id, input, inputCount, output, time, tier, extra);
	}
	
	public static class GrindingRecipeBuilder
			extends RecipeUnaryWithExtra.Builder<RecipeGrinding>
	{
		public GrindingRecipeBuilder(IRecipeRegistrationEvent<RecipeGrinding> event)
		{
			super(event, RecipeGrinding::new);
		}
	}
}