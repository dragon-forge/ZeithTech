package org.zeith.tech.modules.shared.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.registrar.SoundRegistrar;
import org.zeith.tech.modules.processing.init.SoundsZT_Processing;

public interface SoundsZT
		extends SoundsZT_Processing
{
	SoundEvent ANVIL_DESTROY = register("block.anvil.destroy");
	SoundEvent ANVIL_USE = register("block.anvil.use");
	SoundEvent ANVIL_USE_DONE = register("block.anvil.use.done");
	
	static SoundEvent register(String s)
	{
		return SoundRegistrar.alloc(() -> new SoundEvent(new ResourceLocation(ZeithTech.MOD_ID, s)));
	}
	
	static void setup()
	{
	}
}