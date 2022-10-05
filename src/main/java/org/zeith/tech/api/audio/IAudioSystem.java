package org.zeith.tech.api.audio;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.zeith.tech.api.tile.IEnableableTile;

public interface IAudioSystem
{
	/**
	 * Plays looping sound (workSound), and if tile is no longer active, fades out the sound.
	 * If the tile gets interrupted while active, interruptSound plays (if present)
	 */
	<T extends BlockEntity & IEnableableTile> void playMachineSoundLoop(T tile, SoundEvent workSound, SoundEvent interruptSound);
	
	/**
	 * Plays a sound at the given tile's position.
	 */
	void playTileSound(BlockEntity tile, SoundEvent sound, float volume, float pitch);
}