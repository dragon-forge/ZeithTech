package org.zeith.tech.core.registrar;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.init.SoundsZT_Processing;
import org.zeith.tech.modules.shared.init.SoundsZT;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@SimplyRegister
public class SoundRegistrar
{
	private static final List<SoundEvent> SOUNDS = new ArrayList<>();
	
	static
	{
		SoundsZT.setup();
		SoundsZT_Processing.setup();
	}
	
	public static synchronized SoundEvent alloc(Supplier<SoundEvent> sound)
	{
		var s = sound.get();
		SOUNDS.add(s);
		return s;
	}
	
	@SimplyRegister
	private static void registerSounds(BiConsumer<ResourceLocation, SoundEvent> registry)
	{
		ZeithTech.LOG.info("Registering " + SOUNDS.size() + " SoundEvents.");
		SOUNDS.forEach(snd -> registry.accept(snd.getLocation(), snd));
	}
}