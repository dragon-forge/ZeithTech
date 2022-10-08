package org.zeith.tech.utils.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidHelper
{
	public static FluidStack limit(FluidStack fluid, int max)
	{
		if(fluid.getAmount() <= max)
			return fluid;
		return withAmount(fluid, max);
	}
	
	public static FluidStack withAmount(FluidStack fluid, int amount)
	{
		var fs = fluid.copy();
		fs.setAmount(amount);
		return fs;
	}
	
	public static int transfer(IFluidHandler from, IFluidHandler to, int limit)
	{
		var canDrain = from.drain(limit, IFluidHandler.FluidAction.SIMULATE);
		if(canDrain.isEmpty())
			return 0;
		var filledAmount = to.fill(canDrain, IFluidHandler.FluidAction.EXECUTE);
		if(filledAmount <= 0)
			return 0;
		from.drain(filledAmount, IFluidHandler.FluidAction.EXECUTE);
		return filledAmount;
	}
}