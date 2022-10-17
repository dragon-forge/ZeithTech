package org.zeith.tech.modules.processing.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import org.zeith.tech.modules.shared.init.TagsZT;

public class ItemMiningHead
		extends DiggerItem
{
	public ItemMiningHead(Tier tier, Properties props)
	{
		super(0, -2, tier, TagsZT.Blocks.MINEABLE_WITH_MINING_HEAD, props);
	}
	
	public void onPreMine(Level lvl, BlockPos pos, BlockState state, ItemStack headStack)
	{
	}
	
	public void onPostMine(Level lvl, BlockPos pos, BlockState state, ItemStack headStack)
	{
	}
	
	public boolean canMine(Level lvl, BlockPos pos, BlockState state, ItemStack headStack)
	{
		return TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
	}
}