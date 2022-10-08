package org.zeith.tech.core.mixins.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.tech.core.audio.IExtraSoundGenerator;

import java.util.List;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin
{
	@Shadow
	@Final
	private List<TickableSoundInstance> tickingSounds;
	
	@Shadow
	public abstract void play(SoundInstance p_120313_);
	
	@Inject(
			method = "tickNonPaused",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
					),
					to = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/Options;getSoundSourceVolume(Lnet/minecraft/sounds/SoundSource;)F"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map;entrySet()Ljava/util/Set;"
			)
	)
	public void tickNonPausedWithExtra_ZT(CallbackInfo ci)
	{
		List<TickableSoundInstance> sounds = this.tickingSounds;
		int ss = sounds.size();
		for(int i = 0; i < ss; i++)
		{
			var snd = sounds.get(i);
			if(snd instanceof IExtraSoundGenerator gen)
				gen.playQueuedSound(this::play);
		}
	}
}