package org.zeith.tech.api.block.multiblock.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MultiBlockRegistry
{
	private static final List<MultiBlockFormer> REGISTRY = new ArrayList<>();
	
	public static void register(MultiBlockFormer former)
	{
		if(!REGISTRY.contains(former))
			REGISTRY.add(former);
	}
	
	public static Stream<MultiBlockFormer> registered()
	{
		return REGISTRY.stream();
	}
}