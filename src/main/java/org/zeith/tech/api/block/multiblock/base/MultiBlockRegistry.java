package org.zeith.tech.api.block.multiblock.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MultiBlockRegistry
{
	private static final List<MultiBlockFormer> REGISTRY = new ArrayList<>();
	
	public static <T extends MultiBlockFormer> T register(T former)
	{
		if(!REGISTRY.contains(former))
			REGISTRY.add(former);
		return former;
	}
	
	public static Stream<MultiBlockFormer> registered()
	{
		return REGISTRY.stream();
	}
}