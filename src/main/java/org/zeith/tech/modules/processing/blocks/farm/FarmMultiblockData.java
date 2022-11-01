package org.zeith.tech.modules.processing.blocks.farm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.zeith.tech.api.block.multiblock.base.MultiBlockFormer;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;
import org.zeith.tech.modules.shared.init.BlocksZT;

import java.util.List;

public class FarmMultiblockData
{
	public int itemPorts, fluidPorts, energyPorts;
	
	public FarmMultiblockData(Level level, BlockPos origin, List<MultiBlockFormer.VisibleBlockPos> posList)
	{
		for(MultiBlockFormer.VisibleBlockPos pos : posList)
		{
			var state = TileMultiBlockPart.getPartState(level, pos);
			if(state.is(BlocksZT.FARM_FLUID_PORT)) ++fluidPorts;
			if(state.is(BlocksZT.FARM_ITEM_PORT)) ++itemPorts;
			if(state.is(BlocksZT.FARM_ENERGY_PORT)) ++energyPorts;
		}
	}
	
	public boolean isValid()
	{
		return fluidPorts > 0 && energyPorts > 0;
	}
}