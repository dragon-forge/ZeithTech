package org.zeith.tech.modules.transport.blocks.item_pipe;

import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class ItemPipeContents
		implements INBTSerializable<ListTag>
{
	private final List<ItemInPipe> all = new ArrayList<>();
	private final Set<UUID> allIds = new HashSet<>(), tmpIds = new HashSet<>();
	private final ArrayDeque<ItemInPipe> insertQueue = new ArrayDeque<>();
	
	public void update(TileItemPipe tile)
	{
		allIds.clear();
		
		synchronized(all)
		{
			while(!insertQueue.isEmpty())
				all.add(insertQueue.remove());
			
			all.removeIf(item ->
			{
				if(!allIds.add(item.itemId))
					return true;
				
				return item.getContents().isEmpty() || item.update(tile);
			});
		}
		
		if(!allIds.equals(tmpIds))
		{
			if(tile.isOnServer()) tile.sync();
			
			tmpIds.clear();
			tmpIds.addAll(allIds);
		}
	}
	
	public Iterable<ItemInPipe> getAll()
	{
		return all;
	}
	
	public Optional<ItemInPipe> byId(UUID id)
	{
		return all.stream()
				.filter(i -> i.itemId.equals(id))
				.findFirst();
	}
	
	public void add(ItemInPipe item)
	{
		insertQueue.add(item);
	}
	
	public void addNow(ItemInPipe item)
	{
		synchronized(all)
		{
			all.add(item);
		}
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
		for(int i = 0; i < count; ++i) all.add(new ItemInPipe(nbt.getCompound(i)));
	}
	
	public void removeById(UUID itemId)
	{
		synchronized(all)
		{
			all.removeIf(i -> itemId.equals(i.itemId));
		}
	}
}