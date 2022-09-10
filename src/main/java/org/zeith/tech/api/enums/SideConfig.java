package org.zeith.tech.api.enums;

import net.minecraft.util.StringRepresentable;

public enum SideConfig
		implements StringRepresentable
{
	NONE("none"),
	PUSH("push"),
	PULL("pull"),
	DISABLE("disable");
	
	final String name;
	
	SideConfig(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getSerializedName()
	{
		return name;
	}
	
	private static final SideConfig[] VALUES = values();
	
	public static SideConfig byId(int i)
	{
		return VALUES[i];
	}
}
