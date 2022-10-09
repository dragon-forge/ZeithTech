package org.zeith.tech.api.modules;

import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;

import java.util.List;

public interface IModuleProcessing
		extends IBaseModule
{
	void setLiquidFuelBurnTime(FluidIngredient fluid, int burnTimeInTicks);
	
	int getLiquidFuelBurnTime(FluidStack fluid);
	
	List<Tuple<FluidIngredient, Integer>> getLiquidFuelBurnTimes();
}