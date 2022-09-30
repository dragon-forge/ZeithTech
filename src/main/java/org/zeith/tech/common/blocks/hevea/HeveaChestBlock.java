package org.zeith.tech.common.blocks.hevea;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.client.renderer.item.HeveaChestISTER;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HeveaChestBlock
		extends ChestBlock
		implements IRegisterListener, ICustomBlockItem
{
	private final List<TagKey<Item>> itemTags = new ArrayList<>();
	protected boolean dropsSelf = false;
	
	public HeveaChestBlock(BlockBehaviour.Properties props, Supplier<BlockEntityType<? extends ChestBlockEntity>> type)
	{
		super(props, type);
	}
	
	public HeveaChestBlock dropsSelf()
	{
		this.dropsSelf = true;
		return this;
	}
	
	public HeveaChestBlock addItemTags(Collection<TagKey<Item>> tags)
	{
		if(itemBlock != null)
			for(var tag : tags)
				TagAdapter.bindStaticTag(tag, itemBlock);
		itemTags.addAll(tags);
		return this;
	}
	
	public HeveaChestBlock addItemTag(TagKey<Item> tag)
	{
		if(itemBlock != null)
			TagAdapter.bindStaticTag(tag, itemBlock);
		itemTags.add(tag);
		return this;
	}
	
	public HeveaChestBlock addBlockTags(Collection<TagKey<Block>> tags)
	{
		for(var tag : tags)
			TagAdapter.bindStaticTag(tag, this);
		return this;
	}
	
	public HeveaChestBlock addBlockTag(TagKey<Block> tag)
	{
		TagAdapter.bindStaticTag(tag, this);
		return this;
	}
	
	private BlockItem itemBlock;
	
	@Override
	public BlockItem createBlockItem()
	{
		var props = new Item.Properties().tab(ZeithTech.TAB);
		var gen = new BlockItem(this, props)
		{
			@Override
			public void initializeClient(Consumer<IClientItemExtensions> consumer)
			{
				consumer.accept(new IClientItemExtensions()
				{
					@Override
					public BlockEntityWithoutLevelRenderer getCustomRenderer()
					{
						return HeveaChestISTER.HEVEA_CHEST;
					}
				});
			}
		};
		itemBlock = gen;
		for(var tag : itemTags)
			TagAdapter.bindStaticTag(tag, gen);
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