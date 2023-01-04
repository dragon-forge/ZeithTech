package org.zeith.tech.api.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;

public record SoundConfiguration(SoundEvent sound, SoundSource source, float volume, float pitch, boolean play)
{
	public static final Codec<SoundConfiguration> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					SoundEvent.DIRECT_CODEC.fieldOf("sound").forGetter(SoundConfiguration::sound),
					Codec.STRING.xmap(SoundSource::valueOf, SoundSource::name).fieldOf("source").forGetter(SoundConfiguration::source),
					Codec.FLOAT.fieldOf("volume").forGetter(SoundConfiguration::volume),
					Codec.FLOAT.fieldOf("pitch").forGetter(SoundConfiguration::pitch),
					Codec.BOOL.fieldOf("play").forGetter(SoundConfiguration::play)
			).apply(instance, SoundConfiguration::new)
	);
	
	public SoundConfiguration(SoundEvent sound, SoundSource source, float volume, float pitch)
	{
		this(sound, source, volume, pitch, true);
	}
	
	public void play(Level level, BlockPos pos)
	{
		if(play())
			level.playSound(null, pos, sound(), source(), volume(), pitch());
	}
	
	public void play(Level level, Vec3 pos)
	{
		if(play())
			level.playSound(null, pos.x, pos.y, pos.z, sound(), source(), volume(), pitch());
	}
	
	public static SoundConfiguration place(SoundType sound)
	{
		return new SoundConfiguration(sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
	}
}