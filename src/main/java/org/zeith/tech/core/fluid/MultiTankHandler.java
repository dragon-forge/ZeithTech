package org.zeith.tech.core.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public record MultiTankHandler(IFluidTank[] tanks, int[] inputTanks, int[] outputTanks)
		implements IFluidHandler
{
	@Override
	public int getTanks()
	{
		return tanks.length;
	}
	
	@Override
	public @NotNull FluidStack getFluidInTank(int tank)
	{
		return tanks[tank].getFluid();
	}
	
	@Override
	public int getTankCapacity(int tank)
	{
		return tanks[tank].getCapacity();
	}
	
	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack)
	{
		return tanks[tank].isFluidValid(stack);
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		FluidStack copy = resource.copy();
		int filled = 0;
		for(int i = 0; i < inputTanks.length && !copy.isEmpty(); ++i)
		{
			int f = tanks[inputTanks[i]].fill(copy, action);
			filled += f;
			copy.shrink(f);
		}
		return filled;
	}
	
	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action)
	{
		FluidStack copy = resource.copy();
		FluidStack drained = FluidStack.EMPTY;
		for(int i = 0; i < outputTanks.length && !copy.isEmpty(); ++i)
		{
			var f = tanks[outputTanks[i]].drain(resource, action);
			if(!f.isEmpty())
			{
				if(drained.isEmpty())
					drained = f;
				else
					drained.grow(f.getAmount());
				copy.shrink(f.getAmount());
			}
		}
		return drained;
	}
	
	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action)
	{
		FluidStack drained = FluidStack.EMPTY;
		for(int tank : outputTanks)
		{
			var f = tanks[tank].drain(maxDrain, action);
			if(!f.isEmpty()) return f;
		}
		return drained;
	}
}
