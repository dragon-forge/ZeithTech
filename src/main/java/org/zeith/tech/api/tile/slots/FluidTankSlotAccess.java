package org.zeith.tech.api.tile.slots;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.misc.IColorProvider;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class FluidTankSlotAccess
		implements ISlotAccess<FluidStack>
{
	protected final FluidTank tank;
	protected final SlotRole role;
	protected Color overrideColor;
	
	public FluidTankSlotAccess(FluidTank tank, SlotRole role)
	{
		this.tank = tank;
		this.role = role;
	}
	
	public FluidTankSlotAccess setOverrideColor(Color overrideColor)
	{
		this.overrideColor = overrideColor;
		return this;
	}
	
	public FluidTankSlotAccess setOverrideColor(int overrideColor)
	{
		this.overrideColor = new Color(overrideColor, false);
		return this;
	}
	
	@Override
	public Optional<Color> getColorOverride()
	{
		return Optional.ofNullable(overrideColor)
				.or(() -> Cast.optionally(tank, IColorProvider.class)
						.map(IColorProvider::getColor));
	}
	
	@Override
	public SlotType<FluidStack> getType()
	{
		return SlotType.FLUID;
	}
	
	@Override
	public FluidStack get()
	{
		return tank.getFluid();
	}
	
	@Override
	public void set(FluidStack val)
	{
		tank.setFluid(val);
	}
	
	@Override
	public int insert(FluidStack val, boolean simulate)
	{
		return tank.fill(val, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
	}
	
	@Override
	public FluidStack extract(int amount, boolean simulate)
	{
		return tank.drain(amount, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
	}
	
	@Override
	public int getAmount()
	{
		return tank.getFluidAmount();
	}
	
	@Override
	public int getMaxAmount()
	{
		return tank.getCapacity();
	}
	
	@Override
	public boolean belongsTo(Object owner)
	{
		return owner == tank;
	}
	
	private final LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> new IFluidHandler()
	{
		@Override
		public int getTanks()
		{
			return 1;
		}
		
		@Override
		public @NotNull FluidStack getFluidInTank(int tank)
		{
			return get();
		}
		
		@Override
		public int getTankCapacity(int tank)
		{
			return getMaxAmount();
		}
		
		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack)
		{
			return FluidTankSlotAccess.this.tank.isFluidValid(stack);
		}
		
		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			return role.input() ? insert(resource, action.simulate()) : 0;
		}
		
		@Override
		public @NotNull FluidStack drain(FluidStack resource, FluidAction action)
		{
			if(role.output() && get().isFluidEqual(resource)) return extract(resource.getAmount(), action.simulate());
			return FluidStack.EMPTY;
		}
		
		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction action)
		{
			if(role.output()) return extract(maxDrain, action.simulate());
			return FluidStack.EMPTY;
		}
	});
	
	@Override
	public @NotNull <CAP> LazyOptional<CAP> getCapability(@NotNull Capability<CAP> cap)
	{
		if(cap == ForgeCapabilities.FLUID_HANDLER)
			return fluidCapability.cast();
		return ISlotAccess.super.getCapability(cap);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		var that = (FluidTankSlotAccess) obj;
		return Objects.equals(this.tank, that.tank) && Objects.equals(this.role, that.role);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(tank, role);
	}
	
	@Override
	public String toString()
	{
		return "FluidTankSlotAccess{" +
				"tank=" + tank +
				", role=" + role +
				'}';
	}
}