package org.zeith.tech.modules.transport.blocks.base.traversable;

import net.minecraft.core.Direction;

import java.util.*;

public class TraversableHelper
{
	private static ThreadLocal<Random> RNG = ThreadLocal.withInitial(Random::new);
	
	public static <T> Optional<TraversablePath<T>> findClosestPath(ITraversable<T> start, Direction from, T contents)
	{
		var rng = RNG.get();
		
		return findAllPaths(start, from, contents)
				.stream()
				.max(Comparator.<TraversablePath<T>>
								comparingInt(p -> p.endpoint.priority()) // First we compare by priority
						.thenComparingInt(p -> -p.size()) // Then, if the priority is the same, compare by the inverse length of the path (the shortest path will be preferred)
						.thenComparingInt(p -> // Then compare by hash, this makes a bit of random-ness if previous two fail.
						{
							rng.setSeed(p.seed);
							return rng.nextInt();
						})
				);
	}
	
	public static <T> List<TraversablePath<T>> findAllPaths(ITraversable<T> start, Direction from, T contents)
	{
		List<TraversablePath<T>> listOfPaths = new ArrayList<>();
		
		Stack<ITraversable<T>> currentBranch = new Stack<>();
		currentBranch.push(start);
		
		collectPaths(listOfPaths, currentBranch, from, contents);
		
		// De-duplicate paths by using EndpointDistinct, effectively fixing all loops and branches that lead to the same endpoint.
		return listOfPaths.stream()
				.map(EndpointDistinct::new)
				.distinct()
				.map(EndpointDistinct::path)
				.toList();
	}
	
	private static <T> void collectPaths(List<TraversablePath<T>> path, Stack<ITraversable<T>> branch, Direction from, T contents)
	{
		var currentPart = branch.peek();
		
		if(branch.size() > 1)
			currentPart.getEndpoints(contents)
					.forEach(endpoint -> path.add(TraversablePath.of(branch, endpoint)));
		else
			currentPart.getEndpoints(contents)
					.stream()
					.filter(endpoint -> endpoint.dir() != from) // for same-block endpoints, we should ignore the facing from where the item originates
					.forEach(endpoint -> path.add(TraversablePath.of(branch, endpoint)));
		
		currentPart.allNeighbors()
				.filter(elem -> !branch.contains(elem))
				.forEach(elem ->
				{
					branch.push(elem);
					try
					{
						collectPaths(path, branch, from, contents);
					} catch(StackOverflowError ignored)
					{
					}
					branch.pop();
				});
	}
}