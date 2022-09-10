package org.zeith.tech.api.tile.energy;

import net.minecraftforge.energy.EnergyStorage;

public class EnergyStorage2
		extends EnergyStorage
{
	public EnergyStorage2(int capacity)
	{
		super(capacity);
	}
	
	public EnergyStorage2(int capacity, int maxTransfer)
	{
		super(capacity, maxTransfer);
	}
	
	public EnergyStorage2(int capacity, int maxReceive, int maxExtract)
	{
		super(capacity, maxReceive, maxExtract);
	}
	
	public EnergyStorage2(int capacity, int maxReceive, int maxExtract, int energy)
	{
		super(capacity, maxReceive, maxExtract, energy);
	}
	
	public int getEnergyTillFull()
	{
		return this.capacity - this.energy;
	}
	
	public void setEnergyStored(int energy)
	{
		this.energy = Math.max(0, Math.min(capacity, energy));
	}
}