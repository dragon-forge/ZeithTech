package org.zeith.tech.api;

import net.minecraftforge.common.capabilities.*;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.IEnergyMeasurable;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.slots.ITileSlotProvider;

public class ZeithTechCapabilities
{
	public static final Capability<ITileSidedConfig> SIDED_CONFIG = CapabilityManager.get(new CapabilityToken<>()
	{
	});
	
	public static final Capability<IEnergyMeasurable> ENERGY_MEASURABLE = CapabilityManager.get(new CapabilityToken<>()
	{
	});
	
	public static final Capability<ITileSlotProvider> TILE_SLOT_PROVIDER = CapabilityManager.get(new CapabilityToken<>()
	{
	});
	
	public static final Capability<RedstoneControl> REDSTONE_CONTROL = CapabilityManager.get(new CapabilityToken<>()
	{
	});
}