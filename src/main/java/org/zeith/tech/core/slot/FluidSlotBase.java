package org.zeith.tech.core.slot;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.awt.*;

public abstract class FluidSlotBase
{
	public int x, y;
	
	protected FluidSlotBase(int x, int y)
	{
		this.x = x - 1;
		this.y = y - 1;
		fluidSlotRect.setRect(this.x, this.y, 18, 66);
	}
	
	public abstract FluidStack getFluid();
	
	public abstract int getCapacity();
	
	protected Rectangle fluidSlotRect = new Rectangle();
	
	public boolean isHovered(double mouseX, double mouseY)
	{
		return fluidSlotRect.contains(mouseX, mouseY);
	}
	
	public static class FluidHandlerSlot
			extends FluidSlotBase
	{
		public final IFluidHandler handler;
		public final int tank;
		
		public FluidHandlerSlot(IFluidHandler handler, int tank, int x, int y)
		{
			super(x, y);
			this.handler = handler;
			this.tank = tank;
		}
		
		@Override
		public FluidStack getFluid()
		{
			return handler.getFluidInTank(tank);
		}
		
		@Override
		public int getCapacity()
		{
			return handler.getTankCapacity(tank);
		}
	}
	
	public static class FluidTankSlot
			extends FluidSlotBase
	{
		public final IFluidTank tank;
		
		public FluidTankSlot(IFluidTank tank, int x, int y)
		{
			super(x, y);
			this.tank = tank;
		}
		
		@Override
		public FluidStack getFluid()
		{
			return tank.getFluid();
		}
		
		@Override
		public int getCapacity()
		{
			return tank.getCapacity();
		}
	}
}