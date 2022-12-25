package org.zeith.tech.api.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * A utility class for storing block state properties used by ZeithTech blocks.
 */
public class ZeithTechStateProperties
{
	private ZeithTechStateProperties()
	{
		// Private constructor to prevent instantiation
	}
	
	/**
	 * A boolean property representing whether a block is logged with crude oil.
	 */
	public static final BooleanProperty CRUDE_OIL_LOGGED = BooleanProperty.create("crude_oil_logged");
	/**
	 * A boolean property representing whether a block is logged with lava.
	 */
	public static final BooleanProperty LAVA_LOGGED = BooleanProperty.create("lavalogged");
	/**
	 * A boolean property representing whether a block is offset.
	 */
	public static final BooleanProperty OFFSET = BooleanProperty.create("offset");
	/**
	 * A boolean property representing whether a block is visible.
	 */
	public static final BooleanProperty VISIBLE = BooleanProperty.create("visible");
	/**
	 * An integer property representing the light level of a block.
	 */
	public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light", 0, 15);
}