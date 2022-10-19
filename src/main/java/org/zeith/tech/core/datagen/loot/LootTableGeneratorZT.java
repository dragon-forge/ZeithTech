package org.zeith.tech.core.datagen.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.ItemsZT;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class LootTableGeneratorZT
		implements DataProvider
{
	private static final Set<Item> EXPLOSION_RESISTANT = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(ItemLike::asItem).collect(ImmutableSet.toImmutableSet());
	
	private static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
	private static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
	private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
	private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
	private static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[] {
			0.05F,
			0.0625F,
			0.083333336F,
			0.1F
	};
	private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[] {
			0.02F,
			0.022222223F,
			0.025F,
			0.033333335F,
			0.1F
	};
	
	private final DataGenerator generator;
	
	BiConsumer<ResourceLocation, LootTable.Builder> consumer;
	
	public LootTableGeneratorZT(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(CachedOutput cache) throws IOException
	{
		Set<ResourceLocation> set = Sets.newHashSet();
		var prov = generator.createPathProvider(DataGenerator.Target.DATA_PACK, "loot_tables");
		consumer = (id, table) ->
		{
			if(!set.add(id))
			{
				throw new IllegalStateException("Duplicate loot table " + id);
			} else
			{
				Path path1 = prov.json(id);
				
				try
				{
					DataProvider.saveStable(cache, LootTables.serialize(table.build()), path1);
				} catch(IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		};
		
		generateLootTables();
	}
	
	private void generateLootTables()
	{
		add(BlocksZT.HEVEA_LEAVES, block -> createLeavesDrops(block, BlocksZT.HEVEA_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES));
		dropPottedContents(BlocksZT.POTTED_HEVEA_SAPLING);
		add(BlocksZT.ALUMINUM_ORE, block -> createOreDrop(block, ItemsZT.RAW_ALUMINUM));
		add(BlocksZT.DEEPSLATE_ALUMINUM_ORE, block -> createOreDrop(block, ItemsZT.RAW_ALUMINUM));
		add(BlocksZT.LEAD_ORE, block -> createOreDrop(block, ItemsZT.RAW_LEAD));
		add(BlocksZT.DEEPSLATE_LEAD_ORE, block -> createOreDrop(block, ItemsZT.RAW_LEAD));
		add(BlocksZT.TIN_ORE, block -> createOreDrop(block, ItemsZT.RAW_TIN));
		add(BlocksZT.DEEPSLATE_TIN_ORE, block -> createOreDrop(block, ItemsZT.RAW_TIN));
		add(BlocksZT.SILVER_ORE, block -> createOreDrop(block, ItemsZT.RAW_SILVER));
		add(BlocksZT.DEEPSLATE_SILVER_ORE, block -> createOreDrop(block, ItemsZT.RAW_SILVER));
		add(BlocksZT.TUNGSTEN_ORE, block -> createOreDrop(block, ItemsZT.RAW_TUNGSTEN));
		add(BlocksZT.DEEPSLATE_TUNGSTEN_ORE, block -> createOreDrop(block, ItemsZT.RAW_TUNGSTEN));
		add(BlocksZT.ZINC_ORE, block -> createOreDrop(block, ItemsZT.RAW_ZINC));
		add(BlocksZT.DEEPSLATE_ZINC_ORE, block -> createOreDrop(block, ItemsZT.RAW_ZINC));
	}
	
	protected void add(Block block, Function<Block, LootTable.Builder> builder)
	{
		consumer.accept(block.getLootTable(), builder.apply(block).setParamSet(LootContextParamSets.BLOCK));
	}
	
	public void dropPottedContents(FlowerPotBlock block)
	{
		this.add(block, b -> createPotFlowerItemTable(((FlowerPotBlock) b).getContent()));
	}
	
	protected static LootTable.Builder createPotFlowerItemTable(ItemLike p_124271_)
	{
		return LootTable.lootTable().withPool(applyExplosionCondition(Blocks.FLOWER_POT, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.FLOWER_POT)))).withPool(applyExplosionCondition(p_124271_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_124271_))));
	}
	
	protected static <T extends ConditionUserBuilder<T>> T applyExplosionCondition(ItemLike p_236225_, ConditionUserBuilder<T> p_236226_)
	{
		return (T) (!EXPLOSION_RESISTANT.contains(p_236225_.asItem()) ? p_236226_.when(ExplosionCondition.survivesExplosion()) : p_236226_.unwrap());
	}
	
	protected static <T extends FunctionUserBuilder<T>> T applyExplosionDecay(ItemLike p_236222_, FunctionUserBuilder<T> p_236223_)
	{
		return (T) (!EXPLOSION_RESISTANT.contains(p_236222_.asItem()) ? p_236223_.apply(ApplyExplosionDecay.explosionDecay()) : p_236223_.unwrap());
	}
	
	protected static LootTable.Builder createOreDrop(Block p_124140_, Item p_124141_)
	{
		return createSilkTouchDispatchTable(p_124140_, applyExplosionDecay(p_124140_, LootItem.lootTableItem(p_124141_).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
	}
	
	protected static LootTable.Builder createSilkTouchDispatchTable(Block p_124169_, LootPoolEntryContainer.Builder<?> p_124170_)
	{
		return createSelfDropDispatchTable(p_124169_, HAS_SILK_TOUCH, p_124170_);
	}
	
	protected static LootTable.Builder createLeavesDrops(Block block, Block sapling, float... saplingChances)
	{
		return createSilkTouchOrShearsDispatchTable(block, applyExplosionCondition(block, LootItem.lootTableItem(sapling)).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, saplingChances))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(applyExplosionDecay(block, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))));
	}
	
	protected static LootTable.Builder createSilkTouchOrShearsDispatchTable(Block p_124284_, LootPoolEntryContainer.Builder<?> p_124285_)
	{
		return createSelfDropDispatchTable(p_124284_, HAS_SHEARS_OR_SILK_TOUCH, p_124285_);
	}
	
	protected static LootTable.Builder createSelfDropDispatchTable(Block p_124172_, LootItemCondition.Builder p_124173_, LootPoolEntryContainer.Builder<?> p_124174_)
	{
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_124172_).when(p_124173_).otherwise(p_124174_)));
	}
	
	@Override
	public String getName()
	{
		return "Loot Table";
	}
}