package org.zeith.tech.api.recipes.processing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.base.RecipeUnaryBase;

import java.util.Optional;

public class RecipeGrinding
		extends RecipeUnaryBase
{
	protected final ExtraOutput extra;
	
	public RecipeGrinding(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier, ExtraOutput extra)
	{
		super(id, input, inputCount, output, time, tier);
		this.extra = extra;
	}
	
	public RecipeGrinding(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier)
	{
		super(id, input, inputCount, output, time, tier);
		this.extra = null;
	}
	
	public Optional<ExtraOutput> getExtra()
	{
		return Optional.ofNullable(extra);
	}
	
	public static class GrindingRecipeBuilder
			extends RecipeUnaryBase.Builder<RecipeGrinding>
	{
		protected ExtraOutput extra;
		
		public GrindingRecipeBuilder(IRecipeRegistrationEvent<RecipeGrinding> event)
		{
			super(event, null);
		}
		
		public GrindingRecipeBuilder extraOutput(ExtraOutput output)
		{
			this.extra = output;
			return this;
		}
		
		@Override
		protected RecipeGrinding createRecipe() throws IllegalStateException
		{
			return new RecipeGrinding(getIdentifier(), inputItem, inputCount, result, time, tier, extra);
		}
	}
}