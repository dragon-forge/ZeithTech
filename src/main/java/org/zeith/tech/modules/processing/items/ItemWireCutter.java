package org.zeith.tech.modules.processing.items;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.init.TagsZT;

public class ItemWireCutter
		extends DiggerItem
{
	private final RandomSource random = RandomSource.create();
	
	public ItemWireCutter(Properties props, Tier tier)
	{
		super(0, -3.0F, tier, TagsZT.Blocks.MINEABLE_WITH_WIRE_CUTTER, props.defaultDurability(192));
		ZeithTech.TAB.add(this);
	}
	
	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemStack)
	{
		ItemStack damagedStack = itemStack.copy();
		return damagedStack.hurt(1, this.random, null) ? ItemStack.EMPTY : damagedStack;
	}
}