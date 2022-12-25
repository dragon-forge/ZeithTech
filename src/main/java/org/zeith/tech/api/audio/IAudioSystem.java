package org.zeith.tech.api.audio;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.zeith.tech.api.tile.IEnableableTile;

/**
 * An interface for a system that plays sounds.
 */
public interface IAudioSystem
{
	/**
	 * Plays looping sound (workSound), and if tile is no longer active, fades out the sound.
	 * If the tile gets interrupted while active, interruptSound plays (if present).
	 *
	 * @param <T>
	 * 		the type of block entity that is implementing the IEnableableTile interface
	 * @param tile
	 * 		the block entity that is implementing the IEnableableTile interface
	 * @param workSound
	 * 		the sound to play while the tile is active
	 * @param interruptSound
	 * 		the sound to play if the tile gets interrupted while active
	 */
	<T extends BlockEntity & IEnableableTile> void playMachineSoundLoop(T tile, SoundEvent workSound, SoundEvent interruptSound);
	
	/**
	 * Plays a sound at the given tile's position.
	 *
	 * @param tile
	 * 		the block entity at which the sound should be played
	 * @param sound
	 * 		the sound to play
	 * @param volume
	 * 		the volume of the sound
	 * @param pitch
	 * 		the pitch of the sound
	 */
	void playTileSound(BlockEntity tile, SoundEvent sound, float volume, float pitch);
	
	/**
	 * Plays a sound at the given position.
	 *
	 * @param level
	 * 		the level in which the sound should be played
	 * @param pos
	 * 		the position at which the sound should be played
	 * @param sound
	 * 		the sound to play
	 * @param volume
	 * 		the volume of the sound
	 * @param pitch
	 * 		the pitch of the sound
	 */
	void playPositionedSound(Level level, BlockPos pos, SoundEvent sound, float volume, float pitch);
}