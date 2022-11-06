package org.zeith.tech.core.cfg;

import org.zeith.hammerlib.api.config.*;

@Config(module = "balance")
public class ZeithTechBalanceConfigs
		implements IConfigRoot
{
	public static final ConfigHolder<ZeithTechBalanceConfigs> INSTANCE = new ConfigHolder<>();
	
	@Config.ConfigEntry(entry = "Vanilla Tweaks", comment = "Change how the mod alters the vanilla recipes.")
	public final VanillaTweaks vanilla = new VanillaTweaks();
	
	public static class VanillaTweaks
			implements IConfigStructure
	{
		@Config.BooleanEntry(true)
		@Config.ConfigEntry(entry = "Netherite Tweaks", comment = "Should ZeithTech replace the recipe of netherite from 4 scraps + 4 gold ingots into a blast furnace recipe of 1 scrap + 1 gold block?")
		public boolean netheriteTweaks = true;
	}
}