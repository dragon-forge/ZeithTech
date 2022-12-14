package org.zeith.tech.modules.shared.blocks;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.api.item.IBurnableItem;
import org.zeith.tech.core.ZeithTech;

import java.util.*;

public class SimpleBlockZT
		extends Block
		implements IRegisterListener, ICustomBlockItem
{
	private final List<TagKey<Item>> itemTags = new ArrayList<>();
	protected boolean dropsSelf = false;
	protected IBurnableItem burnable;
	
	public SimpleBlockZT(Properties props, BlockHarvestAdapter.MineableType toolType, Tier miningTier)
	{
		super(props);
		BlockHarvestAdapter.bindTool(toolType, miningTier, this);
	}
	
	public SimpleBlockZT(Properties props, BlockHarvestAdapter.MineableType toolType)
	{
		super(props);
		addBlockTag(toolType.blockTag());
	}
	
	public SimpleBlockZT(Properties props)
	{
		super(props);
	}
	
	public SimpleBlockZT burnable(IBurnableItem burnable)
	{
		this.burnable = burnable;
		return this;
	}
	
	public SimpleBlockZT dropsSelf()
	{
		this.dropsSelf = true;
		return this;
	}
	
	public SimpleBlockZT addItemTags(Collection<TagKey<Item>> tags)
	{
		if(itemBlock != null)
			for(var tag : tags)
				TagAdapter.bind(tag, itemBlock);
		itemTags.addAll(tags);
		return this;
	}
	
	public SimpleBlockZT addItemTag(TagKey<Item> tag)
	{
		if(itemBlock != null)
			TagAdapter.bind(tag, itemBlock);
		itemTags.add(tag);
		return this;
	}
	
	public SimpleBlockZT addBlockTags(Collection<TagKey<Block>> tags)
	{
		for(var tag : tags)
			TagAdapter.bind(tag, this);
		return this;
	}
	
	public SimpleBlockZT addBlockTag(TagKey<Block> tag)
	{
		TagAdapter.bind(tag, this);
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
			public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType)
			{
				if(burnable != null) return burnable.getBurnTime(itemStack, recipeType);
				return super.getBurnTime(itemStack, recipeType);
			}
		};
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