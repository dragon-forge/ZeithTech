package org.zeith.tech.modules.world.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.*;
import org.zeith.tech.api.tile.BlockEntityTypeModifier;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.shared.blocks.SimpleBlockZT;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.world.blocks.*;
import org.zeith.tech.modules.world.blocks.tap.BlockHeveaTreeTap;

import java.util.List;

import static net.minecraft.world.item.Tiers.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.of;
import static org.zeith.hammerlib.core.adapter.BlockHarvestAdapter.MineableType.PICKAXE;

@SimplyRegister
public interface BlocksZT_World
{
	// Hevea tree stuff
	
	@RegistryName("hevea_planks")
	SimpleBlockZT HEVEA_PLANKS = new SimpleBlockZT(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.PLANKS)).addItemTag(ItemTags.PLANKS).dropsSelf();
	
	@RegistryName("stripped_hevea_log")
	BlockHeveaLog STRIPPED_HEVEA_LOG = BaseZT.heveaLog(MaterialColor.WOOD, MaterialColor.PODZOL, true).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS_THAT_BURN, BlockTags.PARROTS_SPAWNABLE_ON, BlockTags.LOGS)).addItemTags(List.of(ItemTags.LOGS_THAT_BURN, ItemTags.LOGS, TagsZT.Items.HEVEA_LOGS)).dropsSelf();
	
	@RegistryName("stripped_hevea_wood")
	BlockHeveaLog STRIPPED_HEVEA_WOOD = BaseZT.heveaLog(MaterialColor.WOOD, MaterialColor.PODZOL, true).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS_THAT_BURN, BlockTags.PARROTS_SPAWNABLE_ON, BlockTags.LOGS)).addItemTags(List.of(ItemTags.LOGS_THAT_BURN, ItemTags.LOGS, TagsZT.Items.HEVEA_LOGS)).dropsSelf();
	
	@RegistryName("hevea_log")
	BlockHeveaLog HEVEA_LOG = BaseZT.heveaLog(MaterialColor.WOOD, MaterialColor.PODZOL, false).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS_THAT_BURN, BlockTags.OVERWORLD_NATURAL_LOGS, BlockTags.PARROTS_SPAWNABLE_ON, BlockTags.LOGS)).addItemTags(List.of(ItemTags.LOGS_THAT_BURN, ItemTags.OVERWORLD_NATURAL_LOGS, ItemTags.LOGS, TagsZT.Items.HEVEA_LOGS)).toolModify(BlockHeveaLog.HEVEA_LOG_STRIPPING.get()).dropsSelf();
	
	@RegistryName("hevea_wood")
	BlockHeveaLog HEVEA_WOOD = BaseZT.heveaLog(MaterialColor.WOOD, MaterialColor.PODZOL, false).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS_THAT_BURN, BlockTags.OVERWORLD_NATURAL_LOGS, BlockTags.PARROTS_SPAWNABLE_ON, BlockTags.LOGS)).addItemTags(List.of(ItemTags.LOGS_THAT_BURN, ItemTags.OVERWORLD_NATURAL_LOGS, ItemTags.LOGS, TagsZT.Items.HEVEA_LOGS)).toolModify(BlockHeveaLog.HEVEA_WOOD_STRIPPING.get()).dropsSelf();
	
	@RegistryName("hevea_leaves")
	BlockHeveaLeaves HEVEA_LEAVES = BaseZT.leaves(SoundType.GRASS).addBlockTags(List.of(BlockTags.LEAVES, BlockTags.COMPLETES_FIND_TREE_TUTORIAL, BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE, BlockTags.PARROTS_SPAWNABLE_ON, BlockTags.MINEABLE_WITH_HOE)).addItemTags(List.of(ItemTags.LEAVES, ItemTags.COMPLETES_FIND_TREE_TUTORIAL));
	
	@RegistryName("hevea_sapling")
	BlockHeveaSapling HEVEA_SAPLING = new BlockHeveaSapling(new GrowerHeveaTree(), BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.SAPLINGS)).addItemTag(ItemTags.SAPLINGS).dropsSelf();
	
	@RegistryName("hevea_button")
	BlockHeveaWoodButton HEVEA_BUTTON = new BlockHeveaWoodButton(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.BUTTONS, BlockTags.WOODEN_BUTTONS)).addItemTags(List.of(ItemTags.BUTTONS, ItemTags.WOODEN_BUTTONS)).dropsSelf();
	
	@RegistryName("hevea_stairs")
	BlockHeveaStair HEVEA_STAIRS = new BlockHeveaStair(HEVEA_PLANKS::defaultBlockState, BlockBehaviour.Properties.copy(HEVEA_PLANKS)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.STAIRS, BlockTags.WOODEN_STAIRS)).addItemTags(List.of(ItemTags.STAIRS, ItemTags.WOODEN_STAIRS)).dropsSelf();
	
	@RegistryName("hevea_slab")
	BlockHeveaSlab HEVEA_SLAB = new BlockHeveaSlab(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).strength(2.0F, 3.0F).sound(SoundType.WOOD)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.SLABS, BlockTags.WOODEN_SLABS)).addItemTags(List.of(ItemTags.SLABS, ItemTags.WOODEN_SLABS)).dropsSelf();
	
	@RegistryName("hevea_fence_gate")
	BlockHeveaFenceGate HEVEA_FENCE_GATE = new BlockHeveaFenceGate(BlockBehaviour.Properties.of(Material.WOOD, HEVEA_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.FENCE_GATES, BlockTags.UNSTABLE_BOTTOM_CENTER, Tags.Blocks.FENCE_GATES_WOODEN)).dropsSelf();
	
	@RegistryName("hevea_fence")
	BlockHeveaFence HEVEA_FENCE = new BlockHeveaFence(BlockBehaviour.Properties.of(Material.WOOD, HEVEA_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.FENCES, BlockTags.WOODEN_FENCES)).addItemTags(List.of(ItemTags.FENCES, ItemTags.WOODEN_FENCES)).dropsSelf();
	
	@RegistryName("hevea_door")
	BlockHeveaDoor HEVEA_DOOR = new BlockHeveaDoor(BlockBehaviour.Properties.of(Material.WOOD, HEVEA_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.WOODEN_DOORS, BlockTags.DOORS)).addItemTags(List.of(ItemTags.DOORS, ItemTags.WOODEN_DOORS)).dropsSelf();
	
	@RegistryName("hevea_sign")
	BlockHeveaStandingSign HEVEA_SIGN = new BlockHeveaStandingSign(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).noCollission().strength(1.0F).sound(SoundType.WOOD), ConstantsZT_World.HEVEA).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.SIGNS, BlockTags.STANDING_SIGNS, BlockTags.WALL_POST_OVERRIDE));
	
	@RegistryName("hevea_wall_sign")
	BlockHeveaWallSign HEVEA_WALL_SIGN = new BlockHeveaWallSign(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).noCollission().strength(1.0F).sound(SoundType.WOOD), ConstantsZT_World.HEVEA).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.SIGNS, BlockTags.WALL_SIGNS, BlockTags.WALL_POST_OVERRIDE));
	
	@RegistryName("hevea_pressure_plate")
	BlockHeveaPressurePlate HEVEA_PRESSURE_PLATE = new BlockHeveaPressurePlate(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of(Material.WOOD, HEVEA_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.WALL_POST_OVERRIDE, BlockTags.PRESSURE_PLATES, BlockTags.WOODEN_PRESSURE_PLATES)).addItemTag(ItemTags.WOODEN_PRESSURE_PLATES).dropsSelf();
	
	@RegistryName("hevea_trapdoor")
	BlockHeveaTrapDoor HEVEA_TRAP_DOOR = new BlockHeveaTrapDoor(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(BaseZT::never)).addBlockTags(List.of(BlockTags.WOODEN_TRAPDOORS, BlockTags.TRAPDOORS, BlockTags.MINEABLE_WITH_AXE)).addItemTags(List.of(ItemTags.TRAPDOORS, ItemTags.WOODEN_TRAPDOORS)).dropsSelf();
	
	@RegistryName("potted_hevea_sapling")
	BlockHeveaFlowerPot POTTED_HEVEA_SAPLING = new BlockHeveaFlowerPot(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> HEVEA_SAPLING, BlockBehaviour.Properties.of(Material.DECORATION).instabreak().noOcclusion());
	
	@RegistryName("hevea_chest")
	BlockHeveaChest HEVEA_CHEST = new BlockHeveaChest(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD), () -> BlockEntityType.CHEST).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.FEATURES_CANNOT_REPLACE, BlockTags.GUARDED_BY_PIGLINS, BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE, Tags.Blocks.CHESTS, Tags.Blocks.CHESTS_WOODEN)).addItemTags(List.of(Tags.Items.CHESTS, Tags.Items.CHESTS_WOODEN));
	
	@RegistryName("hevea_trapped_chest")
	BlockHeveaChest HEVEA_TRAPPED_CHEST = new BlockHeveaTrappedChest(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD), () -> BlockEntityType.TRAPPED_CHEST).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.FEATURES_CANNOT_REPLACE, BlockTags.GUARDED_BY_PIGLINS, BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE, Tags.Blocks.CHESTS, Tags.Blocks.CHESTS_TRAPPED)).addItemTags(List.of(Tags.Items.CHESTS, Tags.Items.CHESTS_TRAPPED));
	
	@RegistryName("hevea_tree_tap")
	BlockHeveaTreeTap HEVEA_TREE_TAP = new BlockHeveaTreeTap(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
	
	// Normal ores
	
	@RegistryName("tin_ore")
	SimpleBlockZT TIN_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.ORES_TIN).addItemTag(TagsZT.Items.ORES_TIN);
	@RegistryName("lead_ore")
	SimpleBlockZT LEAD_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.ORES_LEAD).addItemTag(TagsZT.Items.ORES_LEAD);
	@RegistryName("aluminum_ore")
	SimpleBlockZT ALUMINUM_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.ORES_ALUMINUM).addItemTag(TagsZT.Items.ORES_ALUMINUM);
	@RegistryName("lithium_ore")
	SimpleBlockZT LITHIUM_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.ORES_LITHIUM).addItemTag(TagsZT.Items.ORES_LITHIUM).dropsSelf();
	@RegistryName("uranium_ore")
	SimpleBlockZT URANIUM_ORE = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, IRON).addBlockTag(TagsZT.Blocks.ORES_URANIUM).addItemTag(TagsZT.Items.ORES_URANIUM).dropsSelf();
	
	// Deepslate ores
	
	@RegistryName("deepslate_tin_ore")
	SimpleBlockZT DEEPSLATE_TIN_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.ORES_TIN).addItemTag(TagsZT.Items.ORES_TIN);
	@RegistryName("deepslate_lead_ore")
	SimpleBlockZT DEEPSLATE_LEAD_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.ORES_LEAD).addItemTag(TagsZT.Items.ORES_LEAD);
	@RegistryName("deepslate_aluminum_ore")
	SimpleBlockZT DEEPSLATE_ALUMINUM_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.ORES_ALUMINUM).addItemTag(TagsZT.Items.ORES_ALUMINUM);
	@RegistryName("deepslate_lithium_ore")
	SimpleBlockZT DEEPSLATE_LITHIUM_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.ORES_LITHIUM).addItemTag(TagsZT.Items.ORES_LITHIUM).dropsSelf();
	@RegistryName("deepslate_uranium_ore")
	SimpleBlockZT DEEPSLATE_URANIUM_ORE = new SimpleBlockZT(of(Material.STONE).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F), PICKAXE, IRON).addBlockTag(TagsZT.Blocks.ORES_URANIUM).addItemTag(TagsZT.Items.ORES_URANIUM).dropsSelf();
	
	// Raw blocks
	
	@RegistryName("raw_tin_block")
	SimpleBlockZT RAW_TIN_BLOCK = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.STORAGE_BLOCKS_RAW_TIN).addItemTag(TagsZT.Items.STORAGE_BLOCKS_RAW_TIN).dropsSelf();
	@RegistryName("raw_lead_block")
	SimpleBlockZT RAW_LEAD_BLOCK = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.STORAGE_BLOCKS_RAW_LEAD).addItemTag(TagsZT.Items.STORAGE_BLOCKS_RAW_LEAD).dropsSelf();
	@RegistryName("raw_aluminum_block")
	SimpleBlockZT RAW_ALUMINUM_BLOCK = new SimpleBlockZT(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), PICKAXE, STONE).addBlockTags(List.of(TagsZT.Blocks.STORAGE_BLOCKS_RAW_ALUMINUM, BlockTags.BEACON_BASE_BLOCKS)).addItemTag(TagsZT.Items.STORAGE_BLOCKS_RAW_ALUMINUM).dropsSelf();
	
	// Metal blocks
	
	@RegistryName("tin_block")
	SimpleBlockZT TIN_BLOCK = new SimpleBlockZT(of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F), PICKAXE, STONE).addBlockTags(List.of(TagsZT.Blocks.STORAGE_BLOCKS_TIN, BlockTags.BEACON_BASE_BLOCKS)).addItemTag(TagsZT.Items.STORAGE_BLOCKS_TIN).dropsSelf();
	@RegistryName("lead_block")
	SimpleBlockZT LEAD_BLOCK = new SimpleBlockZT(of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F), PICKAXE, STONE).addBlockTags(List.of(BlockTags.BEACON_BASE_BLOCKS, TagsZT.Blocks.STORAGE_BLOCKS_LEAD)).addItemTag(TagsZT.Items.STORAGE_BLOCKS_LEAD).dropsSelf();
	@RegistryName("aluminum_block")
	SimpleBlockZT ALUMINUM_BLOCK = new SimpleBlockZT(of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F), PICKAXE, STONE).addBlockTag(TagsZT.Blocks.STORAGE_BLOCKS_ALUMINUM).addItemTag(TagsZT.Items.STORAGE_BLOCKS_ALUMINUM).dropsSelf();
	
	static @Setup void registerExtra()
	{
		((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(HEVEA_SAPLING), () -> POTTED_HEVEA_SAPLING);
		
		BlockEntityTypeModifier.addBlocksToEntityType(BlockEntityType.SIGN, HEVEA_SIGN, HEVEA_WALL_SIGN);
		BlockEntityTypeModifier.addBlocksToEntityType(BlockEntityType.CHEST, HEVEA_CHEST);
		BlockEntityTypeModifier.addBlocksToEntityType(BlockEntityType.TRAPPED_CHEST, HEVEA_TRAPPED_CHEST);
	}
}