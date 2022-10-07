package org.zeith.tech.modules.transport.blocks.fluid_pipe;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class PipeFluidHandler
		implements IFluidHandler
{
	public final TileFluidPipe pipe;
	public final Direction from;
	
	public PipeFluidHandler(TileFluidPipe pipe, Direction from)
	{
		this.pipe = pipe;
		this.from = from;
	}
	
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
	
	@Override
	public int getTanks()
	{
		return 1;
	}
	
	@Override
	public @NotNull FluidStack getFluidInTank(int tank)
	{
		return pipe.tank.getFluidInTank(tank);
	}
	
	@Override
	public int getTankCapacity(int tank)
	{
		return pipe.tank.getTankCapacity(tank);
	}
	
	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack)
	{
		return pipe.tank.isFluidValid(tank, stack);
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		return pipe.tank.fill(limit(resource, pipe.transfer), action);
	}
	
	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action)
	{
		return pipe.tank.drain(limit(resource, pipe.transfer), action);
	}
	
	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action)
	{
		return pipe.tank.drain(Math.min(maxDrain, pipe.transfer), action);
	}
}