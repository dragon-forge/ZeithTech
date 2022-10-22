package org.zeith.tech.modules.shared.items.multitool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.item.multitool.IMultiToolHead;
import org.zeith.tech.api.item.multitool.TooltipFlagMultiTool;
import org.zeith.tech.modules.shared.init.TagsZT;

import java.util.List;

public class ItemElectricSaw
		extends DiggerItem
		implements IMultiToolHead
{
	final ResourceLocation multiToolModel;
	
	public ItemElectricSaw(Tier tier, ResourceLocation model, Properties props)
	{
		super(0, -2, tier, TagsZT.Blocks.MINEABLE_WITH_ELECTRIC_SAW, props);
		this.multiToolModel = model;
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot)
	{
		return ImmutableMultimap.of();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag)
	{
		var inMultiTool = flag instanceof TooltipFlagMultiTool;
		if(!inMultiTool) components.add(Component.translatable("info.zeithtech.multi_tool.head").withStyle(ChatFormatting.DARK_GRAY));
		super.appendHoverText(stack, level, components, flag);
	}
	
	@Override
	public boolean isCorrectHeadForDrops(ItemStack headStack, ItemStack multiToolStack, BlockState state)
	{
		return (state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_HOE))
				&& net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
	}
	
	@Override
	public float getHeadMiningSpeed(ItemStack headStack, ItemStack multiToolStack, BlockState state)
	{
		return (state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_HOE)) ? getTier().getSpeed() : 1F;
	}
	
	@Override
	public ResourceLocation getMultiToolPartModel(ItemStack partStack, ItemStack multiToolStack)
	{
		return multiToolModel;
	}
	
	@Override
	public List<ResourceLocation> getAllPossibleMultiToolPartModels()
	{
		return List.of(multiToolModel);
	}
}
