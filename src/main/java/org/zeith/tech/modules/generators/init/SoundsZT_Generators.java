package org.zeith.tech.modules.generators.init;

import net.minecraft.sounds.SoundEvent;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.core.registrar.SoundRegistrar;

public interface SoundsZT_Generators
{
	SoundEvent BASIC_FUEL_GENERATOR = register("block.fuel_generator.basic");
	SoundEvent BASIC_LIQUID_FUEL_GENERATOR = register("block.lfuel_generator.basic");
	SoundEvent MAGMATIC_GENERATOR = register("block.magmatic_generator");
	
	static SoundEvent register(String s)
	{
		return SoundRegistrar.alloc(() -> new SoundEvent(ZeithTechAPI.id("generators." + s)));
	}
	
	static void setup()
	{
	}
}