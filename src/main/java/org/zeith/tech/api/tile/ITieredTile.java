package org.zeith.tech.api.tile;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.enums.TechTier;

import java.util.Optional;

public interface ITieredTile
{
	TechTier getTechTier();
	
	static Optional<TechTier> get(BlockEntity tile)
	{
		return Cast.optionally(tile, ITieredTile.class).map(ITieredTile::getTechTier);
	}
}