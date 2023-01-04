package org.zeith.tech.modules.shared.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.item.IAccumulatorItem;
import org.zeith.tech.api.item.ItemHandlerForgeEnergy;
import org.zeith.tech.api.item.multitool.TooltipFlagMultiTool;
import org.zeith.tech.api.item.tooltip.TooltipEnergyBar;
import org.zeith.tech.core.ZeithTech;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemAccumulator
		extends Item
		implements IAccumulatorItem
{
	public final int capacity;
	
	public ItemAccumulator(int capacity, Properties props)
	{
		super(props.stacksTo(1));
		this.capacity = capacity;
		ZeithTech.TAB.add(this);
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
	{
		return 0;
	}
	
	@Override
	public void setDamage(ItemStack stack, int damage)
	{
	}
	
	@Override
	public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
	{
		return new ItemHandlerForgeEnergy(stack, this);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(getEnergy(stack) * 13F / getMaxEnergy(stack));
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, getBarWidth(stack) / 13F);
		return Mth.hsvToRgb(173 / 360F, 1.0F, 0.666F + f * 0.333F);
	}
	
	private CompoundTag getEnergyTag(ItemStack stack)
	{
		return stack.getOrCreateTagElement("Energy");
	}
	
	@Override
	public int getEnergy(ItemStack stack)
	{
		var energyTag = getEnergyTag(stack);
		return energyTag.getInt("Stored");
	}
	
	@Override
	public int getMaxEnergy(ItemStack stack)
	{
		return capacity;
	}
	
	@Override
	public void setEnergy(ItemStack stack, int energy)
	{
		var energyTag = getEnergyTag(stack);
		energyTag.putInt("Stored", Mth.clamp(energy, 0, getMaxEnergy(stack)));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag)
	{
		var inMultiTool = flag instanceof TooltipFlagMultiTool;
		
		tooltip.add(Component.literal(I18n.get("info.zeithtech.fe.stored", getEnergy(stack))).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.literal(I18n.get("info.zeithtech.fe.capacity", getMaxEnergy(stack))).withStyle(ChatFormatting.GRAY));
		
		if(!inMultiTool) tooltip.add(Component.translatable("info.zeithtech.multi_tool.battery").withStyle(ChatFormatting.DARK_GRAY));
	}
	
	@Override
	public boolean isEnchantable(ItemStack p_41456_)
	{
		return false;
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		return Optional.of(new TooltipEnergyBar(getEnergy(stack), getMaxEnergy(stack)));
	}
}