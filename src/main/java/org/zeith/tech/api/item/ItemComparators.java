package org.zeith.tech.api.item;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
import org.zeith.tech.api.ZeithTechAPI;

import java.util.Comparator;

public class ItemComparators
{
	public static final Comparator<ResourceLocation> PREFER_ZEITHTECH = Comparator.comparingInt(loc -> loc.getNamespace().equals(ZeithTechAPI.MOD_ID) ? 1 : 0);
	
	public static final Comparator<Item> PREFER_ZEITHTECH_ITEMS = preferZeithTech(ForgeRegistries.Keys.ITEMS);
	public static final Comparator<Block> PREFER_ZEITHTECH_BLOCKS = preferZeithTech(ForgeRegistries.Keys.BLOCKS);
	
	public static final Comparator<Holder<Item>> PREFER_ZEITHTECH_ITEM_HOLDERS = preferZeithTechHolder(ForgeRegistries.Keys.ITEMS);
	public static final Comparator<Holder<Block>> PREFER_ZEITHTECH_BLOCK_HOLDERS = preferZeithTechHolder(ForgeRegistries.Keys.BLOCKS);
	
	public static <T> Comparator<T> preferZeithTech(ResourceKey<Registry<T>> key)
	{
		return Comparator.comparing(RegistryManager.ACTIVE.getRegistry(key)::getKey, PREFER_ZEITHTECH);
	}
	
	public static <T> Comparator<Holder<T>> preferZeithTechHolder(ResourceKey<Registry<T>> key)
	{
		return Comparator.comparing(Holder::value, preferZeithTech(key));
	}
}