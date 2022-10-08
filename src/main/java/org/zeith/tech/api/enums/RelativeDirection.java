package org.zeith.tech.api.enums;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.HashMap;
import java.util.Map;

public enum RelativeDirection
		implements StringRepresentable
{
	FRONT("front"),
	BACK("back"),
	UP("up"),
	DOWN("down"),
	LEFT("left"),
	RIGHT("right");
	
	final String name;
	
	RelativeDirection(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getSerializedName()
	{
		return name;
	}
	
	public static RelativeDirection fromHorizontalFront(Direction horizontal, Direction direction)
	{
		if(horizontal == null) return null;
		
		if(horizontal == direction)
			return RelativeDirection.FRONT;
		
		if(horizontal == direction.getOpposite())
			return RelativeDirection.BACK;
		
		if(direction.getAxis() != Direction.Axis.Y)
		{
			if(horizontal == direction.getClockWise())
				return RelativeDirection.RIGHT;
			if(horizontal == direction.getCounterClockWise())
				return RelativeDirection.LEFT;
		}
		
		if(horizontal.getAxis() == Direction.Axis.Y)
		{
			var up = horizontal == Direction.UP ? Direction.NORTH : Direction.SOUTH;
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
	
	public static Direction getAbsolute(Direction horizontal, RelativeDirection relative)
	{
		return new FrontConfiguration(horizontal, relative).toAbsolute();
	}
	
	private record FrontConfiguration(Direction horizontal, RelativeDirection relative)
	{
		private static final Map<FrontConfiguration, Direction> TO_ABSOLUTE = new HashMap<>();
		
		public Direction toAbsolute()
		{
			return TO_ABSOLUTE.get(this);
		}
	}
	
	static
	{
		var directions = Direction.values();
		
		for(var horizontal : directions)
		{
			if(horizontal.getAxis() == Direction.Axis.Y) continue;
			
			for(var absolute : directions)
			{
				var relative = fromHorizontalFront(horizontal, absolute);
				FrontConfiguration.TO_ABSOLUTE.put(new FrontConfiguration(horizontal, relative), absolute);
			}
		}
	}
}