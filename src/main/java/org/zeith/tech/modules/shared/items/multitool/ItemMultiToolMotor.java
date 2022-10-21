package org.zeith.tech.modules.shared.items.multitool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.zeith.tech.api.item.multitool.IMultiToolMotor;

import java.util.List;
import java.util.function.Consumer;

public class ItemMultiToolMotor
		extends TieredItem
		implements IMultiToolMotor
{
	public final ResourceLocation model;
	
	public ItemMultiToolMotor(Tier tier, Properties props, ResourceLocation model)
	{
		super(tier, props.durability(0));
		this.model = model;
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
