package org.zeith.tech.api.enums;

import net.minecraft.util.StringRepresentable;

public enum ParentingMode
		implements StringRepresentable
{
	ABSOLUTE("absolute"),
	RELATIVE("relative");
	
	final String name;
	
	ParentingMode(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getSerializedName()
	{
		return name;
	}
}