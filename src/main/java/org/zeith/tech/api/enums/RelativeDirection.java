package org.zeith.tech.api.enums;

import net.minecraft.util.StringRepresentable;

public enum RelativeDirection
		implements StringRepresentable
{
	FRONT("front"),
	BACK("back"),
	UP("up"),
	DOWN("down"),
	LEFT("left"),
	RIGHT("right");
	
	final String name;
	
	RelativeDirection(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getSerializedName()
	{
		return name;
	}
}