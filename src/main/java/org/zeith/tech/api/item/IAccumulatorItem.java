package org.zeith.tech.api.item;

import net.minecraft.world.item.ItemStack;

public interface IAccumulatorItem
{
	int getEnergy(ItemStack stack);
	
	int getMaxEnergy(ItemStack stack);
	
	void setEnergy(ItemStack stack, int energy);
	
	default int storeEnergy(ItemStack stack, int energy, boolean simulate)
	{
		var fe = getEnergy(stack);
		int rcv = Math.min(energy, getMaxEnergy(stack) - fe);
		if(!simulate) setEnergy(stack, fe + rcv);
		return rcv;
	}
	
	default int takeEnergy(ItemStack stack, int energy, boolean simulate)
	{
		var fe = getEnergy(stack);
		int ext = Math.min(energy, fe);
		if(!simulate) setEnergy(stack, fe - ext);
		return ext;
	}
	
	default boolean takeEnergyWhole(ItemStack stack, int energy, boolean simulate)
	{
		var fe = getEnergy(stack);
		if(fe < energy) return false;
		if(!simulate) setEnergy(stack, fe - energy);
		return true;
	}
}