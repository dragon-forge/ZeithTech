package org.zeith.tech.api.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ZeithTechStateProperties
{
	public static final BooleanProperty CRUDE_OIL_LOGGED = BooleanProperty.create("crude_oil_logged");
	public static final BooleanProperty LAVA_LOGGED = BooleanProperty.create("lavalogged");
	
	public static final BooleanProperty OFFSET = BooleanProperty.create("offset");
	
	public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light", 0, 15);
}