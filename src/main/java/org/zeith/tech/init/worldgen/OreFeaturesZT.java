package org.zeith.tech.init.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.init.blocks.OresZT;

import java.util.List;

import static net.minecraft.data.worldgen.features.OreFeatures.*;

public class OreFeaturesZT
{
	private static final List<OreConfiguration.TargetBlockState> ORE_TIN_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, OresZT.TIN_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, OresZT.DEEPSLATE_TIN_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_LEAD_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, OresZT.LEAD_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, OresZT.DEEPSLATE_LEAD_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_ALUMINUM_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, OresZT.ALUMINUM_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, OresZT.DEEPSLATE_ALUMINUM_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_LITHIUM_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, OresZT.LITHIUM_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, OresZT.DEEPSLATE_LITHIUM_ORE.defaultBlockState())
	);
	
	private static final List<OreConfiguration.TargetBlockState> ORE_URANIUM_TARGET_LIST = List.of(
			OreConfiguration.target(STONE_ORE_REPLACEABLES, OresZT.URANIUM_ORE.defaultBlockState()),
			OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, OresZT.DEEPSLATE_URANIUM_ORE.defaultBlockState())
	);
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> TIN_ORE = register("tin_ore", Feature.ORE, new OreConfiguration(ORE_TIN_TARGET_LIST, 9));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> TIN_ORE_SMALL = register("small_tin_ore", Feature.ORE, new OreConfiguration(ORE_TIN_TARGET_LIST, 4));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> LEAD_ORE = register("lead_ore", Feature.ORE, new OreConfiguration(ORE_LEAD_TARGET_LIST, 9));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> LEAD_ORE_SMALL = register("small_lead_ore", Feature.ORE, new OreConfiguration(ORE_LEAD_TARGET_LIST, 4));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ALUMINUM_ORE = register("aluminum_ore", Feature.ORE, new OreConfiguration(ORE_ALUMINUM_TARGET_LIST, 9));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ALUMINUM_ORE_SMALL = register("small_aluminum_ore", Feature.ORE, new OreConfiguration(ORE_ALUMINUM_TARGET_LIST, 4));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> LITHIUM_ORE = register("lithium_ore", Feature.ORE, new OreConfiguration(ORE_LITHIUM_TARGET_LIST, 9));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> LITHIUM_ORE_SMALL = register("small_lithium_ore", Feature.ORE, new OreConfiguration(ORE_LITHIUM_TARGET_LIST, 4));
	
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> BIG_URANIUM_ORE = register("big_uranium_ore", Feature.ORE, new OreConfiguration(ORE_URANIUM_TARGET_LIST, 12));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> URANIUM_ORE = register("uranium_ore", Feature.ORE, new OreConfiguration(ORE_URANIUM_TARGET_LIST, 6));
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> URANIUM_ORE_SMALL = register("small_uranium_ore", Feature.ORE, new OreConfiguration(ORE_URANIUM_TARGET_LIST, 2));
	
	
	private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> register(String p_206489_, F p_206490_, FC p_206491_)
	{
		return FeatureUtils.register(ZeithTech.MOD_ID + ":" + p_206489_, p_206490_, p_206491_);
	}
}