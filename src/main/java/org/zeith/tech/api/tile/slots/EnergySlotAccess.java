package org.zeith.tech.api.tile.slots;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.tech.api.tile.energy.EnergyManager;

import java.util.Objects;

public final class EnergySlotAccess
		implements ISlotAccess<FECharge>
{
	private final EnergyManager storage;
	private final SlotRole role;
	
	public EnergySlotAccess(EnergyManager storage, SlotRole role)
	{
		this.storage = storage;
		this.role = role;
	}
	
	@Override
	public SlotType<FECharge> getType()
	{
		return SlotType.ENERGY;
	}
	
	@Override
	public FECharge get()
	{
		return new FECharge(storage.getEnergyStored());
	}
	
	@Override
	public void set(FECharge val)
	{
		storage.fe.setEnergyStored(val.FE);
	}
	
	@Override
	public int insert(FECharge val, boolean simulate)
	{
		return storage.receiveEnergy(val.FE, simulate);
	}
	
	@Override
	public FECharge extract(int amount, boolean simulate)
	{
		return new FECharge(storage.extractEnergy(amount, simulate));
	}
	
	@Override
	public int getAmount()
	{
		return storage.getEnergyStored();
	}
	
	@Override
	public int getMaxAmount()
	{
		return storage.getMaxEnergyStored();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		var that = (EnergySlotAccess) obj;
		return Objects.equals(this.storage, that.storage) &&
				Objects.equals(this.role, that.role);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(storage, role);
	}
	
	@Override
	public String toString()
	{
		return "EnergySlotAccess[" +
				"storage=" + storage + ", " +
				"role=" + role + ']';
	}
	
	private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> new IEnergyStorage()
	{
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate)
		{
			return role.input() ? storage.receiveEnergy(maxReceive, simulate) : 0;
		}
		
		@Override
		public int extractEnergy(int maxExtract, boolean simulate)
		{
			return role.output() ? storage.extractEnergy(maxExtract, simulate) : 0;
		}
		
		@Override
		public int getEnergyStored()
		{
			return getAmount();
		}
		
		@Override
		public int getMaxEnergyStored()
		{
			return getMaxAmount();
		}
		
		@Override
		public boolean canExtract()
		{
			return role.output();
		}
		
		@Override
		public boolean canReceive()
		{
			return role.input();
		}
	});
	
	@Override
	public @NotNull <CAP> LazyOptional<CAP> getCapability(@NotNull Capability<CAP> cap)
	{
		if(cap == ForgeCapabilities.ENERGY)
			return energyCapability.cast();
		return ISlotAccess.super.getCapability(cap);
	}
}