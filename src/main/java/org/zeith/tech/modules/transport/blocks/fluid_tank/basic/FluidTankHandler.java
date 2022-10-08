package org.zeith.tech.modules.transport.blocks.fluid_tank.basic;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.util.mcf.fluid.FluidHelper;

import java.util.function.ToIntBiFunction;

public record FluidTankHandler(IFluidHandler parent, ToIntBiFunction<FluidStack, IFluidHandler.FluidAction> onOverflow)
		implements IFluidHandler
{
	@Override
	public int getTanks()
	{
		return parent.getTanks();
	}
	
	@Override
	public @NotNull FluidStack getFluidInTank(int tank)
	{
		return parent.getFluidInTank(tank);
	}
	
	@Override
	public int getTankCapacity(int tank)
	{
		return parent.getTankCapacity(tank);
	}
	
	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack)
	{
		return parent.isFluidValid(tank, stack);
	}
	
	@Override
	public int fill(FluidStack resource, IFluidHandler.FluidAction action)
	{
		int amt = resource.getAmount();
		int filled = parent.fill(resource, action);
		if(amt > filled) filled += onOverflow.applyAsInt(FluidHelper.limit(resource, amt - filled), action);
		return filled;
	}
	
	@Override
	public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action)
	{
		return parent.drain(maxDrain, action);
	}
	
	@Override
	public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action)
	{
		return parent.drain(resource, action);
	}
}