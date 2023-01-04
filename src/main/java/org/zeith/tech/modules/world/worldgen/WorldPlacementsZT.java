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
import org.zeith.hammerlib.annotations.*;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.world.init.BlocksZT_World;

import java.util.List;

@SimplyRegister
public class WorldPlacementsZT
{
	private static final PlacementModifier TREE_THRESHOLD = SurfaceWaterDepthFilter.forMaxDepth(0);
	
	@RegistryName("ore_tin_upper")
	public static final PlacedFeature ORE_TIN_UPPER = register(WorldFeaturesZT.TIN_ORE, commonOrePlacement(25, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	@RegistryName("ore_tin_middle")
	public static final PlacedFeature ORE_TIN_MIDDLE = register(WorldFeaturesZT.TIN_ORE, commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	@RegistryName("ore_tin_small")
	public static final PlacedFeature ORE_TIN_SMALL = register(WorldFeaturesZT.TIN_ORE_SMALL, commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	@RegistryName("ore_lead_upper")
	public static final PlacedFeature ORE_LEAD_UPPER = register(WorldFeaturesZT.LEAD_ORE, commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	@RegistryName("ore_lead_middle")
	public static final PlacedFeature ORE_LEAD_MIDDLE = register(WorldFeaturesZT.LEAD_ORE, commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	@RegistryName("ore_lead_small")
	public static final PlacedFeature ORE_LEAD_SMALL = register(WorldFeaturesZT.LEAD_ORE_SMALL, commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	@RegistryName("ore_aluminum_upper")
	public static final PlacedFeature ORE_ALUMINUM_UPPER = register(WorldFeaturesZT.ALUMINUM_ORE, commonOrePlacement(25, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	@RegistryName("ore_aluminum_middle")
	public static final PlacedFeature ORE_ALUMINUM_MIDDLE = register(WorldFeaturesZT.ALUMINUM_ORE, commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	@RegistryName("ore_aluminum_small")
	public static final PlacedFeature ORE_ALUMINUM_SMALL = register(WorldFeaturesZT.ALUMINUM_ORE_SMALL, commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	@RegistryName("ore_silver_middle")
	public static final PlacedFeature ORE_SILVER_MIDDLE = register(WorldFeaturesZT.SILVER_ORE, commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(32))));
	@RegistryName("ore_silver_small")
	public static final PlacedFeature ORE_SILVER_SMALL = register(WorldFeaturesZT.SILVER_ORE_SMALL, commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(48))));
	
	@RegistryName("ore_zinc_middle")
	public static final PlacedFeature ORE_ZINC_MIDDLE = register(WorldFeaturesZT.ZINC_ORE, commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	@RegistryName("ore_zinc_small")
	public static final PlacedFeature ORE_ZINC_SMALL = register(WorldFeaturesZT.ZINC_ORE_SMALL, commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	@RegistryName("ore_tungsten_middle")
	public static final PlacedFeature ORE_TUNGSTEN_MIDDLE = register(WorldFeaturesZT.TUNGSTEN_ORE, commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	@RegistryName("ore_tungsten_small")
	public static final PlacedFeature ORE_TUNGSTEN_SMALL = register(WorldFeaturesZT.TUNGSTEN_ORE_SMALL, commonOrePlacement(3, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	@RegistryName("ore_lithium_upper")
	public static final PlacedFeature ORE_LITHIUM_UPPER = register(WorldFeaturesZT.LITHIUM_ORE, commonOrePlacement(6, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	@RegistryName("ore_lithium_middle")
	public static final PlacedFeature ORE_LITHIUM_MIDDLE = register(WorldFeaturesZT.LITHIUM_ORE, commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	@RegistryName("ore_lithium_small")
	public static final PlacedFeature ORE_LITHIUM_SMALL = register(WorldFeaturesZT.LITHIUM_ORE_SMALL, commonOrePlacement(3, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	@RegistryName("ore_uranium_upper")
	public static final PlacedFeature ORE_URANIUM_UPPER = register(WorldFeaturesZT.URANIUM_ORE_SMALL, commonOrePlacement(8, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(128))));
	@RegistryName("ore_uranium_middle")
	public static final PlacedFeature ORE_URANIUM_MIDDLE = register(WorldFeaturesZT.URANIUM_ORE, commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(72))));
	@RegistryName("ore_uranium_deep")
	public static final PlacedFeature ORE_URANIUM_DEEP = register(WorldFeaturesZT.BIG_URANIUM_ORE, commonOrePlacement(6, HeightRangePlacement.of(BiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(8), 1))));
	
	@RegistryName("hevea_trees_plains")
	public static final PlacedFeature HEVEA_TREES_PLAINS = register(WorldFeaturesZT.HEVEA_TREES_PLAINS, List.of(PlacementUtils.countExtra(0, 0.25F, 1), InSquarePlacement.spread(), TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(BlocksZT_World.HEVEA_SAPLING.defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome()));
	
	@RegistryName("small_underground_oil_lake")
	public static final PlacedFeature SMALL_UNDERGROUND_OIL_LAKES = register(WorldFeaturesZT.SMALL_OIL_LAKE, List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(24))), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5), BiomeFilter.biome()));
	@RegistryName("medium_underground_oil_lake")
	public static final PlacedFeature MEDIUM_UNDERGROUND_OIL_LAKES = register(WorldFeaturesZT.MEDIUM_OIL_LAKE, List.of(RarityFilter.onAverageOnceEvery(20), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(12))), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5), BiomeFilter.biome()));
	@RegistryName("large_underground_oil_lake")
	public static final PlacedFeature LARGE_UNDERGROUND_OIL_LAKES = register(WorldFeaturesZT.LARGE_OIL_LAKE, List.of(RarityFilter.onAverageOnceEvery(40), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.aboveBottom(40))), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5), BiomeFilter.biome()));
	
	@Setup // Causes this class to initialize.
	public static void setup()
	{
		ZeithTech.LOG.info("Registered placed features into Minecraft.");
	}
	
	private static PlacedFeature register(Holder<ConfiguredFeature<?, ?>> configured, List<PlacementModifier> modifiers)
	{
		return new PlacedFeature(configured, modifiers);
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