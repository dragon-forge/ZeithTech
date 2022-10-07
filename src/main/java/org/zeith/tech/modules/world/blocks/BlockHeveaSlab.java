package org.zeith.tech.modules.world.blocks;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.core.ZeithTech;

import java.util.*;

public class BlockHeveaSlab
		extends SlabBlock
		implements IRegisterListener, ICustomBlockItem
{
	private final List<TagKey<Item>> itemTags = new ArrayList<>();
	protected boolean dropsSelf = false;
	
	public BlockHeveaSlab(Properties props)
	{
		super(props);
	}
	
	public BlockHeveaSlab dropsSelf()
	{
		this.dropsSelf = true;
		return this;
	}
	
	public BlockHeveaSlab addItemTags(Collection<TagKey<Item>> tags)
	{
		if(itemBlock != null)
			for(var tag : tags)
				TagAdapter.bind(tag, itemBlock);
		itemTags.addAll(tags);
		return this;
	}
	
	public BlockHeveaSlab addItemTag(TagKey<Item> tag)
	{
		if(itemBlock != null)
			TagAdapter.bind(tag, itemBlock);
		itemTags.add(tag);
		return this;
	}
	
	public BlockHeveaSlab addBlockTags(Collection<TagKey<Block>> tags)
	{
		for(var tag : tags)
			TagAdapter.bind(tag, this);
		return this;
	}
	
	public BlockHeveaSlab addBlockTag(TagKey<Block> tag)
	{
		TagAdapter.bind(tag, this);
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
			TagAdapter.bind(tag, gen);
		return gen;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		if(dropsSelf)
			return List.of(new ItemStack(this));
		else
			return super.getDrops(state, builder);
	}
}