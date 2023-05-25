package org.zeith.tech.modules.processing.blocks.mining_quarry.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;

public class BlockMiningQuarryB
		extends BlockBaseMachine<TileMiningQuarryB>
{
	public BlockMiningQuarryB()
	{
		super(TileMiningQuarryB.class);
	}
	
	@Override
	public @Nullable TileMiningQuarryB newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileMiningQuarryB(TilesZT_Processing.BASIC_QUARRY, pos, state);
	}
}
