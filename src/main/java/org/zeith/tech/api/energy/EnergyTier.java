package org.zeith.tech.api.energy;

/**
 * This defines the energy tiers for the mod.
 * You may add more if needed, but these are the voltages used by {@link org.zeith.tech.ZeithTech}.
 * Cables in this mod have one of these four transfer rates and are subject to burning, if overpowered.
 */
public record EnergyTier(int maxFE)
{
	public static final EnergyTier LOW_VOLTAGE = new EnergyTier(64);
	public static final EnergyTier MID_VOLTAGE = new EnergyTier(512);
	public static final EnergyTier HIGH_VOLTAGE = new EnergyTier(4096);
	public static final EnergyTier EXTREME_VOLTAGE = new EnergyTier(32768);
}