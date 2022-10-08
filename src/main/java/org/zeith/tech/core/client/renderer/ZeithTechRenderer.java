package org.zeith.tech.core.client.renderer;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class ZeithTechRenderer
{
	public static int calculateGlowLight(int combinedLight, @NotNull FluidStack fluid)
	{
		return fluid.isEmpty() ? combinedLight : calculateGlowLight(combinedLight, fluid.getFluid().getFluidType().getLightLevel(fluid));
	}
	
	public static int calculateGlowLight(int combinedLight, int glow)
	{
		return combinedLight & -65536 | Math.max(Math.min(glow, 15) << 4, combinedLight & '\uffff');
	}
}