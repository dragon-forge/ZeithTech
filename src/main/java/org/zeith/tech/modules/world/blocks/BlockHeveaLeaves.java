package org.zeith.tech.modules.world.blocks;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.core.ZeithTech;

import java.util.*;

public class BlockHeveaLeaves
		extends LeavesBlock
		implements IRegisterListener, ICustomBlockItem
{
	private final List<TagKey<Item>> itemTags = new ArrayList<>();
	
	public BlockHeveaLeaves(BlockBehaviour.Properties props)
	{
		super(props);
	}
	
	public BlockHeveaLeaves addItemTags(Collection<TagKey<Item>> tags)
	{
		for(var tag : tags)
		{
			if(itemBlock != null)
				TagAdapter.bindStaticTag(tag, itemBlock);
			itemTags.add(tag);
		}
		return this;
	}
	
	public BlockHeveaLeaves addBlockTags(Collection<TagKey<Block>> tags)
	{
		for(var tag : tags)
			TagAdapter.bindStaticTag(tag, this);
		return this;
	}
	
	private BlockItem itemBlock;
	
	@Override
	public BlockItem createBlockItem()
	{
		var props = new Item.Properties().tab(ZeithTech.TAB);
		var gen = new BlockItem(this, props);
		itemBlock = gen;
		for(var tag : itemTags)
			TagAdapter.bindStaticTag(tag, gen);
		return gen;
	}
}