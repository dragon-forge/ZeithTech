package org.zeith.tech.modules.shared.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.zeith.tech.api.ZeithTechAPI;

public class TagsZT
{
	public static void init()
	{
		TagsZT.Items.init();
		TagsZT.Blocks.init();
		TagsZT.Fluids.init();
	}
	
	public static class Items
	{
		private static void init()
		{
		}
		
		public static final TagKey<Item> MINING_PIPE = modTag("mining_pipe");
		
		public static final TagKey<Item> HEVEA_LOGS = modTag("hevea_logs");
		public static final TagKey<Item> HEVEA_PLANKS = modTag("hevea_planks");
		
		public static final TagKey<Item> INGOTS_TIN = tag("ingots/tin");
		public static final TagKey<Item> INGOTS_LEAD = tag("ingots/lead");
		public static final TagKey<Item> INGOTS_ALUMINUM = tag("ingots/aluminum");
		public static final TagKey<Item> INGOTS_SILVER = tag("ingots/silver");
		public static final TagKey<Item> INGOTS_ZINC = tag("ingots/zinc");
		public static final TagKey<Item> INGOTS_TUNGSTEN = tag("ingots/tungsten");
		
		public static final TagKey<Item> PLATES_IRON = tag("plates/iron");
		public static final TagKey<Item> PLATES_GOLD = tag("plates/gold");
		public static final TagKey<Item> PLATES_COPPER = tag("plates/copper");
		public static final TagKey<Item> PLATES_TIN = tag("plates/tin");
		public static final TagKey<Item> PLATES_LEAD = tag("plates/lead");
		public static final TagKey<Item> PLATES_ALUMINUM = tag("plates/aluminum");
		public static final TagKey<Item> PLATES_SILVER = tag("plates/silver");
		public static final TagKey<Item> PLATES_ZINC = tag("plates/zinc");
		public static final TagKey<Item> PLATES_TUNGSTEN = tag("plates/tungsten");
		
		public static final TagKey<Item> GEARS_WOODEN = tag("gears/wooden");
		public static final TagKey<Item> GEARS_STONE = tag("gears/stone");
		public static final TagKey<Item> GEARS_COPPER = tag("gears/copper");
		public static final TagKey<Item> GEARS_IRON = tag("gears/iron");
		public static final TagKey<Item> GEARS_TIN = tag("gears/tin");
		public static final TagKey<Item> GEARS_SILVER = tag("gears/silver");
		public static final TagKey<Item> GEARS_GOLD = tag("gears/gold");
		public static final TagKey<Item> GEARS_DIAMOND = tag("gears/diamond");
		public static final TagKey<Item> GEARS_TUNGSTEN = tag("gears/tungsten");
		public static final TagKey<Item> GEARS_NETHERITE = tag("gears/netherite");
		
		public static final TagKey<Item> SILICON = tag("silicon");
		public static final TagKey<Item> PLASTIC = tag("plastic");
		public static final TagKey<Item> RUBBER_OR_LATEX = modTag("rubber_or_latex");
		public static final TagKey<Item> RUBBER = tag("rubber");
		public static final TagKey<Item> LATEX = tag("latex");
		
		public static final TagKey<Item> COILS_COPPER = tag("coils/copper");
		public static final TagKey<Item> COILS_GOLD = tag("coils/gold");
		
		public static final TagKey<Item> RAW_MATERIALS_TIN = tag("raw_materials/tin");
		public static final TagKey<Item> RAW_MATERIALS_LEAD = tag("raw_materials/lead");
		public static final TagKey<Item> RAW_MATERIALS_ALUMINUM = tag("raw_materials/aluminum");
		public static final TagKey<Item> RAW_MATERIALS_SILVER = tag("raw_materials/silver");
		public static final TagKey<Item> RAW_MATERIALS_ZINC = tag("raw_materials/zinc");
		public static final TagKey<Item> RAW_MATERIALS_TUNGSTEN = tag("raw_materials/tungsten");
		
		public static final TagKey<Item> DUSTS_COAL = tag("dusts/coal");
		public static final TagKey<Item> DUSTS_IRON = tag("dusts/iron");
		
		public static final TagKey<Item> ORES_TIN = tag("ores/tin");
		public static final TagKey<Item> ORES_LEAD = tag("ores/lead");
		public static final TagKey<Item> ORES_ALUMINUM = tag("ores/aluminum");
		public static final TagKey<Item> ORES_SILVER = tag("ores/silver");
		public static final TagKey<Item> ORES_ZINC = tag("ores/zinc");
		public static final TagKey<Item> ORES_TUNGSTEN = tag("ores/tungsten");
		public static final TagKey<Item> ORES_LITHIUM = tag("ores/lithium");
		public static final TagKey<Item> ORES_URANIUM = tag("ores/uranium");
		
		public static final TagKey<Item> STORAGE_BLOCKS_TIN = tag("storage_blocks/tin");
		public static final TagKey<Item> STORAGE_BLOCKS_LEAD = tag("storage_blocks/lead");
		public static final TagKey<Item> STORAGE_BLOCKS_ALUMINUM = tag("storage_blocks/aluminum");
		public static final TagKey<Item> STORAGE_BLOCKS_ZINC = tag("storage_blocks/zinc");
		public static final TagKey<Item> STORAGE_BLOCKS_TUNGSTEN = tag("storage_blocks/tungsten");
		public static final TagKey<Item> STORAGE_BLOCKS_SILVER = tag("storage_blocks/silver");
		
		public static final TagKey<Item> STORAGE_BLOCKS_RAW_TIN = tag("storage_blocks/raw_tin");
		public static final TagKey<Item> STORAGE_BLOCKS_RAW_LEAD = tag("storage_blocks/raw_lead");
		public static final TagKey<Item> STORAGE_BLOCKS_RAW_ALUMINUM = tag("storage_blocks/raw_aluminum");
		public static final TagKey<Item> STORAGE_BLOCKS_RAW_SILVER = tag("storage_blocks/raw_silver");
		public static final TagKey<Item> STORAGE_BLOCKS_RAW_ZINC = tag("storage_blocks/raw_zinc");
		public static final TagKey<Item> STORAGE_BLOCKS_RAW_TUNGSTEN = tag("storage_blocks/raw_tungsten");
		
		private static TagKey<Item> tag(String name)
		{
			return ItemTags.create(new ResourceLocation("forge", name));
		}
		
		private static TagKey<Item> modTag(String name)
		{
			return ItemTags.create(ZeithTechAPI.id(name));
		}
		
		private static TagKey<Item> vanillaTag(String name)
		{
			return ItemTags.create(new ResourceLocation("minecraft", name));
		}
	}
	
	public static class Blocks
	{
		private static void init()
		{
		}
		
		public static final TagKey<Block> MINING_PIPE = modTag("mining_pipe");
		public static final TagKey<Block> FACADE_WHITELIST = modTag("facade_whitelist");
		public static final TagKey<Block> MINEABLE_WITH_WIRE_CUTTER = vanillaTag("mineable/wire_cutter");
		public static final TagKey<Block> MINEABLE_WITH_MINING_HEAD = modTag("mineable/mining_head");
		public static final TagKey<Block> MINEABLE_WITH_ELECTRIC_SAW = modTag("mineable/electric_saw");
		public static final TagKey<Block> HEVEA_LOGS = modTag("hevea_logs");
		
		public static final TagKey<Block> COMPOSITE_BRICKS = modTag("composite_bricks");
		
		public static final TagKey<Block> ORES_TIN = tag("ores/tin");
		public static final TagKey<Block> ORES_LEAD = tag("ores/lead");
		public static final TagKey<Block> ORES_ALUMINUM = tag("ores/aluminum");
		public static final TagKey<Block> ORES_SILVER = tag("ores/silver");
		public static final TagKey<Block> ORES_ZINC = tag("ores/zinc");
		public static final TagKey<Block> ORES_TUNGSTEN = tag("ores/tungsten");
		public static final TagKey<Block> ORES_LITHIUM = tag("ores/lithium");
		public static final TagKey<Block> ORES_URANIUM = tag("ores/uranium");
		
		public static final TagKey<Block> STORAGE_BLOCKS_TIN = tag("storage_blocks/tin");
		public static final TagKey<Block> STORAGE_BLOCKS_LEAD = tag("storage_blocks/lead");
		public static final TagKey<Block> STORAGE_BLOCKS_ALUMINUM = tag("storage_blocks/aluminum");
		public static final TagKey<Block> STORAGE_BLOCKS_SILVER = tag("storage_blocks/silver");
		public static final TagKey<Block> STORAGE_BLOCKS_ZINC = tag("storage_blocks/zinc");
		public static final TagKey<Block> STORAGE_BLOCKS_TUNGSTEN = tag("storage_blocks/tungsten");
		
		public static final TagKey<Block> STORAGE_BLOCKS_RAW_TIN = tag("storage_blocks/raw_tin");
		public static final TagKey<Block> STORAGE_BLOCKS_RAW_LEAD = tag("storage_blocks/raw_lead");
		public static final TagKey<Block> STORAGE_BLOCKS_RAW_ALUMINUM = tag("storage_blocks/raw_aluminum");
		public static final TagKey<Block> STORAGE_BLOCKS_RAW_SILVER = tag("storage_blocks/raw_silver");
		public static final TagKey<Block> STORAGE_BLOCKS_RAW_ZINC = tag("storage_blocks/raw_zinc");
		public static final TagKey<Block> STORAGE_BLOCKS_RAW_TUNGSTEN = tag("storage_blocks/raw_tungsten");
		
		private static TagKey<Block> tag(String name)
		{
			return BlockTags.create(new ResourceLocation("forge", name));
		}
		
		private static TagKey<Block> vanillaTag(String name)
		{
			return BlockTags.create(new ResourceLocation("minecraft", name));
		}
		
		private static TagKey<Block> modTag(String name)
		{
			return BlockTags.create(ZeithTechAPI.id(name));
		}
	}
	
	public static class Fluids
	{
		private static void init()
		{
		}
		
		public static final TagKey<Fluid> HONEY = tag("honey");
		public static final TagKey<Fluid> BIOMASS = tag("biomass");
		public static final TagKey<Fluid> CRUDE_OIL = tag("crude_oil");
		public static final TagKey<Fluid> OIL = tag("oil");
		public static final TagKey<Fluid> DIESEL = tag("diesel");
		public static final TagKey<Fluid> GAS = tag("gas");
		public static final TagKey<Fluid> SULFURIC_ACID = tag("sulfuric_acid");
		
		private static TagKey<Fluid> tag(String name)
		{
			return FluidTags.create(new ResourceLocation("forge", name));
		}
		
		private static TagKey<Fluid> vanillaTag(String name)
		{
			return FluidTags.create(new ResourceLocation("minecraft", name));
		}
	}
}