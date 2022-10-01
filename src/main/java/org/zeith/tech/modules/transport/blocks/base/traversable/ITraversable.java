package org.zeith.tech.modules.transport.blocks.base.traversable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public interface ITraversable<T>
{
	Optional<? extends ITraversable<T>> getRelativeTraversable(Direction side);
	
	default Stream<ITraversable<T>> allNeighbors()
	{
		return Arrays.stream(EndpointData.DIRECTIONS)
				.map(this::getRelativeTraversable)
				.flatMap(Optional::stream);
	}
	
	// Higher priority will make this traversable preferred.
	List<EndpointData> getEndpoints(T contents);
	
	BlockPos getPosition();
	
	@Nullable
	default Direction getTo(ITraversable<T> other)
	{
		return Direction.fromNormal(other.getPosition().subtract(getPosition()));
	}
	
	@Nullable
	default Direction getFrom(ITraversable<T> other)
	{
		return Direction.fromNormal(getPosition().subtract(other.getPosition()));
	}
}