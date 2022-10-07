package org.zeith.tech.modules.world.blocks;

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
import org.zeith.tech.modules.world.init.ItemsZT_World;

import java.util.Collection;
import java.util.List;

public class BlockHeveaStandingSign
		extends StandingSignBlock
		implements IRegisterListener, INoItemBlock
{
	public BlockHeveaStandingSign(Properties props, WoodType woodType)
	{
		super(props, woodType);
	}
	
	public BlockHeveaStandingSign addBlockTags(Collection<TagKey<Block>> tags)
	{
		for(var tag : tags)
			TagAdapter.bind(tag, this);
		return this;
	}
	
	public BlockHeveaStandingSign addBlockTag(TagKey<Block> tag)
	{
		TagAdapter.bind(tag, this);
		return this;
	}
	
	@Override
	public Item asItem()
	{
		return ItemsZT_World.HEVEA_SIGN;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		return List.of(new ItemStack(ItemsZT_World.HEVEA_SIGN));
	}
}