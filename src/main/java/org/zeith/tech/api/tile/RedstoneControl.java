package org.zeith.tech.api.tile;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.tech.api.ZeithTechAPI;

import java.util.Locale;
import java.util.function.BiPredicate;

@AutoRegisterCapability
public class RedstoneControl
		implements INBTSerializable<CompoundTag>
{
	protected RedstoneMode mode = RedstoneMode.ALWAYS_ACTIVE;
	protected byte threshold = 1;
	
	public void setThreshold(byte threshold)
	{
		Preconditions.checkArgument(threshold >= 1 && threshold <= 15, "Threshold must be in range of [1; 15].");
		this.threshold = threshold;
	}
	
	public void setMode(RedstoneMode mode)
	{
		this.mode = mode;
	}
	
	public RedstoneMode getMode()
	{
		return mode;
	}
	
	public byte getThreshold()
	{
		return threshold;
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		var tag = new CompoundTag();
		tag.putString("Mode", mode.name());
		tag.putByte("Threshold", threshold);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		this.mode = RedstoneMode.valueOf(nbt.getString("Mode"));
		this.threshold = nbt.getByte("Threshold");
	}
	
	public boolean shouldWork(BlockEntity tile)
	{
		return shouldWork(tile.getLevel(), tile.getBlockPos());
	}
	
	public boolean shouldWork(Level level, BlockPos pos)
	{
		return shouldWork(level.getBestNeighborSignal(pos));
	}
	
	public boolean shouldWork(int redstone)
	{
		return mode.shouldWork(redstone, threshold);
	}
	
	public enum RedstoneMode
			implements StringRepresentable
	{
		NEVER_ACTIVE("info." + ZeithTechAPI.MOD_ID + ".redstone_control.never", (i, t) -> false),
		ACTIVE_WHEN_POWERED("info." + ZeithTechAPI.MOD_ID + ".redstone_control.normal", (i, t) -> i >= t),
		ACTIVE_WHEN_UNPOWERED("info." + ZeithTechAPI.MOD_ID + ".redstone_control.inversed", (i, t) -> i < t),
		ALWAYS_ACTIVE("info." + ZeithTechAPI.MOD_ID + ".redstone_control.ignore", (i, t) -> true);
		
		final String lang;
		final BiPredicate<Integer, Integer> fromRedstone;
		
		RedstoneMode(String lang, BiPredicate<Integer, Integer> fromRedstone)
		{
			this.lang = lang;
			this.fromRedstone = fromRedstone;
		}
		
		public boolean shouldWork(int redstone, int threshold)
		{
			return fromRedstone.test(redstone, threshold);
		}
		
		@Override
		public String getSerializedName()
		{
			return name().toLowerCase(Locale.ROOT);
		}
		
		public MutableComponent translate(int threshold)
		{
			return Component.translatable(lang, Component.literal(Integer.toUnsignedString(threshold)));
		}
	}
}