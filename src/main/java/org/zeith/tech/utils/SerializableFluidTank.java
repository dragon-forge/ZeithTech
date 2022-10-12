package org.zeith.tech.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.zeith.tech.api.misc.IColorProvider;
import org.zeith.tech.api.misc.SampleColorGenerator;

import java.awt.*;
import java.util.function.Predicate;

public class SerializableFluidTank
		extends FluidTank
		implements INBTSerializable<CompoundTag>, IColorProvider
{
	protected Color color;
	
	public SerializableFluidTank(int capacity)
	{
		super(capacity);
		this.color = SampleColorGenerator.generateRandomColor(SampleColorGenerator.getCaller());
	}
	
	public SerializableFluidTank(int capacity, Predicate<FluidStack> validator)
	{
		super(capacity, validator);
		this.color = SampleColorGenerator.generateRandomColor(SampleColorGenerator.getCaller());
	}
	
	public SerializableFluidTank withColor(Color color)
	{
		this.color = color;
		return this;
	}
	
	public SerializableFluidTank withColor(int color)
	{
		this.color = new Color(color, false);
		return this;
	}
	
	@Override
	public Color getColor()
	{
		return color;
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
