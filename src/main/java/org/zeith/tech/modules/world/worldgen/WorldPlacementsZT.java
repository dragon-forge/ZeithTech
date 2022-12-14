package org.zeith.tech.modules.world.worldgen;

import net.minecraft.core.*;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;
import org.zeith.hammerlib.annotations.Setup;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.world.init.BlocksZT_World;

import java.util.List;

import static net.minecraft.data.worldgen.placement.VegetationPlacements.TREE_THRESHOLD;

public class WorldPlacementsZT
{
	public static final Holder<PlacedFeature> ORE_TIN_UPPER = register("ore_tin_upper", WorldFeaturesZT.TIN_ORE, commonOrePlacement(25, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	public static final Holder<PlacedFeature> ORE_TIN_MIDDLE = register("ore_tin_middle", WorldFeaturesZT.TIN_ORE, commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_TIN_SMALL = register("ore_tin_small", WorldFeaturesZT.TIN_ORE_SMALL, commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_LEAD_UPPER = register("ore_lead_upper", WorldFeaturesZT.LEAD_ORE, commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	public static final Holder<PlacedFeature> ORE_LEAD_MIDDLE = register("ore_lead_middle", WorldFeaturesZT.LEAD_ORE, commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_LEAD_SMALL = register("ore_lead_small", WorldFeaturesZT.LEAD_ORE_SMALL, commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_ALUMINUM_UPPER = register("ore_aluminum_upper", WorldFeaturesZT.ALUMINUM_ORE, commonOrePlacement(25, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	public static final Holder<PlacedFeature> ORE_ALUMINUM_MIDDLE = register("ore_aluminum_middle", WorldFeaturesZT.ALUMINUM_ORE, commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_ALUMINUM_SMALL = register("ore_aluminum_small", WorldFeaturesZT.ALUMINUM_ORE_SMALL, commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_SILVER_MIDDLE = register("ore_silver_middle", WorldFeaturesZT.SILVER_ORE, commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(32))));
	public static final Holder<PlacedFeature> ORE_SILVER_SMALL = register("ore_silver_small", WorldFeaturesZT.SILVER_ORE_SMALL, commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(48))));
	
	public static final Holder<PlacedFeature> ORE_ZINC_MIDDLE = register("ore_zinc_middle", WorldFeaturesZT.ZINC_ORE, commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_ZINC_SMALL = register("ore_zinc_small", WorldFeaturesZT.ZINC_ORE_SMALL, commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_TUNGSTEN_MIDDLE = register("ore_tungsten_middle", WorldFeaturesZT.TUNGSTEN_ORE, commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_TUNGSTEN_SMALL = register("ore_tungsten_small", WorldFeaturesZT.TUNGSTEN_ORE_SMALL, commonOrePlacement(3, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_LITHIUM_UPPER = register("ore_lithium_upper", WorldFeaturesZT.LITHIUM_ORE, commonOrePlacement(6, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	public static final Holder<PlacedFeature> ORE_LITHIUM_MIDDLE = register("ore_lithium_middle", WorldFeaturesZT.LITHIUM_ORE, commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_LITHIUM_SMALL = register("ore_lithium_small", WorldFeaturesZT.LITHIUM_ORE_SMALL, commonOrePlacement(3, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_URANIUM_UPPER = register("ore_uranium_upper", WorldFeaturesZT.URANIUM_ORE_SMALL, commonOrePlacement(8, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(128))));
	public static final Holder<PlacedFeature> ORE_URANIUM_MIDDLE = register("ore_uranium_middle", WorldFeaturesZT.URANIUM_ORE, commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(72))));
	public static final Holder<PlacedFeature> ORE_URANIUM_DEEP = register("ore_uranium_deep", WorldFeaturesZT.BIG_URANIUM_ORE, commonOrePlacement(6, HeightRangePlacement.of(BiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(8), 1))));
	
	public static final Holder<PlacedFeature> HEVEA_TREES_PLAINS = register("hevea_trees_plains", WorldFeaturesZT.HEVEA_TREES_PLAINS, List.of(PlacementUtils.countExtra(0, 0.25F, 1), InSquarePlacement.spread(), TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(BlocksZT_World.HEVEA_SAPLING.defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome()));
	
	public static final Holder<PlacedFeature> SMALL_UNDERGROUND_OIL_LAKES = register("small_underground_oil_lake", WorldFeaturesZT.SMALL_OIL_LAKE, List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(24))), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5), BiomeFilter.biome()));
	public static final Holder<PlacedFeature> MEDIUM_UNDERGROUND_OIL_LAKES = register("medium_underground_oil_lake", WorldFeaturesZT.MEDIUM_OIL_LAKE, List.of(RarityFilter.onAverageOnceEvery(20), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(12))), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5), BiomeFilter.biome()));
	public static final Holder<PlacedFeature> LARGE_UNDERGROUND_OIL_LAKES = register("large_underground_oil_lake", WorldFeaturesZT.LARGE_OIL_LAKE, List.of(RarityFilter.onAverageOnceEvery(40), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.aboveBottom(40))), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5), BiomeFilter.biome()));
	
	@Setup // Causes this class to initialize.
	public static void setup()
	{
		ZeithTech.LOG.info("Registered placed features into Minecraft.");
	}
	
	private static Holder<PlacedFeature> register(String p_206510_, Holder<? extends ConfiguredFeature<?, ?>> p_206511_, List<PlacementModifier> p_206512_)
	{
		return PlacementUtils.register(ZeithTech.MOD_ID + ":" + p_206510_, p_206511_, p_206512_);
	}
	
	private static List<PlacementModifier> orePlacement(PlacementModifier mod1, PlacementModifier mod2)
	{
		return List.of(mod1, InSquarePlacement.spread(), mod2, BiomeFilter.biome());
	}
	
	private static List<PlacementModifier> commonOrePlacement(int perChunk, PlacementModifier mod)
	{
		return orePlacement(CountPlacement.of(perChunk), mod);
	}
	
	private static List<PlacementModifier> rareOrePlacement(int onceEvery, PlacementModifier mod)
	{
		return orePlacement(RarityFilter.onAverageOnceEvery(onceEvery), mod);
	}
}