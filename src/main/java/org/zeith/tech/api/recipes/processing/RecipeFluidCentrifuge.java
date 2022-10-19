package org.zeith.tech.api.recipes.processing;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.api.crafting.building.GeneralRecipeBuilder;
import org.zeith.hammerlib.api.crafting.impl.*;
import org.zeith.hammerlib.api.energy.EnergyUnit;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredientStack;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.recipes.RegistrationAllocator;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;

import java.util.Optional;

public class RecipeFluidCentrifuge
		extends BaseNameableRecipe
		implements IZeithTechRecipe
{
	private final FluidIngredientStack input;
	private final int energy;
	private final FluidStack output;
	private final ExtraOutput byproduct;
	
	public RecipeFluidCentrifuge(ResourceLocation id, FluidIngredientStack input, int energy, FluidStack output, ExtraOutput byproduct)
	{
		super(id, new FluidStackResult(output), NonNullList.of(new FluidStackIngredient(input.fluid()), new EnergyIngredient(energy, EnergyUnit.FE)));
		this.input = input;
		this.energy = energy;
		this.output = output;
		this.byproduct = byproduct;
	}
	
	public FluidIngredientStack getInput()
	{
		return input;
	}
	
	public int getEnergy()
	{
		return energy;
	}
	
	public FluidStack getOutput()
	{
		return output.copy();
	}
	
	public ExtraOutput getByproduct()
	{
		return byproduct;
	}
	
	public Optional<ExtraOutput> getExtra()
	{
		return Optional.ofNullable(byproduct);
	}
	
	public int getOutputAmount()
	{
		return output.getAmount();
	}
	
	public static class FluidCentrifugeRecipeBuilder
			extends GeneralRecipeBuilder<RecipeFluidCentrifuge, FluidCentrifugeRecipeBuilder>
	{
		protected final ReloadRecipeRegistryEvent.AddRecipes<RecipeFluidCentrifuge> event;
		
		protected FluidIngredientStack input = FluidIngredientStack.EMPTY;
		protected int energy = 0;
		protected FluidStack result = FluidStack.EMPTY;
		protected ExtraOutput extra;
		
		public FluidCentrifugeRecipeBuilder(IRecipeRegistrationEvent<RecipeFluidCentrifuge> event)
		{
			super(event);
			this.event = Cast.cast(event);
		}
		
		public FluidCentrifugeRecipeBuilder input(FluidIngredientStack input)
		{
			this.input = input;
			return this;
		}
		
		public FluidCentrifugeRecipeBuilder energy(int energy)
		{
			this.energy = energy;
			return this;
		}
		
		public FluidCentrifugeRecipeBuilder result(FluidStack stack)
		{
			this.result = stack;
			return this;
		}
		
		public FluidCentrifugeRecipeBuilder extraOutput(ExtraOutput output)
		{
			this.extra = output;
			return this;
		}
		
		@Override
		protected ResourceLocation generateId()
		{
			ResourceLocation rl = ForgeRegistries.FLUIDS.getKey(result.getFluid());
			return RegistrationAllocator.findFreeLocation(event, rl);
		}
		
		@Override
		protected void validate() throws IllegalStateException
		{
			if(result.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined result!");
			if(input.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined input!");
			if(energy <= 0)
				throw new IllegalStateException(getClass().getSimpleName() + " does not have set energy!");
		}
		
		@Override
		protected RecipeFluidCentrifuge createRecipe() throws IllegalStateException
		{
			return new RecipeFluidCentrifuge(getIdentifier(), input, energy, result, extra);
		}
	}
}