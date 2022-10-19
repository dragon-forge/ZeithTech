package org.zeith.tech.core.cfg;

import org.zeith.hammerlib.api.config.*;

@Config(module = "modules")
public class ZeithTechModuleConfigs
		implements IConfigRoot
{
	public static final ConfigHolder<ZeithTechModuleConfigs> INSTANCE = new ConfigHolder<>();
	
	@Config.ConfigEntry(entry = "States", comment = "States of all ZeithTech modules. From here you can enable/disable each module. Disabling a module usually results in missing recipes, but all items/blocks are still in-game.")
	@Config.AvoidSync
	public final ModuleStates states = new ModuleStates();
	
	public static class ModuleStates
			implements IConfigStructure
	{
		@Config.ConfigEntry(entry = "Shared", comment = "Enable 'Shared' module (mostly crafting materials)?")
		@Config.BooleanEntry(true)
		public boolean sharedModule = true;
		
		@Config.ConfigEntry(entry = "World", comment = "Enable 'World' module (worldgen, recipes, etc)?")
		@Config.BooleanEntry(true)
		public boolean worldModule = true;
		
		@Config.ConfigEntry(entry = "Transport", comment = "Enable 'Transport' module (pipes, wires, multimeter)?")
		@Config.BooleanEntry(true)
		public boolean transportModule = true;
		
		@Config.ConfigEntry(entry = "Processing", comment = "Enable 'Processing' module (machines)?")
		@Config.BooleanEntry(true)
		public boolean processingModule = true;
		
		@Config.ConfigEntry(entry = "Generators", comment = "Enable 'Generator' module (FE-producing machines)?")
		@Config.BooleanEntry(true)
		public boolean generatorsModule = true;
	}
}