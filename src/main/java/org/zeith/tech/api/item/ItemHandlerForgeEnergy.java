package org.zeith.tech.api.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemHandlerForgeEnergy
		implements ICapabilityProvider, IEnergyStorage
{
	private final LazyOptional<IEnergyStorage> cap = LazyOptional.of(() -> this);
	
	protected final ItemStack stack;
	protected final IAccumulatorItem accumulator;
	
	public ItemHandlerForgeEnergy(ItemStack stack, IAccumulatorItem accumulator)
	{
		this.stack = stack;
		this.accumulator = accumulator;
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
	{
		if(cap == ForgeCapabilities.ENERGY) return this.cap.cast();
		return LazyOptional.empty();
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		return getCapability(cap);
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return accumulator.storeEnergy(stack, maxReceive, simulate);
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return accumulator.takeEnergy(stack, maxExtract, simulate);
	}
	
	@Override
	public int getEnergyStored()
	{
		return accumulator.getEnergy(stack);
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return accumulator.getMaxEnergy(stack);
	}
	
	@Override
	public boolean canExtract()
	{
		return true;
	}
	
	@Override
	public boolean canReceive()
	{
		return true;
	}
}
