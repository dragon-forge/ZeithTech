package org.zeith.tech.core.audio;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.zeith.tech.api.audio.IAudioSystem;
import org.zeith.tech.api.tile.IEnableableTile;

public class CommonAudioSystem
		implements IAudioSystem
{
	@Override
	public <T extends BlockEntity & IEnableableTile> void playMachineSoundLoop(T tile, SoundEvent workSound, SoundEvent interruptSound)
	{
	}
	
	@Override
	public void playTileSound(BlockEntity tile, SoundEvent sound, float volume, float pitch)
	{
		if(tile != null && tile.getLevel() instanceof ServerLevel srv)
		{
			Vec3 pp = Vec3.atCenterOf(tile.getBlockPos());
			srv.playSound(null, pp.x, pp.y, pp.z, sound, SoundSource.BLOCKS, volume, pitch);
		}
	}
	
	@Override
	public void playPositionedSound(Level level, BlockPos pos, SoundEvent sound, float volume, float pitch)
	{
		if(level instanceof ServerLevel srv)
		{
			Vec3 pp = Vec3.atCenterOf(pos);
			srv.playSound(null, pp.x, pp.y, pp.z, sound, SoundSource.BLOCKS, volume, pitch);
		}
	}
}