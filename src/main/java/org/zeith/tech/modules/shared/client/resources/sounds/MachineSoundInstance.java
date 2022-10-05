package org.zeith.tech.modules.shared.client.resources.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.GlobalPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.zeith.tech.api.tile.IEnableableTile;
import org.zeith.tech.core.audio.IExtraSoundGenerator;
import org.zeith.tech.core.audio.IPredictableSoundManager;

import java.util.*;
import java.util.function.Consumer;

public class MachineSoundInstance<T extends BlockEntity & IEnableableTile>
		extends AbstractTickableSoundInstance
		implements IExtraSoundGenerator
{
	public static final Map<GlobalPos, MachineSoundInstance<?>> POSITIONED_SOUNDS = new HashMap<>();
	
	public final T tile;
	
	public final SoundEvent workSound;
	public final SoundEvent interruptSound;
	public final SoundManager soundManager;
	
	public SimpleSoundInstanceTickable activeSound;
	
	protected MachineSoundInstance(SoundEvent workSound, SoundEvent interruptSound, SoundSource source, SoundManager soundManager, T tile)
	{
		super(workSound, source, SoundInstance.createUnseededRandom());
		this.workSound = workSound;
		this.interruptSound = interruptSound;
		this.soundManager = soundManager;
		this.tile = Objects.requireNonNull(tile, "tile");
		this.volume = 0F;
		this.pitch = 1F;
		this.looping = true;
		var pos = Vec3.atCenterOf(tile.getBlockPos());
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}
	
	public static <T extends BlockEntity & IEnableableTile> void startFor(T tile, SoundEvent sound, SoundEvent interruptSound)
	{
		var soundManager = Minecraft.getInstance().getSoundManager();
		
		var pos = GlobalPos.of(tile.getLevel().dimension(), tile.getBlockPos());
		var active = POSITIONED_SOUNDS.get(pos);
		
		if(active == null || !soundManager.isActive(active))
		{
			active = new MachineSoundInstance<>(sound, interruptSound, SoundSource.BLOCKS, soundManager, tile);
			soundManager.queueTickingSound(active);
			POSITIONED_SOUNDS.put(pos, active);
		}
	}
	
	private double durationInTicks;
	private int aliveTicks;
	
	@Override
	public boolean canPlaySound()
	{
		return true;
	}
	
	@Override
	public boolean canStartSilent()
	{
		return true;
	}
	
	@Override
	public void tick()
	{
	}
	
	@Override
	public void playQueuedSound(Consumer<SoundInstance> player)
	{
		if(tile == null || tile.isRemoved())
		{
			stop();
			activeSound = null;
			return;
		}
		
		if(tile.isEnabled())
		{
			if(tile.isInterrupted())
			{
				stop();
				activeSound = null;
				if(interruptSound != null)
					player.accept(createSubSound(interruptSound, 1F, 1F));
			} else
			{
				if(activeSound == null || !soundManager.isActive(activeSound) || (durationInTicks - aliveTicks <= 2))
				{
					if(activeSound != null)
						soundManager.stop(activeSound);
					
					activeSound = createSubSound(workSound, 1F, 1F);
					activeSound.volume = Math.min(activeSound.volume / 0.7F, 0.1F);
					player.accept(activeSound);
					
					aliveTicks = -1;
					durationInTicks = Math.round(((IPredictableSoundManager) soundManager).getAudioFileDurationInSeconds(activeSound.getSound()) * 20);
				} else
					activeSound.volume = Math.min(activeSound.volume / 0.7F, 0.1F);
				
				++aliveTicks;
			}
		} else
		{
			if(activeSound != null)
				activeSound.volume *= 0.7F;
			if(activeSound == null || activeSound.volume < 0.001F)
			{
				stop();
				activeSound = null;
			}
		}
	}
	
	private SimpleSoundInstanceTickable createSubSound(SoundEvent sound, float volume, float pitch)
	{
		return new SimpleSoundInstanceTickable(sound, source, volume, pitch, SoundInstance.createUnseededRandom(), getX(), getY(), getZ());
	}
	
	public class SimpleSoundInstanceTickable
			extends SimpleSoundInstance
			implements TickableSoundInstance
	{
		public SimpleSoundInstanceTickable(SoundEvent p_235100_, SoundSource p_235101_, float p_235102_, float p_235103_, RandomSource p_235104_, double p_235105_, double p_235106_, double p_235107_)
		{
			super(p_235100_, p_235101_, p_235102_, p_235103_, p_235104_, p_235105_, p_235106_, p_235107_);
		}
		
		@Override
		public boolean isStopped()
		{
			return activeSound != SimpleSoundInstanceTickable.this;
		}
		
		@Override
		public void tick()
		{
			// NO-OP
		}
	}
}
