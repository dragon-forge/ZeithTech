package org.zeith.tech.api.tile.slots;

public enum SlotRole
{
	NONE(false, false),
	INPUT(true, false),
	OUTPUT(false, true),
	BOTH(true, true);
	
	final boolean allowInput, allowOutput;
	
	SlotRole(boolean allowInput, boolean allowOutput)
	{
		this.allowInput = allowInput;
		this.allowOutput = allowOutput;
	}
	
	public boolean input()
	{
		return allowInput;
	}
	
	public boolean output()
	{
		return allowOutput;
	}
}