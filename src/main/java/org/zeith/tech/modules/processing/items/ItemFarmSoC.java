package org.zeith.tech.modules.processing.items;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.misc.farm.FarmAlgorithm;
import org.zeith.tech.modules.shared.BaseZT;

import java.util.List;
import java.util.Optional;

public class ItemFarmSoC
		extends Item
{
	public ItemFarmSoC()
	{
		super(BaseZT.itemProps().stacksTo(1));
	}
	
	public FarmAlgorithm getAlgorithm(ItemStack stack)
	{
		var tag = stack.getTag();
		if(tag != null && tag.contains("Algorithm"))
			return ZeithTechAPI.get().getFarmAlgorithms()
					.getValue(new ResourceLocation(tag.getString("Algorithm")));
		return null;
	}
	
	public ItemStack ofAlgorithm(FarmAlgorithm algorithm)
	{
		var stack = new ItemStack(this);
		if(algorithm != null)
			stack.addTagElement("Algorithm", StringTag.valueOf(algorithm.getRegistryName().toString()));
		return stack;
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items)
	{
		super.fillItemCategory(tab, items);
		if(allowedIn(tab))
			for(FarmAlgorithm algorithm : ZeithTechAPI.get().getFarmAlgorithms())
				items.add(ofAlgorithm(algorithm));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
	{
		var algo = getAlgorithm(stack);
		if(algo != null)
			tooltip.add(algo.getDisplayName().withStyle(s -> s.withColor(TextColor.fromRgb(algo.getColor()))));
	}
	
	@Override
	public int getBarColor(@NotNull ItemStack stack)
	{
		return Optional.ofNullable(getAlgorithm(stack)).map(FarmAlgorithm::getColor).orElse(0xFFFFFF);
	}
	
	@Override
	public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack)
	{
		return Optional.empty();
	}
}