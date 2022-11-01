package org.zeith.tech.api.block;

import net.minecraft.world.level.block.state.BlockState;

public interface IFarmlandBlock
{
	int getMaxMoistLevel();
	
	int getMoistLevel(BlockState state);
	
	BlockState withMoistLevel(BlockState state, int moistLevel);
}