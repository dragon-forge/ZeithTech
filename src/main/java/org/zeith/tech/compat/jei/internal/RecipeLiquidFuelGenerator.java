package org.zeith.tech.compat.jei.internal;

import net.minecraft.util.Tuple;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;

public record RecipeLiquidFuelGenerator(FluidIngredient ingredient, int burnTime)
{
	public static RecipeLiquidFuelGenerator of(Tuple<FluidIngredient, Integer> tuple)
	{
		return new RecipeLiquidFuelGenerator(tuple.getA(), tuple.getB());
	}
}