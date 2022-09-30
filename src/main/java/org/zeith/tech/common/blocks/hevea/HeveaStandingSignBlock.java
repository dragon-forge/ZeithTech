package org.zeith.tech.common.blocks.hevea;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.LootContext;
import org.zeith.hammerlib.api.blocks.INoItemBlock;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.init.ItemsZT;

import java.util.Collection;
import java.util.List;

public class HeveaStandingSignBlock
		extends StandingSignBlock
		implements IRegisterListener, INoItemBlock
{
	public HeveaStandingSignBlock(Properties props, WoodType woodType)
	{
		super(props, woodType);
	}
	
	public HeveaStandingSignBlock addBlockTags(Collection<TagKey<Block>> tags)
	{
		for(var tag : tags)
			TagAdapter.bindStaticTag(tag, this);
		return this;
	}
	
	public HeveaStandingSignBlock addBlockTag(TagKey<Block> tag)
	{
		TagAdapter.bindStaticTag(tag, this);
		return this;
	}
	
	@Override
	public Item asItem()
	{
		return ItemsZT.HEVEA_SIGN;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		return List.of(new ItemStack(ItemsZT.HEVEA_SIGN));
	}
}