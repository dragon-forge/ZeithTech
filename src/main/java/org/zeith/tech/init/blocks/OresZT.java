package org.zeith.tech.init.blocks;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.common.blocks.SimpleBlockZT;
import org.zeith.tech.init.TagsZT;

import static net.minecraft.world.item.Tiers.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.of;
import static org.zeith.hammerlib.core.adapter.BlockHarvestAdapter.MineableType.PICKAXE;
import static org.zeith.tech.init.BaseZT.newItem;

@SimplyRegister
public interface OresZT
{
	// Normal ores
	
	@RegistryName("tin_ore")
	SimpleBlockZT TIN_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.ORES_TIN)
			.addItemTag(TagsZT.Items.ORES_TIN);
	@RegistryName("lead_ore")
	SimpleBlockZT LEAD_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.ORES_LEAD)
			.addItemTag(TagsZT.Items.ORES_LEAD);
	@RegistryName("aluminum_ore")
	SimpleBlockZT ALUMINUM_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.ORES_ALUMINUM)
			.addItemTag(TagsZT.Items.ORES_ALUMINUM);
	@RegistryName("lithium_ore")
	SimpleBlockZT LITHIUM_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.ORES_LITHIUM)
			.addItemTag(TagsZT.Items.ORES_LITHIUM)
			.dropsSelf();
	@RegistryName("uranium_ore")
	SimpleBlockZT URANIUM_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, IRON)
			.addBlockTag(TagsZT.Blocks.ORES_URANIUM)
			.addItemTag(TagsZT.Items.ORES_URANIUM)
			.dropsSelf();
	
	// Deepslate ores
	
	@RegistryName("deepslate_tin_ore")
	SimpleBlockZT DEEPSLATE_TIN_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.ORES_TIN)
			.addItemTag(TagsZT.Items.ORES_TIN);
	@RegistryName("deepslate_lead_ore")
	SimpleBlockZT DEEPSLATE_LEAD_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.ORES_LEAD)
			.addItemTag(TagsZT.Items.ORES_LEAD);
	@RegistryName("deepslate_aluminum_ore")
	SimpleBlockZT DEEPSLATE_ALUMINUM_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.ORES_ALUMINUM)
			.addItemTag(TagsZT.Items.ORES_ALUMINUM);
	@RegistryName("deepslate_lithium_ore")
	SimpleBlockZT DEEPSLATE_LITHIUM_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.ORES_LITHIUM)
			.addItemTag(TagsZT.Items.ORES_LITHIUM)
			.dropsSelf();
	@RegistryName("deepslate_uranium_ore")
	SimpleBlockZT DEEPSLATE_URANIUM_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, IRON)
			.addBlockTag(TagsZT.Blocks.ORES_URANIUM)
			.addItemTag(TagsZT.Items.ORES_URANIUM)
			.dropsSelf();
	
	// Raw blocks
	
	@RegistryName("raw_tin_block")
	SimpleBlockZT RAW_TIN_BLOCK = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.STORAGE_BLOCKS_RAW_TIN)
			.addItemTag(TagsZT.Items.STORAGE_BLOCKS_RAW_TIN)
			.dropsSelf();
	@RegistryName("raw_lead_block")
	SimpleBlockZT RAW_LEAD_BLOCK = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.STORAGE_BLOCKS_RAW_LEAD)
			.addItemTag(TagsZT.Items.STORAGE_BLOCKS_RAW_LEAD)
			.dropsSelf();
	@RegistryName("raw_aluminum_block")
	SimpleBlockZT RAW_ALUMINUM_BLOCK = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.STORAGE_BLOCKS_RAW_ALUMINUM)
			.addBlockTag(BlockTags.BEACON_BASE_BLOCKS)
			.addItemTag(TagsZT.Items.STORAGE_BLOCKS_RAW_ALUMINUM)
			.dropsSelf();
	
	// Metal blocks
	
	@RegistryName("tin_block")
	SimpleBlockZT TIN_BLOCK = new SimpleBlockZT(of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.STORAGE_BLOCKS_TIN)
			.addBlockTag(BlockTags.BEACON_BASE_BLOCKS)
			.addItemTag(TagsZT.Items.STORAGE_BLOCKS_TIN)
			.dropsSelf();
	@RegistryName("lead_block")
	SimpleBlockZT LEAD_BLOCK = new SimpleBlockZT(of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.STORAGE_BLOCKS_LEAD)
			.addBlockTag(BlockTags.BEACON_BASE_BLOCKS)
			.addItemTag(TagsZT.Items.STORAGE_BLOCKS_LEAD)
			.dropsSelf();
	@RegistryName("aluminum_block")
	SimpleBlockZT ALUMINUM_BLOCK = new SimpleBlockZT(of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F), PICKAXE, STONE)
			.addBlockTag(TagsZT.Blocks.STORAGE_BLOCKS_ALUMINUM)
			.addItemTag(TagsZT.Items.STORAGE_BLOCKS_ALUMINUM)
			.dropsSelf();
	
	// ITEMS
	
	// Raw ores
	
	@RegistryName("raw_tin")
	Item RAW_TIN = newItem(TagsZT.Items.RAW_MATERIALS_TIN);
	@RegistryName("raw_lead")
	Item RAW_LEAD = newItem(TagsZT.Items.RAW_MATERIALS_LEAD);
	@RegistryName("raw_aluminum")
	Item RAW_ALUMINUM = newItem(TagsZT.Items.RAW_MATERIALS_ALUMINUM);
	
	// Ingots
	
	@RegistryName("tin_ingot")
	Item TIN_INGOT = newItem(TagsZT.Items.INGOTS_TIN);
	@RegistryName("lead_ingot")
	Item LEAD_INGOT = newItem(TagsZT.Items.INGOTS_LEAD);
	@RegistryName("aluminum_ingot")
	Item ALUMINUM_INGOT = newItem(TagsZT.Items.INGOTS_ALUMINUM);
}