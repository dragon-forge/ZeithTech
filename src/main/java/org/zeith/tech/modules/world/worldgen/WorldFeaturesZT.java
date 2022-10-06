package org.zeith.tech.modules.world.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.world.init.BlocksZT_World;
import org.zeith.tech.modules.world.worldgen.oil.OilLakeFeature;

import java.util.List;

import static net.minecraft.data.worldgen.features.OreFeatures.*;

public class WorldFeaturesZT
{
	private static final List<OreConfiguration.TargetBlockState> ORE_TIN_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, BlocksZT_World.TIN_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, BlocksZT_World.DEEPSLATE_TIN_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_LEAD_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, BlocksZT_World.LEAD_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, BlocksZT_World.DEEPSLATE_LEAD_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_ALUMINUM_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, BlocksZT_World.ALUMINUM_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, BlocksZT_World.DEEPSLATE_ALUMINUM_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_ZINC_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, BlocksZT_World.ZINC_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, BlocksZT_World.DEEPSLATE_ZINC_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_TUNGSTEN_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, BlocksZT_World.TUNGSTEN_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, BlocksZT_World.DEEPSLATE_TUNGSTEN_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_LITHIUM_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, BlocksZT_World.LITHIUM_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, BlocksZT_World.DEEPSLATE_LITHIUM_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_URANIUM_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, BlocksZT_World.URANIUM_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, BlocksZT_World.DEEPSLATE_URANIUM_ORE.defaultBlockState())
	);
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> TIN_ORE = register("tin_ore", Feature.ORE, new OreConfiguration(ORE_TIN_TARGET_LIST, 9));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> TIN_ORE_SMALL = register("small_tin_ore", Feature.ORE, new OreConfiguration(ORE_TIN_TARGET_LIST, 4));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> LEAD_ORE = register("lead_ore", Feature.ORE, new OreConfiguration(ORE_LEAD_TARGET_LIST, 9));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> LEAD_ORE_SMALL = register("small_lead_ore", Feature.ORE, new OreConfiguration(ORE_LEAD_TARGET_LIST, 4));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ALUMINUM_ORE = register("aluminum_ore", Feature.ORE, new OreConfiguration(ORE_ALUMINUM_TARGET_LIST, 9));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ALUMINUM_ORE_SMALL = register("small_aluminum_ore", Feature.ORE, new OreConfiguration(ORE_ALUMINUM_TARGET_LIST, 4));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ZINC_ORE = register("zinc_ore", Feature.ORE, new OreConfiguration(ORE_ZINC_TARGET_LIST, 10));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ZINC_ORE_SMALL = register("small_zinc_ore", Feature.ORE, new OreConfiguration(ORE_ZINC_TARGET_LIST, 6));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> TUNGSTEN_ORE = register("tungsten_ore", Feature.ORE, new OreConfiguration(ORE_TUNGSTEN_TARGET_LIST, 5));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> TUNGSTEN_ORE_SMALL = register("small_tungsten_ore", Feature.ORE, new OreConfiguration(ORE_TUNGSTEN_TARGET_LIST, 3));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> LITHIUM_ORE = register("lithium_ore", Feature.ORE, new OreConfiguration(ORE_LITHIUM_TARGET_LIST, 9));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> LITHIUM_ORE_SMALL = register("small_lithium_ore", Feature.ORE, new OreConfiguration(ORE_LITHIUM_TARGET_LIST, 4));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> BIG_URANIUM_ORE = register("big_uranium_ore", Feature.ORE, new OreConfiguration(ORE_URANIUM_TARGET_LIST, 12));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> URANIUM_ORE = register("uranium_ore", Feature.ORE, new OreConfiguration(ORE_URANIUM_TARGET_LIST, 6));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> URANIUM_ORE_SMALL = register("small_uranium_ore", Feature.ORE, new OreConfiguration(ORE_URANIUM_TARGET_LIST, 2));
	
	
	// TREES
	
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> HEVEA_TREE = register("hevea_tree", Feature.TREE,
			new TreeConfiguration.TreeConfigurationBuilder(
					BlockStateProvider.simple(BlocksZT_World.HEVEA_LOG),
					new StraightTrunkPlacer(6, 2, 2),
					BlockStateProvider.simple(BlocksZT_World.HEVEA_LEAVES.defaultBlockState()),
					new PineFoliagePlacer(ConstantInt.of(2), ConstantInt.of(2), ConstantInt.of(7)),
					new TwoLayersFeatureSize(1, 0, 1)
			)
					.dirt(BlockStateProvider.simple(Blocks.ROOTED_DIRT))
					.forceDirt()
					.build()
	);
	
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> HEVEA_TREE_WITH_BEES = register("hevea_tree_with_bees", Feature.TREE,
			new TreeConfiguration.TreeConfigurationBuilder(
					BlockStateProvider.simple(BlocksZT_World.HEVEA_LOG),
					new StraightTrunkPlacer(6, 2, 2),
					BlockStateProvider.simple(BlocksZT_World.HEVEA_LEAVES.defaultBlockState()),
					new PineFoliagePlacer(ConstantInt.of(2), ConstantInt.of(2), ConstantInt.of(7)),
					new TwoLayersFeatureSize(1, 0, 1)
			)
					.dirt(BlockStateProvider.simple(Blocks.ROOTED_DIRT))
					.forceDirt()
					.decorators(List.of(new BeehiveDecorator(0.05F)))
					.build()
	);
	
	public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> HEVEA_TREES_PLAINS = register("hevea_trees_plains", Feature.RANDOM_SELECTOR,
			new RandomFeatureConfiguration(List.of(),
					PlacementUtils.inlinePlaced(HEVEA_TREE_WITH_BEES))
	);
	
	// Oil
	
	public static final Holder<ConfiguredFeature<OilLakeFeature.OilLakeConfiguration, ?>> SMALL_OIL_LAKE = register("small_oil_lake", FeaturesZT.OIL_LAKE,
			new OilLakeFeature.OilLakeConfiguration(OilLakeFeature.LakeType.SMALL, UniformFloat.of(0.5F, 1F))
	);
	
	public static final Holder<ConfiguredFeature<OilLakeFeature.OilLakeConfiguration, ?>> MEDIUM_OIL_LAKE = register("medium_oil_lake", FeaturesZT.OIL_LAKE,
			new OilLakeFeature.OilLakeConfiguration(OilLakeFeature.LakeType.MEDIUM, UniformFloat.of(0.5F, 1F))
	);
	
	public static final Holder<ConfiguredFeature<OilLakeFeature.OilLakeConfiguration, ?>> LARGE_OIL_LAKE = register("large_oil_lake", FeaturesZT.OIL_LAKE,
			new OilLakeFeature.OilLakeConfiguration(OilLakeFeature.LakeType.LARGE, UniformFloat.of(0.8F, 1.2F))
	);
	
	private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> register(String id, F feature, FC configs)
	{
		return FeatureUtils.register(ZeithTech.MOD_ID + ":" + id, feature, configs);
	}
}