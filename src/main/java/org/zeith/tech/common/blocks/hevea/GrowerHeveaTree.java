package org.zeith.tech.common.blocks.hevea;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.zeith.tech.init.worldgen.WorldFeaturesZT;

public class GrowerHeveaTree
		extends AbstractTreeGrower
{
	@Override
	protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource rng, boolean bees)
	{
		return bees ? WorldFeaturesZT.HEVEA_TREE_WITH_BEES : WorldFeaturesZT.HEVEA_TREE;
	}
}