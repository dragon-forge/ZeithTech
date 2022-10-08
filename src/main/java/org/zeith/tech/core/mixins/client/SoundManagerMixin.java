package org.zeith.tech.core.mixins.client;

import com.google.common.collect.Maps;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.tech.core.audio.IPredictableSoundManager;
import org.zeith.tech.core.audio.VorbisDuration;

import java.io.IOException;
import java.util.Map;

@Mixin(SoundManager.class)
@Implements({
		@Interface(iface = IPredictableSoundManager.class, prefix = "ipsm$")
})
public class SoundManagerMixin
{
	private final Map<ResourceLocation, Double> vorbisDurations = Maps.newHashMap();
	
	private ResourceManager manager;
	
	@Inject(
			method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/client/sounds/SoundManager$Preparations;",
			at = @At("HEAD")
	)
	public void resetVorbisDurations(ResourceManager manager, ProfilerFiller p_120357_, CallbackInfoReturnable cir)
	{
		this.manager = manager;
		vorbisDurations.clear();
	}
	
	public double ipsm$getAudioFileDurationInSeconds(Sound sound)
	{
		return vorbisDurations.computeIfAbsent(sound.getPath(), path ->
		{
			var res = manager.getResource(path);
			if(res.isEmpty()) return -1D;
			try(var in = res.orElseThrow().open())
			{
				return VorbisDuration.calculateDuration(in);
			} catch(IOException e)
			{
				e.printStackTrace();
			}
			return -1D;
		});
	}
}