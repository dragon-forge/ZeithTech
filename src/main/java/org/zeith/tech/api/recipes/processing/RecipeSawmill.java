package org.zeith.tech.api.recipes.processing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.base.RecipeUnaryBase;

import java.util.Optional;

public class RecipeSawmill
		extends RecipeUnaryBase
{
	protected final ExtraOutput extra;
	
	public RecipeSawmill(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier, ExtraOutput extra)
	{
		super(id, input, inputCount, output, time, tier);
		this.extra = extra;
	}
	
	public RecipeSawmill(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier)
	{
		super(id, input, inputCount, output, time, tier);
		this.extra = null;
	}
	
	public Optional<ExtraOutput> getExtra()
	{
		return Optional.ofNullable(extra);
	}
	
	public static class SawmillRecipeBuilder
			extends RecipeUnaryBase.Builder<RecipeSawmill>
	{
		protected ExtraOutput extra;
		
		public SawmillRecipeBuilder(IRecipeRegistrationEvent<RecipeSawmill> event)
		{
			super(event, null);
		}
		
		public SawmillRecipeBuilder extraOutput(ExtraOutput output)
		{
			this.extra = output;
			return this;
		}
		
		@Override
		protected RecipeSawmill createRecipe() throws IllegalStateException
		{
			return new RecipeSawmill(getIdentifier(), inputItem, inputCount, result, time, tier, extra);
		}
	}
}