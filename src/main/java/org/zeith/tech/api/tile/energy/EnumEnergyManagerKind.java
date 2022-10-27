package org.zeith.tech.api.tile.energy;

public enum EnumEnergyManagerKind
{
	CONSUMER(true, false),
	GENERATOR(false, true),
	ENERGY_CELL(true, true);
	
	final boolean consumesEnergy, generatesEnergy;
	
	EnumEnergyManagerKind(boolean consumesEnergy, boolean generatesEnergy)
	{
		this.consumesEnergy = consumesEnergy;
		this.generatesEnergy = generatesEnergy;
	}
	
	public boolean consumesEnergy()
	{
		return consumesEnergy;
	}
	
	public boolean generatesEnergy()
	{
		return generatesEnergy;
	}
}