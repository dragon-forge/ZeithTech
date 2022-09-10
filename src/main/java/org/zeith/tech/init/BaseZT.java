package org.zeith.tech.init;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.ZeithTech;

public class BaseZT
{
	public static Item newItem()
	{
		return new Item(itemProps());
	}
	
	public static Item newItem(TagKey<Item> tag)
	{
		var item = newItem();
		TagAdapter.bindStaticTag(tag, item);
		return item;
	}
	
	public static Item.Properties itemProps()
	{
		return new Item.Properties().tab(ZeithTech.TAB);
	}
}