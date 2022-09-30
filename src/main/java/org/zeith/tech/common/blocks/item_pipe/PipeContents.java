package org.zeith.tech.common.blocks.item_pipe;

import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class PipeContents
		implements INBTSerializable<ListTag>
{
	private final List<ItemInPipe> all = new ArrayList<>();
	private final ArrayDeque<ItemInPipe> insertQueue = new ArrayDeque<>();
	
	private int prevSize;
	
	public void update(TileItemPipe tile)
	{
		if(all.size() != prevSize)
		{
			if(tile.isOnServer()) tile.sync();
			prevSize = all.size();
		}
		
		while(!insertQueue.isEmpty())
			all.add(insertQueue.remove());
		
		all.removeIf(item -> item.getContents().isEmpty() || item.update(tile));
	}
	
	public Iterable<ItemInPipe> getAll()
	{
		return all;
	}
	
	public void add(ItemInPipe item)
	{
		insertQueue.add(item);
	}
	
	@Override
	public ListTag serializeNBT()
	{
		var nbt = new ListTag();
		all.stream().map(ItemInPipe::serializeNBT).forEach(nbt::add);
		return nbt;
	}
	
	@Override
	public void deserializeNBT(ListTag nbt)
	{
		all.clear();
		var count = nbt.size();
		for(int i = 0; i < count; ++i)
			all.add(new ItemInPipe(nbt.getCompound(i)));
	}
}