package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.registrar.SoundRegistrar;

public interface SoundsZT_Processing
{
	SoundEvent BASIC_MACHINE_INTERRUPT = register("block.basic_interrupt");
	SoundEvent BASIC_FUEL_GENERATOR = register("block.fuel_generator.basic");
	SoundEvent BASIC_LIQUID_FUEL_GENERATOR = register("block.lfuel_generator.basic");
	SoundEvent WASTE_PROCESSOR = register("block.waste_processor");
	SoundEvent BASIC_ELECTRIC_FURNACE = register("block.electric_furnace.basic");
	SoundEvent BASIC_GRINDER = register("block.grinder.basic");
	SoundEvent BASIC_SAWMILL = register("block.sawmill.basic");
	SoundEvent FLUID_PUMP = register("block.fluid_pump.basic");
	SoundEvent FLUID_CENTRIFUGE = register("block.fluid_centrifuge.basic");
	
	static SoundEvent register(String s)
	{
		return SoundRegistrar.alloc(() -> new SoundEvent(new ResourceLocation(ZeithTech.MOD_ID, "processing." + s)));
	}
	
	static void setup()
	{
	}
}