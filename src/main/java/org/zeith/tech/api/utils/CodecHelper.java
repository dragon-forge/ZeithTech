package org.zeith.tech.api.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.*;

import java.util.List;

public class CodecHelper
{
	public static <T> Tag encodeCompound(Codec<T> codec, T thing)
	{
		return NbtOps.INSTANCE.withEncoder(codec).apply(thing).result().orElse(new CompoundTag());
	}
	
	public static <T> T decodeCompound(Codec<T> codec, Tag tag)
	{
		return NbtOps.INSTANCE.withDecoder(codec).apply(tag).result().map(Pair::getFirst).orElse(null);
	}
	
	public static <T> Tag encodeList(Codec<T> codec, List<T> things)
	{
		return NbtOps.INSTANCE.withEncoder(codec.listOf()).apply(things).result().orElse(new ListTag());
	}
	
	public static <T> List<T> decodeList(Codec<T> codec, Tag tag)
	{
		return NbtOps.INSTANCE.withDecoder(codec.listOf()).apply(tag).result().map(Pair::getFirst).orElse(List.of());
	}
}