package org.zeith.tech.api.energy;

import org.zeith.tech.core.ZeithTech;

/**
 * This defines the energy tiers for the mod.
 * You may add more if needed, but these are the voltages used by {@link ZeithTech}.
 * Cables in this mod have one of these five transfer rates and are subject to burning, if overpowered.
 */
public record EnergyTier(int maxTransfer, int capacity)
{
	public static final EnergyTier EXTRA_LOW_VOLTAGE = new EnergyTier(64, 20_000);
	public static final EnergyTier LOW_VOLTAGE = new EnergyTier(128, 40_000);
	public static final EnergyTier MEDIUM_VOLTAGE = new EnergyTier(512, 160_000);
	public static final EnergyTier HIGH_VOLTAGE = new EnergyTier(4096, 512_000);
	public static final EnergyTier EXTRA_HIGH_VOLTAGE = new EnergyTier(32768, 1_024_000);
}