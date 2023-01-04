package org.zeith.tech.modules.processing.init;

import net.minecraft.sounds.SoundEvent;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.core.registrar.SoundRegistrar;

public interface SoundsZT_Processing
{
	SoundEvent BASIC_MACHINE_INTERRUPT = register("block.basic_interrupt");
	SoundEvent WASTE_PROCESSOR = register("block.waste_processor");
	SoundEvent METAL_PRESS_ACT = register("block.metal_press.act");
	SoundEvent BASIC_ELECTRIC_FURNACE = register("block.electric_furnace.basic");
	SoundEvent BASIC_GRINDER = register("block.grinder.basic");
	SoundEvent BASIC_SAWMILL = register("block.sawmill.basic");
	SoundEvent FLUID_PUMP = register("block.fluid_pump.basic");
	SoundEvent FLUID_CENTRIFUGE = register("block.fluid_centrifuge.basic");
	SoundEvent FACADE_SLICER = register("block.facade_slicer");
	
	static SoundEvent register(String s)
	{
		return SoundRegistrar.alloc(() -> SoundEvent.createVariableRangeEvent(ZeithTechAPI.id("processing." + s)));
	}
	
	static void setup()
	{
	}
}