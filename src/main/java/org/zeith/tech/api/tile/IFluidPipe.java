package org.zeith.tech.api.tile;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Intermediate interface for working with fluid pipes
 */
public interface IFluidPipe
{
	default void createVacuum(int ticks)
	{
		createVacuum(FluidStack.EMPTY, ticks);
	}
	
	void createVacuum(FluidStack fluid, int ticks);
	
	FluidStack extractFluidFromPipe(int amount, IFluidHandler.FluidAction action);
}