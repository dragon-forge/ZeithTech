package org.zeith.tech.modules.shared.init;

import net.minecraft.sounds.SoundEvent;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.core.registrar.SoundRegistrar;
import org.zeith.tech.modules.generators.init.SoundsZT_Generators;
import org.zeith.tech.modules.processing.init.SoundsZT_Processing;

public interface SoundsZT
		extends SoundsZT_Processing, SoundsZT_Generators
{
	SoundEvent ANVIL_DESTROY = register("block.anvil.destroy");
	SoundEvent ANVIL_USE = register("block.anvil.use");
	SoundEvent ANVIL_USE_DONE = register("block.anvil.use.done");
	
	static SoundEvent register(String s)
	{
		return SoundRegistrar.alloc(() -> new SoundEvent(ZeithTechAPI.id(s)));
	}
	
	static void setup()
	{
	}
}