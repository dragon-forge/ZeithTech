package org.zeith.tech.api.tile;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;

/**
 * Intermediate interface for working with fluid pipes
 */
public interface IFluidPipe
{
	default void createVacuum(int ticks)
	{
		createVacuum(FluidIngredient.EMPTY, ticks);
	}
	
	void createVacuum(FluidIngredient fluid, int ticks);
	
	FluidStack extractFluidFromPipe(int amount, IFluidHandler.FluidAction action);
}