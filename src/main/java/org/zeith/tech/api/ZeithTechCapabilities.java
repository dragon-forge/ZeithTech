package org.zeith.tech.api;

import net.minecraftforge.common.capabilities.*;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.IEnergyMeasurable;
import org.zeith.tech.api.tile.facade.FacadeData;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.slots.ITileSlotProvider;

/**
 * Class that holds the capabilities used by Zeith Tech.
 */
public class ZeithTechCapabilities
{
	/**
	 * Capability for handling sided configuration of tiles.
	 */
	public static final Capability<ITileSidedConfig> SIDED_CONFIG = CapabilityManager.get(new CapabilityToken<>()
	{
	});
	
	/**
	 * Capability for measuring the energy of a tile.
	 */
	public static final Capability<IEnergyMeasurable> ENERGY_MEASURABLE = CapabilityManager.get(new CapabilityToken<>()
	{
	});
	
	/**
	 * Capability for providing slots to a tile.
	 */
	public static final Capability<ITileSlotProvider> TILE_SLOT_PROVIDER = CapabilityManager.get(new CapabilityToken<>()
	{
	});
	
	/**
	 * Capability for handling redstone control of a tile.
	 */
	public static final Capability<RedstoneControl> REDSTONE_CONTROL = CapabilityManager.get(new CapabilityToken<>()
	{
	});
	
	/**
	 * Capability for handling facades of a tile.
	 */
	public static final Capability<FacadeData> FACADES = CapabilityManager.get(new CapabilityToken<>()
	{
	});
}