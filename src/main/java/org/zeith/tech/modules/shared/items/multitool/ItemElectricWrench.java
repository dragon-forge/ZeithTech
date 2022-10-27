package org.zeith.tech.modules.shared.items.multitool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.core.init.TagsHL;
import org.zeith.tech.api.item.multitool.IMultiToolHead;

import java.util.List;

public class ItemElectricWrench
		extends Item
		implements IMultiToolHead
{
	public final ResourceLocation model;
	
	public ItemElectricWrench(Properties props, ResourceLocation model)
	{
		super(props);
		this.model = model;
	}
	
	@Override
	public List<TagKey<Item>> getHeadItemTags(ItemStack headStack, ItemStack multiToolStack)
	{
		return List.of(TagsHL.Items.TOOLS_WRENCH);
	}
	
	@Override
	public boolean isCorrectHeadForDrops(ItemStack headStack, ItemStack multiToolStack, BlockState state)
	{
		return false;
	}
	
	@Override
	public float getHeadMiningSpeed(ItemStack headStack, ItemStack multiToolStack, BlockState state)
	{
		return 0;
	}
	
	@Override
	public ResourceLocation getMultiToolPartModel(ItemStack partStack, ItemStack multiToolStack)
	{
		return model;
	}
	
	@Override
	public List<ResourceLocation> getAllPossibleMultiToolPartModels()
	{
		return List.of(model);
	}
}
