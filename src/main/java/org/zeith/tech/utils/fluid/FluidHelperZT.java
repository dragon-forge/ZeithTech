package org.zeith.tech.utils.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

public class FluidHelperZT
{
	public static boolean isFluidContainerEmpty(ItemStack stack)
	{
		return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(h ->
		{
			var slots = h.getTanks();
			for(int i = 0; i < slots; ++i)
				if(!h.getFluidInTank(i).isEmpty())
					return false;
			return true;
		}).orElse(false);
	}
	
	public static boolean isFluidContainerFull(ItemStack stack)
	{
		return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(h ->
		{
			FluidStack fs;
			var slots = h.getTanks();
			for(int i = 0; i < slots; ++i)
				if((fs = h.getFluidInTank(i)).isEmpty() || fs.getAmount() < h.getTankCapacity(i))
					return false;
			return true;
		}).orElse(false);
	}
}