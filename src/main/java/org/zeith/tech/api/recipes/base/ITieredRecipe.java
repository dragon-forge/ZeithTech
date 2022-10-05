package org.zeith.tech.api.recipes.base;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.tile.ITieredTile;

public interface ITieredRecipe
{
	TechTier getMinTier();
	
	default boolean isTierGoodEnough(TechTier tier)
	{
		return tier.isOrHigher(getMinTier());
	}
	
	default boolean techTierMatches(BlockEntity tile)
	{
		return ITieredTile.get(tile)
				.map(getMinTier()::isOrLower)
				.orElse(false);
	}
}