package org.zeith.tech.modules.processing.blocks.mining_quarry.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;

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
		return new TileMiningQuarryB(pos, state);
	}
}
