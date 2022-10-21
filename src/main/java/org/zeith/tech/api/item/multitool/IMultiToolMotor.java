package org.zeith.tech.api.item.multitool;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.common.TierSortingRegistry;

public interface IMultiToolMotor
		extends IMultiToolPart
{
	default boolean supportsHead(ItemStack motorStack, ItemStack headStack, ItemStack multiToolStack)
	{
		if(this instanceof TieredItem tieredMotor && headStack.getItem() instanceof TieredItem tieredHead)
		{
			return tieredMotor.getTier() == tieredHead.getTier()
					|| TierSortingRegistry.getTiersLowerThan(tieredMotor.getTier()).contains(tieredHead.getTier());
		}
		
		return true;
	}
	
	default float getMotorEnergyMultiplier(ItemStack motorStack, ItemStack multiToolStack)
	{
		return 1F;
	}
	
	default float getMotorSpeedMultiplier(ItemStack motorStack, ItemStack multiToolStack)
	{
		return 1F;
	}
}