package org.zeith.tech.api.events.recipe;

import net.minecraftforge.eventbus.api.Event;
import org.zeith.tech.api.enums.TechTier;

import java.util.*;

public class GrindingRegistryEvent
		extends Event
{
	private final Set<String> blockedTypes;
	private final Map<String, Integer> materialTimeOverride;
	private final Map<String, TechTier> minTierOverride;
	
	public GrindingRegistryEvent(Set<String> blockedTypes, Map<String, Integer> materialTimeOverride, Map<String, TechTier> minTierOverride)
	{
		this.blockedTypes = blockedTypes;
		this.materialTimeOverride = materialTimeOverride;
		this.minTierOverride = minTierOverride;
	}
	
	public Set<String> getBlockedTypes()
	{
		return Collections.unmodifiableSet(blockedTypes);
	}
	
	public Map<String, Integer> getMaterialTimeOverride()
	{
		return Collections.unmodifiableMap(materialTimeOverride);
	}
	
	public Map<String, TechTier> getMaterialTierOverride()
	{
		return Collections.unmodifiableMap(minTierOverride);
	}
	
	public int getTimeForMaterial(String metal)
	{
		return materialTimeOverride.getOrDefault(metal, 200);
	}
	
	public TechTier getMaterialTier(String metal)
	{
		return minTierOverride.getOrDefault(metal, TechTier.BASIC);
	}
	
	public void blockMaterial(String metal)
	{
		blockedTypes.add(metal);
	}
	
	public void unblockMaterial(String metal)
	{
		blockedTypes.remove(metal);
	}
	
	public void setTimeForMaterial(String metal, int hits)
	{
		materialTimeOverride.put(metal, hits);
	}
	
	public void setMaterialTier(String metal, TechTier tier)
	{
		minTierOverride.put(metal, tier);
	}
}