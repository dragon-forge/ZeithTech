package org.zeith.tech.api.capabilities;

import net.minecraftforge.common.capabilities.*;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;

public class ZeithTechCapabilities
{
	public static final Capability<ITileSidedConfig> SIDED_CONFIG = CapabilityManager.get(new CapabilityToken<>()
	{
	});
}