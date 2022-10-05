package org.zeith.tech.core.audio;

import net.minecraft.client.resources.sounds.SoundInstance;

import java.util.function.Consumer;

public interface IExtraSoundGenerator
{
	void playQueuedSound(Consumer<SoundInstance> player);
}