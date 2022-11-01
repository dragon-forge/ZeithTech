package org.zeith.tech.api.misc.farm;

import net.minecraftforge.items.IItemHandlerModifiable;

public enum EnumFarmItemCategory
{
	SOIL,
	PLANT,
	FERTILIZER,
	UNKNOWN;
	
	public boolean isKnown()
	{
		return this != UNKNOWN;
	}
	
	public IItemHandlerModifiable inventoryOf(IFarmController farm)
	{
		return farm.getInventory(this);
	}
}