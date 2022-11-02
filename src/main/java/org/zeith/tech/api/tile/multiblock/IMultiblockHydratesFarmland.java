package org.zeith.tech.api.tile.multiblock;

import net.minecraft.core.BlockPos;

public interface IMultiblockHydratesFarmland
		extends IMultiblockTile
{
	boolean doesHydrate(BlockPos pos);
}