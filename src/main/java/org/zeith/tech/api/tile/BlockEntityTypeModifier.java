package org.zeith.tech.api.tile;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.mixins.BlockEntityTypeAccessor;

import java.util.*;

public class BlockEntityTypeModifier
{
	public static void addBlocksToEntityType(BlockEntityType<?> type, Block... blocks)
	{
		BlockEntityTypeAccessor ac = Cast.cast(type, BlockEntityTypeAccessor.class);
		
		if(ac == null)
			throw new UnsupportedOperationException("The BlockEntityType mixin accessor failed to apply...");
		
		var valid = ac.getValidBlocks();
		if(!tryAdd(valid, blocks))
		{
			valid = new HashSet<>(valid);
			valid.addAll(List.of(blocks));
			ac.setValidBlocks(valid);
		}
	}
	
	private static <T> boolean tryAdd(Collection<T> coll, T... all)
	{
		try
		{
			return coll.addAll(List.of(all));
		} catch(UnsupportedOperationException e)
		{
			return false;
		}
	}
}