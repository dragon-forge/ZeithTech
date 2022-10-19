package org.zeith.tech.api.recipes.processing;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.api.crafting.building.GeneralRecipeBuilder;
import org.zeith.hammerlib.api.crafting.impl.*;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.RegistrationAllocator;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;

import java.util.Objects;

public final class RecipeLiquidFuel
		extends BaseNameableRecipe
		implements IZeithTechRecipe
{
	private final FluidIngredient ingredient;
	private final int burnTime;
	
	public RecipeLiquidFuel(ResourceLocation id, FluidIngredient ingredient, int burnTime)
	{
		super(id, new ForgeEnergyResult(new FECharge(burnTime * 40)), NonNullList.of(new FluidStackIngredient(ingredient)));
		this.ingredient = ingredient;
		this.burnTime = burnTime;
	}
	
	@Override
	public void onDeregistered()
	{
		ZeithTechAPI.ifPresent(api -> api
				.getRecipeRegistries()
				.getRecipeLifecycleListener()
				.onRecipeDeRegistered(this)
		);
	}
	
	public FluidIngredient ingredient()
	{
		return ingredient;
	}
	
	public int burnTime()
	{
		return burnTime;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		var that = (RecipeLiquidFuel) obj;
		return Objects.equals(this.ingredient, that.ingredient) &&
				this.burnTime == that.burnTime;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(ingredient, burnTime);
	}
	
	@Override
	public String toString()
	{
		return "RecipeLiquidFuelGenerator[" +
				"ingredient=" + ingredient + ", " +
				"burnTime=" + burnTime + ']';
	}
	
	public boolean test(FluidStack fluid)
	{
		return ingredient.test(fluid);
	}
	
	public static class LiquidFuelRecipeBuilder
			extends GeneralRecipeBuilder<RecipeLiquidFuel, LiquidFuelRecipeBuilder>
	{
		protected final ReloadRecipeRegistryEvent.AddRecipes<RecipeLiquidFuel> event;
		
		protected FluidIngredient input = FluidIngredient.EMPTY;
		protected int burnTime = 0;
		
		public LiquidFuelRecipeBuilder(IRecipeRegistrationEvent<RecipeLiquidFuel> event)
		{
			super(event);
			this.event = Cast.cast(event);
		}
		
		public LiquidFuelRecipeBuilder input(FluidIngredient input)
		{
			this.input = input;
			return this;
		}
		
		public LiquidFuelRecipeBuilder burnTime(int burnTime)
		{
			this.burnTime = burnTime;
			return this;
		}
		
		@Override
		protected ResourceLocation generateId()
		{
			ResourceLocation rl = ForgeRegistries.FLUIDS.getKey(input.getValues()[0].getFluid());
			return RegistrationAllocator.findFreeLocation(event, rl);
		}
		
		@Override
		protected void validate() throws IllegalStateException
		{
			if(input.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined input!");
			if(burnTime <= 0)
				throw new IllegalStateException(getClass().getSimpleName() + " does not have burn time set!");
		}
		
		@Override
		protected RecipeLiquidFuel createRecipe() throws IllegalStateException
		{
			return new RecipeLiquidFuel(getIdentifier(), input, burnTime);
		}
	}
}