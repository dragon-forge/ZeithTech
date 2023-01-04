package org.zeith.tech.modules.shared.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.core.ZeithTech;

import java.util.*;

public abstract class BaseEntityBlockZT
		extends BaseEntityBlock
		implements IRegisterListener, ICustomBlockItem
{
	private final List<TagKey<Item>> itemTags = new ArrayList<>();
	protected boolean dropsSelf = false;
	
	public BaseEntityBlockZT(BlockBehaviour.Properties props, BlockHarvestAdapter.MineableType toolType, Tier miningTier)
	{
		super(props);
		BlockHarvestAdapter.bindTool(toolType, miningTier, this);
	}
	
	public BaseEntityBlockZT(BlockBehaviour.Properties props, BlockHarvestAdapter.MineableType toolType)
	{
		super(props);
		addBlockTag(toolType.blockTag());
	}
	
	public BaseEntityBlockZT(BlockBehaviour.Properties props)
	{
		super(props);
	}
	
	@Override
	public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
	
	public BaseEntityBlockZT dropsSelf()
	{
		this.dropsSelf = true;
		return this;
	}
	
	public BaseEntityBlockZT addItemTags(Collection<TagKey<Item>> tags)
	{
		if(itemBlock != null)
			for(var tag : tags)
				TagAdapter.bind(tag, itemBlock);
		itemTags.addAll(tags);
		return this;
	}
	
	public BaseEntityBlockZT addItemTag(TagKey<Item> tag)
	{
		if(itemBlock != null)
			TagAdapter.bind(tag, itemBlock);
		itemTags.add(tag);
		return this;
	}
	
	public BaseEntityBlockZT addBlockTags(Collection<TagKey<Block>> tags)
	{
		for(var tag : tags)
			TagAdapter.bind(tag, this);
		return this;
	}
	
	public BaseEntityBlockZT addBlockTag(TagKey<Block> tag)
	{
		TagAdapter.bind(tag, this);
		return this;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	private BlockItem itemBlock;
	
	protected BlockItem newBlockItem(Item.Properties props)
	{
		return new BlockItem(this, props);
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		var props = new Item.Properties();
		var gen = newBlockItem(props);
		itemBlock = gen;
		for(var tag : itemTags)
			TagAdapter.bind(tag, gen);
		return ZeithTech.TAB.add(gen);
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