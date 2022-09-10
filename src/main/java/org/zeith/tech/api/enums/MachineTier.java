package org.zeith.tech.api.enums;

import net.minecraft.util.StringRepresentable;

public enum MachineTier
		implements StringRepresentable
{
	BASIC("basic"),
	ADVANCED("advanced"),
	QUANTUM("quantum");
	
	final String name;
	
	MachineTier(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getSerializedName()
	{
		return name;
	}
	
	public boolean isOrHigher(MachineTier than)
	{
		return ordinal() >= than.ordinal();
	}
}