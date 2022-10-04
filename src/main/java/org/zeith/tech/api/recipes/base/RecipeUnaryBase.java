package org.zeith.tech.api.recipes.base;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.zeith.hammerlib.api.crafting.building.IRecipeBuilderFactory;
import org.zeith.hammerlib.api.crafting.impl.*;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.enums.TechTier;

public class RecipeUnaryBase
		extends BaseNameableRecipe
		implements IUnaryRecipe
{
	final int time, inputCount;
	final Ingredient input;
	final ItemStack output;
	final TechTier tier;
	
	public RecipeUnaryBase(ResourceLocation id, Ingredient input, int inputCount, ItemStack output, int time, TechTier tier)
	{
		super(id, new ItemStackResult(output), NonNullList.of(new MCIngredient(input)));
		this.inputCount = inputCount;
		this.input = input;
		this.output = output;
		this.time = time;
		this.tier = tier;
	}
	
	@Override
	public void onDeregistered()
	{
	}
	
	public ItemStack getRecipeOutput()
	{
		return output.copy();
	}
	
	@Override
	public Ingredient getInput()
	{
		return input;
	}
	
	@Override
	public ItemStack assemble(BlockEntity tile)
	{
		return output.copy();
	}
	
	@Override
	public TechTier getTier()
	{
		return tier;
	}
	
	@Override
	public int getInputCount()
	{
		return inputCount;
	}
	
	@Override
	public int getCraftTime()
	{
		return time;
	}
	
	public static <T extends RecipeUnaryBase> IRecipeBuilderFactory<T, Builder<T>> makeBuilder(IUnaryRecipeConstructor<T> constructor)
	{
		return evt -> new RecipeUnaryBase.Builder<>(evt, constructor);
	}
	
	public static class Builder<T extends RecipeUnaryBase>
			extends BuilderWithStackResult<T, Builder<T>>
	{
		private Ingredient inputItem = Ingredient.EMPTY;
		private int time = 200, inputCount = 1;
		private TechTier tier = TechTier.BASIC;
		
		private final IUnaryRecipeConstructor<T> constructor;
		
		public Builder(IRecipeRegistrationEvent<T> event, IUnaryRecipeConstructor<T> constructor)
		{
			super(event);
			this.constructor = constructor;
		}
		
		public Builder<T> withTier(TechTier tier)
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
			return constructor.newInstance(getIdentifier(), inputItem, inputCount, result, time, tier);
		}
	}
}