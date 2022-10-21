package org.zeith.tech.api.recipes.processing;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.api.crafting.building.GeneralRecipeBuilder;
import org.zeith.hammerlib.api.crafting.impl.BaseNameableRecipe;
import org.zeith.hammerlib.api.crafting.impl.FluidStackResult;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredientStack;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.recipes.RegistrationAllocator;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeWasteProcessor
		extends BaseNameableRecipe
		implements IZeithTechRecipe
{
	private final FluidIngredientStack inputA;
	private final FluidIngredientStack inputB;
	private final Ingredient inputItem;
	private final int time;
	private final FluidStack outputA, outputB;
	private final List<ExtraOutput> byproduct;
	
	public RecipeWasteProcessor(ResourceLocation id, FluidIngredientStack inputA, FluidIngredientStack inputB, Ingredient inputItem, int time, FluidStack outputA, FluidStack outputB, List<ExtraOutput> byproduct)
	{
		super(id, new FluidStackResult(outputA), NonNullList.create());
		this.inputA = inputA;
		this.inputB = inputB;
		this.inputItem = inputItem;
		this.time = time;
		this.outputA = outputA;
		this.outputB = outputB;
		this.byproduct = List.copyOf(byproduct);
	}
	
	public List<ExtraOutput> getByproduct()
	{
		return byproduct;
	}
	
	public FluidIngredientStack getInputA()
	{
		return inputA;
	}
	
	public FluidIngredientStack getInputB()
	{
		return inputB;
	}
	
	public Ingredient getInputItem()
	{
		return inputItem;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public FluidStack getOutputA()
	{
		return outputA.copy();
	}
	
	public FluidStack getOutputB()
	{
		return outputB.copy();
	}
	
	public static class WasteProcessorRecipeBuilder
			extends GeneralRecipeBuilder<RecipeWasteProcessor, WasteProcessorRecipeBuilder>
	{
		protected final ReloadRecipeRegistryEvent.AddRecipes<RecipeWasteProcessor> event;
		
		protected FluidIngredientStack inputA = FluidIngredientStack.EMPTY;
		protected FluidIngredientStack inputB = FluidIngredientStack.EMPTY;
		protected Ingredient inputItem = Ingredient.EMPTY;
		
		protected FluidStack resultA = FluidStack.EMPTY;
		protected FluidStack resultB = FluidStack.EMPTY;
		protected int time;
		protected List<ExtraOutput> byproduct = new ArrayList<>();
		
		public WasteProcessorRecipeBuilder(IRecipeRegistrationEvent<RecipeWasteProcessor> event)
		{
			super(event);
			this.event = Cast.cast(event);
		}
		
		public WasteProcessorRecipeBuilder input(FluidIngredientStack input)
		{
			this.inputA = input;
			this.inputB = FluidIngredientStack.EMPTY;
			return this;
		}
		
		public WasteProcessorRecipeBuilder input(Ingredient input)
		{
			this.inputItem = input;
			return this;
		}
		
		public WasteProcessorRecipeBuilder input(FluidIngredientStack inputA, FluidIngredientStack inputB)
		{
			this.inputA = inputA;
			this.inputB = inputB;
			return this;
		}
		
		public WasteProcessorRecipeBuilder time(int time)
		{
			this.time = time;
			return this;
		}
		
		public WasteProcessorRecipeBuilder result(FluidStack stack)
		{
			this.resultA = stack;
			this.resultB = FluidStack.EMPTY;
			return this;
		}
		
		public WasteProcessorRecipeBuilder result(FluidStack stackA, FluidStack stackB)
		{
			this.resultA = stackA;
			this.resultB = stackB;
			return this;
		}
		
		public WasteProcessorRecipeBuilder byproduct(ExtraOutput... byproduct)
		{
			this.byproduct.addAll(List.of(byproduct));
			return this;
		}
		
		@Override
		protected ResourceLocation generateId()
		{
			if(resultA.isEmpty() && resultB.isEmpty())
			{
				ItemStack stack = byproduct.stream().map(ExtraOutput::getMinimalResult).findFirst().orElse(ItemStack.EMPTY);
				if(!stack.isEmpty())
					return RegistrationAllocator.findFreeLocation(event, ForgeRegistries.ITEMS.getKey(stack.getItem()));
			}
			ResourceLocation rl = ForgeRegistries.FLUIDS.getKey((resultA.isEmpty() ? resultB : resultA).getFluid());
			return RegistrationAllocator.findFreeLocation(event, rl);
		}
		
		@Override
		protected void validate() throws IllegalStateException
		{
			if(resultA.isEmpty() && resultB.isEmpty() && byproduct.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined result!");
			if(inputA.isEmpty() && inputB.isEmpty() && inputItem.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined input! (must have any fluid input, or item input)");
			if(time <= 0)
				throw new IllegalStateException(getClass().getSimpleName() + " does not have process time!");
		}
		
		@Override
		protected RecipeWasteProcessor createRecipe() throws IllegalStateException
		{
			// Shift B components!
			
			if(resultA.isEmpty() && !resultB.isEmpty())
			{
				resultA = resultB;
				resultB = FluidStack.EMPTY;
			}
			
			if(inputA.isEmpty() && !inputB.isEmpty())
			{
				inputA = inputB;
				inputB = FluidIngredientStack.EMPTY;
			}
			
			return new RecipeWasteProcessor(getIdentifier(), inputA, inputB, inputItem, time, resultA, resultB, byproduct);
		}
	}
}