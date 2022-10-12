package org.zeith.tech.api.recipes.base;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.zeith.hammerlib.api.crafting.IFluidIngredient;
import org.zeith.hammerlib.api.crafting.IngredientStack;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;

import java.util.List;

public record FluidStackIngredientMulti(FluidIngredient ingredient)
		implements IFluidIngredient<FluidStackIngredientMulti>
{
	@Override
	public boolean canTakeFrom(IFluidTank tank, IngredientStack<FluidStackIngredientMulti> stack)
	{
		FluidStack drained = tank.drain(stack.amount, IFluidHandler.FluidAction.SIMULATE);
		return !drained.isEmpty() && stack.ingredient.ingredient.test(drained) && drained.getAmount() >= stack.amount;
	}
	
	@Override
	public boolean takeFrom(IFluidTank tank, IngredientStack<FluidStackIngredientMulti> stack)
	{
		if(canTakeFrom(tank, stack))
		{
			FluidStack drained = tank.drain(stack.amount, IFluidHandler.FluidAction.SIMULATE);
			if(!drained.isEmpty() && stack.ingredient.ingredient.test(drained) && drained.getAmount() >= stack.amount)
			{
				tank.drain(stack.amount, IFluidHandler.FluidAction.EXECUTE);
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public List<FluidStack> asIngredient()
	{
		return List.of(ingredient.getValues());
	}
}
