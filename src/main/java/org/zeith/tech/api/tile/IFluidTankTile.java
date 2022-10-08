package org.zeith.tech.api.tile;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidTankTile
{
	int storeFluid(FluidStack resource, IFluidHandler.FluidAction action);
}