package org.zeith.tech.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class SerializableFluidTank
		extends FluidTank
		implements INBTSerializable<CompoundTag>
{
	public SerializableFluidTank(int capacity)
	{
		super(capacity);
	}
	
	public SerializableFluidTank(int capacity, Predicate<FluidStack> validator)
	{
		super(capacity, validator);
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		return writeToNBT(new CompoundTag());
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		readFromNBT(nbt);
	}
}
