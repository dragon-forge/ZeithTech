package org.zeith.tech.api.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.hammerlib.api.crafting.IBaseIngredient;
import org.zeith.hammerlib.api.crafting.ICraftingExecutor;
import org.zeith.hammerlib.api.crafting.impl.*;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.core.adapter.recipe.RecipeShape;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.enums.TechTier;

import java.util.HashMap;
import java.util.Map;

public class RecipeMachineAssembler
		extends BaseNameableRecipe
{
	private final TechTier minTier;
	private final int width, height;
	private final NonNullList<Ingredient> recipeItems;
	private final ItemStackResult output;
	
	public RecipeMachineAssembler(ResourceLocation id, TechTier minTier, ItemStackResult output, RecipeShape shape, Map<Character, Ingredient> dictionary)
	{
		super(id, output, parseHLIngredients(shape, dictionary));
		if(shape.width > 5 || shape.height > 5)
			throw new IllegalArgumentException("Recipe input map is larger than allowed (5x5)");
		this.output = output;
		this.minTier = minTier;
		this.width = shape.width;
		this.height = shape.height;
		this.recipeItems = shape.createIngredientMap(dictionary);
	}
	
	public ItemStack getRecipeOutput(ICraftingExecutor executor)
	{
		return output.getOutput(executor);
	}
	
	public TechTier getMinTier()
	{
		return minTier;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public NonNullList<Ingredient> getRecipeItems()
	{
		return recipeItems;
	}
	
	public boolean matches(Container container, TechTier tier)
	{
		if(!tier.isOrHigher(this.minTier))
			return false;
		
		for(int i = 0; i <= 5 - this.width; ++i)
		{
			for(int j = 0; j <= 5 - this.height; ++j)
			{
				if(this.matches(container, i, j, true))
				{
					return true;
				}
				
				if(this.matches(container, i, j, false))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean matches(Container container, int x, int y, boolean mirror)
	{
		for(int i = 0; i < 5; ++i)
		{
			for(int j = 0; j < 5; ++j)
			{
				int k = i - x;
				int l = j - y;
				
				Ingredient ingredient = Ingredient.EMPTY;
				
				if(k >= 0 && l >= 0 && k < this.width && l < this.height)
				{
					if(mirror)
					{
						ingredient = this.recipeItems.get(this.width - k - 1 + l * this.width);
					} else
					{
						ingredient = this.recipeItems.get(k + l * this.width);
					}
				}
				
				if(!ingredient.test(container.getItem(i + j * 5)))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static NonNullList<IBaseIngredient> parseHLIngredients(RecipeShape shape, Map<Character, Ingredient> dictionary)
	{
		NonNullList<IBaseIngredient> lst = NonNullList.create();
		shape.createIngredientMap(dictionary)
				.stream()
				.filter(s -> !s.isEmpty())
				.map(MCIngredient::new)
				.forEach(lst::add);
		return lst;
	}
	
	public static class Builder
			extends BuilderWithStackResult<RecipeMachineAssembler, Builder>
	{
		private final Map<Character, Ingredient> dictionary = new HashMap<>();
		private RecipeShape shape;
		private TechTier minTier = TechTier.BASIC;
		
		public Builder(IRecipeRegistrationEvent<RecipeMachineAssembler> event)
		{
			super(event);
		}
		
		public Builder minTier(TechTier minTier)
		{
			this.minTier = minTier;
			return this;
		}
		
		public Builder shape(int width, int height, String... shapeKeys)
		{
			this.shape = new RecipeShape(width, height, shapeKeys);
			return this;
		}
		
		public Builder shape(String... shapeKeys)
		{
			this.shape = new RecipeShape(shapeKeys);
			return this;
		}
		
		public Builder map(char c, Object ingredient)
		{
			dictionary.put(c, RecipeHelper.fromComponent(ingredient));
			return this;
		}
		
		@Override
		protected void validate() throws IllegalStateException
		{
			super.validate();
			if(shape == null)
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined shape!");
			if(dictionary.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have any defined ingredients!");
		}
		
		@Override
		protected ResourceLocation generateId()
		{
			return registrar.nextId(result.getItem());
		}
		
		@Override
		protected RecipeMachineAssembler createRecipe() throws IllegalStateException
		{
			return new RecipeMachineAssembler(getIdentifier(), minTier, new ItemStackResult(result), shape, dictionary);
		}
	}
}