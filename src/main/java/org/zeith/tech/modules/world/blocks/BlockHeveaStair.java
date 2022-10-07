package org.zeith.tech.modules.world.blocks;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.core.ZeithTech;

import java.util.*;
import java.util.function.Supplier;

public class BlockHeveaStair
		extends StairBlock
		implements IRegisterListener, ICustomBlockItem
{
	private final List<TagKey<Item>> itemTags = new ArrayList<>();
	protected boolean dropsSelf = false;
	
	public BlockHeveaStair(Supplier<BlockState> state, Properties props)
	{
		super(state, props);
	}
	
	public BlockHeveaStair dropsSelf()
	{
		this.dropsSelf = true;
		return this;
	}
	
	public BlockHeveaStair addItemTags(Collection<TagKey<Item>> tags)
	{
		if(itemBlock != null)
			for(var tag : tags)
				TagAdapter.bind(tag, itemBlock);
		itemTags.addAll(tags);
		return this;
	}
	
	public BlockHeveaStair addItemTag(TagKey<Item> tag)
	{
		if(itemBlock != null)
			TagAdapter.bind(tag, itemBlock);
		itemTags.add(tag);
		return this;
	}
	
	public BlockHeveaStair addBlockTags(Collection<TagKey<Block>> tags)
	{
		for(var tag : tags)
			TagAdapter.bind(tag, this);
		return this;
	}
	
	public BlockHeveaStair addBlockTag(TagKey<Block> tag)
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