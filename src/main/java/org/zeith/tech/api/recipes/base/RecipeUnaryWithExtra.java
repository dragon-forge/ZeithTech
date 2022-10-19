package org.zeith.tech.api.recipes.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.enums.TechTier;

import java.util.Optional;

public class RecipeUnaryWithExtra
		extends RecipeUnaryBase
{
	protected final ExtraOutput extra;
	
	public RecipeUnaryWithExtra(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier, ExtraOutput extra)
	{
		super(id, input, inputCount, output, time, tier);
		this.extra = extra;
	}
	
	public RecipeUnaryWithExtra(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier)
	{
		super(id, input, inputCount, output, time, tier);
		this.extra = null;
	}
	
	public Optional<ExtraOutput> getExtra()
	{
		return Optional.ofNullable(extra);
	}
	
	public static class Builder<T extends RecipeUnaryWithExtra>
			extends BuilderWithStackResult<T, RecipeUnaryWithExtra.Builder<T>>
	{
		protected ExtraOutput extra;
		protected Ingredient inputItem = Ingredient.EMPTY;
		protected int time = 200, inputCount = 1;
		protected TechTier tier = TechTier.BASIC;
		
		protected final IUnaryRecipeWithExtraConstructor<T> constructor;
		
		public Builder(IRecipeRegistrationEvent<T> event, IUnaryRecipeWithExtraConstructor<T> constructor)
		{
			super(event);
			this.constructor = constructor;
		}
		
		public Builder<T> tier(TechTier tier)
		{
			this.tier = tier;
			return this;
		}
		
		public Builder<T> craftTime(int time)
		{
			this.time = time;
			return this;
		}
		
		public Builder<T> input(Ingredient inputItem)
		{
			this.inputItem = inputItem;
			return this;
		}
		
		public Builder<T> input(Object input)
		{
			return input(RecipeHelper.fromComponent(input));
		}
		
		public Builder<T> inputCount(int inputCount)
		{
			this.inputCount = inputCount;
			return this;
		}
		
		public Builder<T> extraOutput(ExtraOutput output)
		{
			this.extra = output;
			return this;
		}
		
		@Override
		protected void validate()
		{
			super.validate();
			if(inputItem == null || inputItem.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined input!");
			if(time < 1)
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a set time to perform the recipe!");
		}
		
		@Override
		protected T createRecipe() throws IllegalStateException
		{
			return constructor.newInstance(getIdentifier(), inputItem, inputCount, result, time, tier, extra);
		}
	}
}