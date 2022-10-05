package org.zeith.tech.core.audio;

import net.minecraft.client.resources.sounds.Sound;

public interface IPredictableSoundManager
{
	double getAudioFileDurationInSeconds(Sound sound);
}