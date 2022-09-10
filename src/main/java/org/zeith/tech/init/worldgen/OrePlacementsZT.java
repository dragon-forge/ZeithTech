package org.zeith.tech.init.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight;
import net.minecraft.world.level.levelgen.placement.*;
import org.zeith.hammerlib.annotations.Setup;
import org.zeith.tech.ZeithTech;

import java.util.List;

public class OrePlacementsZT
{
	public static final Holder<PlacedFeature> ORE_TIN_UPPER = register("ore_tin_upper", OreFeaturesZT.TIN_ORE, commonOrePlacement(25, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	public static final Holder<PlacedFeature> ORE_TIN_MIDDLE = register("ore_tin_middle", OreFeaturesZT.TIN_ORE, commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_TIN_SMALL = register("ore_tin_small", OreFeaturesZT.TIN_ORE_SMALL, commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_LEAD_UPPER = register("ore_lead_upper", OreFeaturesZT.LEAD_ORE, commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	public static final Holder<PlacedFeature> ORE_LEAD_MIDDLE = register("ore_lead_middle", OreFeaturesZT.LEAD_ORE, commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_LEAD_SMALL = register("ore_lead_small", OreFeaturesZT.LEAD_ORE_SMALL, commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_ALUMINUM_UPPER = register("ore_aluminum_upper", OreFeaturesZT.ALUMINUM_ORE, commonOrePlacement(25, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	public static final Holder<PlacedFeature> ORE_ALUMINUM_MIDDLE = register("ore_aluminum_middle", OreFeaturesZT.ALUMINUM_ORE, commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_ALUMINUM_SMALL = register("ore_aluminum_small", OreFeaturesZT.ALUMINUM_ORE_SMALL, commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_LITHIUM_UPPER = register("ore_lithium_upper", OreFeaturesZT.LITHIUM_ORE, commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
	public static final Holder<PlacedFeature> ORE_LITHIUM_MIDDLE = register("ore_lithium_middle", OreFeaturesZT.LITHIUM_ORE, commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
	public static final Holder<PlacedFeature> ORE_LITHIUM_SMALL = register("ore_lithium_small", OreFeaturesZT.LITHIUM_ORE_SMALL, commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
	
	public static final Holder<PlacedFeature> ORE_URANIUM_UPPER = register("ore_uranium_upper", OreFeaturesZT.URANIUM_ORE_SMALL, commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(128))));
	public static final Holder<PlacedFeature> ORE_URANIUM_MIDDLE = register("ore_uranium_middle", OreFeaturesZT.URANIUM_ORE, commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(72))));
	public static final Holder<PlacedFeature> ORE_URANIUM_DEEP = register("ore_uranium_deep", OreFeaturesZT.BIG_URANIUM_ORE, commonOrePlacement(8, HeightRangePlacement.of(BiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(8), 1))));
	
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