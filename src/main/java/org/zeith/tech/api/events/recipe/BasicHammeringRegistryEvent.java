package org.zeith.tech.api.events.recipe;

import net.minecraftforge.eventbus.api.Event;
import org.zeith.tech.api.enums.TechTier;

import java.util.*;

public class BasicHammeringRegistryEvent
		extends Event
{
	private final Set<String> blockedMetals;
	private final Map<String, Integer> materialHitOverride;
	private final Map<String, TechTier> minTierOverride;
	
	public BasicHammeringRegistryEvent(Set<String> blockedMetals, Map<String, Integer> materialHitOverride, Map<String, TechTier> minTierOverride)
	{
		this.blockedMetals = blockedMetals;
		this.materialHitOverride = materialHitOverride;
		this.minTierOverride = minTierOverride;
	}
	
	public Set<String> getBlockedMetals()
	{
		return Collections.unmodifiableSet(blockedMetals);
	}
	
	public Map<String, Integer> getMaterialHitOverride()
	{
		return Collections.unmodifiableMap(materialHitOverride);
	}
	
	public Map<String, TechTier> getMaterialTierOverride()
	{
		return Collections.unmodifiableMap(minTierOverride);
	}
	
	public int getHitsForMetal(String metal)
	{
		return materialHitOverride.getOrDefault(metal, 4);
	}
	
	public TechTier getMaterialTier(String metal)
	{
		return minTierOverride.getOrDefault(metal, TechTier.BASIC);
	}
	
	public void blockMetal(String metal)
	{
		blockedMetals.add(metal);
	}
	
	public void unblockMetal(String metal)
	{
		blockedMetals.remove(metal);
	}
	
	public void setHitsForMetal(String metal, int hits)
	{
		materialHitOverride.put(metal, hits);
	}
	
	public void setMaterialTier(String metal, TechTier tier)
	{
		minTierOverride.put(metal, tier);
	}
}