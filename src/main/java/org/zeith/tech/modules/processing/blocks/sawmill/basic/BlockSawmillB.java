package org.zeith.tech.modules.processing.blocks.sawmill.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.BlockUnaryRecipeMachineB;

public class BlockSawmillB
		extends BlockUnaryRecipeMachineB<TileSawmillB>
{
	public BlockSawmillB()
	{
		super(TileSawmillB.class);
	}
	
	@Override
	public @Nullable TileSawmillB newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileSawmillB(pos, state);
	}
}