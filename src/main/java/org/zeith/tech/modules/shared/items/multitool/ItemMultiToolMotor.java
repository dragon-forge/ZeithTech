package org.zeith.tech.modules.shared.items.multitool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.item.multitool.IMultiToolMotor;
import org.zeith.tech.api.item.multitool.TooltipFlagMultiTool;

import java.util.List;
import java.util.function.Consumer;

public class ItemMultiToolMotor
		extends TieredItem
		implements IMultiToolMotor
{
	public final ResourceLocation model;
	public final float energyMul, speedMul;
	
	public ItemMultiToolMotor(Tier tier, float energyMul, float speedMul, Properties props, ResourceLocation model)
	{
		super(tier, props.durability(0));
		this.energyMul = energyMul;
		this.speedMul = speedMul;
		this.model = model;
	}
	
	@Override
	public float getMotorEnergyMultiplier(ItemStack motorStack, ItemStack multiToolStack)
	{
		return energyMul;
	}
	
	@Override
	public float getMotorSpeedMultiplier(ItemStack motorStack, ItemStack multiToolStack)
	{
		return speedMul;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
	{
		var inMultiTool = flag instanceof TooltipFlagMultiTool;
		tooltip.add(Component.translatable("info" + getDescriptionId().substring(4)).withStyle(ChatFormatting.GRAY));
		
		tooltip.add(Component.translatable("info.zeithtech.energy_mult", Component.literal("%.0f%%".formatted(energyMul * 100F))).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("info.zeithtech.speed_mult", Component.literal("%.0f%%".formatted(speedMul * 100F))).withStyle(ChatFormatting.GRAY));
		
		if(!inMultiTool) tooltip.add(Component.translatable("info.zeithtech.multi_tool.motor").withStyle(ChatFormatting.DARK_GRAY));
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
	{
		return 0;
	}
	
	@Override
	public boolean canBeDepleted()
	{
		return false;
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
