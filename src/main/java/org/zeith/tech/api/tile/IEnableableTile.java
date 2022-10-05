package org.zeith.tech.api.tile;

public interface IEnableableTile
{
	boolean isEnabled();
	
	/**
	 * True if a tile was interrupted (ex. machine running out of energy)
	 */
	default boolean isInterrupted()
	{
		return false;
	}
}