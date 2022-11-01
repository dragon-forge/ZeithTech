package org.zeith.tech.api.misc.farm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

public record FarmItemConsumer(EnumFarmItemCategory category, Ingredient item, int amount)
{
	public static final Codec<FarmItemConsumer> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.STRING.xmap(EnumFarmItemCategory::valueOf, EnumFarmItemCategory::name).fieldOf("category").forGetter(FarmItemConsumer::category),
					ItemStack.CODEC.listOf().xmap(lst -> Ingredient.of(lst.stream()), i -> Arrays.asList(i.getItems())).fieldOf("item").forGetter(FarmItemConsumer::item),
					Codec.INT.fieldOf("amount").forGetter(FarmItemConsumer::amount)
			).apply(instance, FarmItemConsumer::new)
	);
	
	public ItemStack getConsumedItem(IFarmController farm)
	{
		if(amount == 0 || item.isEmpty()) return ItemStack.EMPTY;
		
		var inv = farm.getInventory(category);
		
		ItemStack extracted = ItemStack.EMPTY;
		
		if(inv != null)
		{
			int need = amount;
			
			for(int i = 0; i < inv.getSlots() && need > 0; ++i)
			{
				var stack = inv.extractItem(i, need, true);
				if(item.test(stack))
				{
					need -= stack.getCount();
					if(extracted.isEmpty()) extracted = stack;
					else extracted.grow(stack.getCount());
				}
			}
			
			return extracted;
		}
		
		return ItemStack.EMPTY;
	}
	
	public boolean consumeItem(IFarmController farm, boolean simulate)
	{
		if(amount == 0 || item.isEmpty()) return true;
		
		var inv = farm.getInventory(category);
		
		if(inv != null)
		{
			int need = amount;
			
			for(int i = 0; i < inv.getSlots() && need > 0; ++i)
			{
				var stack = inv.extractItem(i, need, true);
				if(item.test(stack))
				{
					need -= stack.getCount();
					if(!simulate)
						inv.extractItem(i, stack.getCount(), false);
				}
			}
			
			return need == 0;
		}
		
		return false;
	}
}