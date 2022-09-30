package org.zeith.tech.api.tile.sided;

import net.minecraft.core.Direction;
import net.minecraft.nbt.ShortTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.enums.RelativeDirection;
import org.zeith.tech.api.enums.SideConfig;

public class SpecificSidedConfigManager
		implements ITileSidedConfig.ISpecificSidedConfig, INBTSerializable<ShortTag>
{
	public final DirectStorage<Direction> currentDirection;
	
	public final SideConfig6 configurations;
	
	public SpecificSidedConfigManager(DirectStorage<Direction> currentDirection, SideConfig def)
	{
		this.currentDirection = currentDirection;
		configurations = new SideConfig6(def);
	}
	
	public SpecificSidedConfigManager(DirectStorage<Direction> currentDirection)
	{
		this(currentDirection, SideConfig.NONE);
	}
	
	@Override
	public ITileSidedConfig.ISpecificSidedConfig setDefaults(SideConfig config)
	{
		configurations.setDefaults(config);
		return this;
	}
	
	public RelativeDirection toRelative(Direction direction)
	{
		var cur = currentDirection.get();
		if(cur == null) return null;
		
		if(cur == direction)
			return RelativeDirection.FRONT;
		
		if(cur == direction.getOpposite())
			return RelativeDirection.BACK;
		
		if(direction.getAxis() != Direction.Axis.Y)
		{
			if(cur == direction.getClockWise())
				return RelativeDirection.RIGHT;
			if(cur == direction.getCounterClockWise())
				return RelativeDirection.LEFT;
		}
		
		if(cur.getAxis() == Direction.Axis.Y)
		{
			var up = cur == Direction.UP ? Direction.NORTH : Direction.SOUTH;
			if(up == direction) return RelativeDirection.UP;
			if(up.getOpposite() == direction) return RelativeDirection.DOWN;
			if(up.getClockWise() == direction) return RelativeDirection.RIGHT;
			if(up.getCounterClockWise() == direction) return RelativeDirection.LEFT;
		} else
		{
			if(direction == Direction.UP)
				return RelativeDirection.UP;
			if(direction == Direction.DOWN)
				return RelativeDirection.DOWN;
		}
		
		// This should not happen if all directions are populated properly.
		throw new NullPointerException();
	}
	
	@Override
	public SideConfig getRelative(RelativeDirection direction)
	{
		if(direction == null) return SideConfig.DISABLE;
		return configurations.get(direction.ordinal());
	}
	
	@Override
	public void setRelative(RelativeDirection direction, SideConfig config)
	{
		if(direction == null) return;
		configurations.set(direction.ordinal(), config);
	}
	
	@Override
	public SideConfig getAbsolute(Direction direction)
	{
		return getRelative(toRelative(direction));
	}
	
	@Override
	public void setAbsolute(Direction direction, SideConfig config)
	{
		setRelative(toRelative(direction), config);
	}
	
	// This magic below does a bit-wise shifts to fit 6*2 (we have 4 values for SideConfig) bits into a 16-bit short.
	// Goes both ways (read/write). Works like a charm!
	
	@Override
	public ShortTag serializeNBT()
	{
		return configurations.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(ShortTag nbt)
	{
		configurations.deserializeNBT(nbt);
	}
}