package org.zeith.tech.core.cfg;

import org.zeith.hammerlib.api.config.*;

@Config(module = "transport")
public class ZeithTechTransportConfigs
		implements IConfigRoot
{
	public static final ConfigHolder<ZeithTechTransportConfigs> INSTANCE = new ConfigHolder<>();
	
	@Config.ConfigEntry(entry = "Main", comment = "Main configs for transport module.")
	@Config.AvoidSync
	public final TransportMain main = new TransportMain();
	
	public static class TransportMain
			implements IConfigStructure
	{
		@Config.ConfigEntry(entry = "Facades in JEI", comment = "Should facades be shown in JEI?")
		@Config.BooleanEntry(true)
		public boolean facadesInJEI = true;
	}
}