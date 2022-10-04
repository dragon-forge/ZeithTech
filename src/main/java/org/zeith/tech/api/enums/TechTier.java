package org.zeith.tech.api.enums;

import net.minecraft.util.StringRepresentable;

public enum TechTier
		implements StringRepresentable
{
	BASIC("basic"),
	ADVANCED("advanced"),
	QUANTUM("quantum");
	
	final String name;
	
	TechTier(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getSerializedName()
	{
		return name;
	}
	
	public boolean isOrHigher(TechTier than)
	{
		return ordinal() >= than.ordinal();
	}
	
	public boolean isOrLower(TechTier than)
	{
		return ordinal() <= than.ordinal();
	}
}