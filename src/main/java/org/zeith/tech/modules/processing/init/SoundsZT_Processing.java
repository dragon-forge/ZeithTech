package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.core.ZeithTech;

@SimplyRegister
public interface SoundsZT_Processing
{
//	@RegistryName("processing.block.basic_interrupt")
//	SoundEvent BASIC_MACHINE_INTERRUPT = register("processing.block.basic_interrupt");
	
	@RegistryName("processing.block.fuel_generator.basic")
	SoundEvent BASIC_FUEL_GENERATOR = register("processing.block.fuel_generator.basic");
	
	@RegistryName("processing.block.electric_furnace.basic")
	SoundEvent BASIC_ELECTRIC_FURNACE = register("processing.block.electric_furnace.basic");
	
	@RegistryName("processing.block.grinder.basic")
	SoundEvent BASIC_GRINDER = register("processing.block.grinder.basic");
	
	@RegistryName("processing.block.sawmill.basic")
	SoundEvent BASIC_SAWMILL = register("processing.block.sawmill.basic");
	
	static SoundEvent register(String s)
	{
		return new SoundEvent(new ResourceLocation(ZeithTech.MOD_ID, s));
	}
}