package org.zeith.tech.utils.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Predicate;

public class FluidHelperZT
{
	public static boolean anyFluidMatches(ItemStack stack, Predicate<FluidStack> filter)
	{
		return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(h ->
		{
			var slots = h.getTanks();
			for(int i = 0; i < slots; ++i)
				if(filter.test(h.getFluidInTank(i)))
					return true;
			return false;
		}).orElse(false);
	}
	
	public static boolean allFluidsMatch(ItemStack stack, Predicate<FluidStack> filter)
	{
		return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(h ->
		{
			var slots = h.getTanks();
			for(int i = 0; i < slots; ++i)
				if(!filter.test(h.getFluidInTank(i)))
					return false;
			return true;
		}).orElse(false);
	}
	
	public static boolean noneFluidsMatch(ItemStack stack, Predicate<FluidStack> filter)
	{
		return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(h ->
		{
			var slots = h.getTanks();
			for(int i = 0; i < slots; ++i)
				if(filter.test(h.getFluidInTank(i)))
					return false;
			return true;
		}).orElse(false);
	}
	
	public static boolean isFluidContainerEmpty(ItemStack stack)
	{
		return allFluidsMatch(stack, FluidStack::isEmpty);
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