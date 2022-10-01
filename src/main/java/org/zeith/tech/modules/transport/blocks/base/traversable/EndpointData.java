package org.zeith.tech.modules.transport.blocks.base.traversable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public record EndpointData(Direction dir, int priority, boolean valid)
		implements INBTSerializable<CompoundTag>
{
	public EndpointData(CompoundTag tag)
	{
		this(DIRECTIONS[tag.getByte("Dir")], tag.getInt("Priority"), tag.getBoolean("Valid"));
	}
	
	static final Direction[] DIRECTIONS = Direction.values();
	
	@Override
	public CompoundTag serializeNBT()
	{
		var nbt = new CompoundTag();
		
		nbt.putByte("Dir", (byte) dir.ordinal());
		nbt.putInt("Priority", priority);
		nbt.putBoolean("Valid", valid);
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
	}
}