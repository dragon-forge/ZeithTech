package org.zeith.tech.core.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.zeith.tech.api.tile.IEnableableTile;
import org.zeith.tech.modules.shared.client.resources.sounds.MachineSoundInstance;

public class ClientAudioSystem
		extends CommonAudioSystem
{
	{
		MinecraftForge.EVENT_BUS.addListener(this::clientTick);
	}
	
	private void clientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase != TickEvent.Phase.START) return;
		
		var mc = Minecraft.getInstance();
		
		if(mc.level == null)
		{
			// When in world, we do not need
			MachineSoundInstance.POSITIONED_SOUNDS.clear();
		}
	}
	
	@Override
	public <T extends BlockEntity & IEnableableTile> void playMachineSoundLoop(T tile, SoundEvent workSound, SoundEvent interruptSound)
	{
		if(tile != null && tile.getLevel() instanceof ClientLevel cl)
		{
			// Starts a sound if it is not yet started.
			MachineSoundInstance.startFor(tile, workSound, interruptSound);
			return;
		}
		
		super.playMachineSoundLoop(tile, workSound, interruptSound);
	}
	
	@Override
	public void playTileSound(BlockEntity tile, SoundEvent sound, float volume, float pitch)
	{
		if(tile != null && tile.getLevel() instanceof ClientLevel cl)
		{
			cl.playLocalSound(tile.getBlockPos(), sound, SoundSource.BLOCKS, volume, pitch, true);
			return;
		}
		
		super.playTileSound(tile, sound, volume, pitch);
	}
	
	@Override
	public void playPositionedSound(Level level, BlockPos pos, SoundEvent sound, float volume, float pitch)
	{
		if(level instanceof ClientLevel cl)
		{
			cl.playLocalSound(pos, sound, SoundSource.BLOCKS, volume, pitch, true);
			return;
		}
		
		super.playPositionedSound(level, pos, sound, volume, pitch);
	}
}
