package org.zeith.tech.modules.shared.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.ZeithTech;

@SimplyRegister
public interface SoundsZT
{
	@RegistryName("block.anvil.destroy")
	SoundEvent ANVIL_DESTROY = register("block.anvil.destroy");
	
	@RegistryName("block.anvil.use")
	SoundEvent ANVIL_USE = register("block.anvil.use");
	
	@RegistryName("block.anvil.use.done")
	SoundEvent ANVIL_USE_DONE = register("block.anvil.use.done");
	
	static SoundEvent register(String s)
	{
		return new SoundEvent(new ResourceLocation(ZeithTech.MOD_ID, s));
	}
}