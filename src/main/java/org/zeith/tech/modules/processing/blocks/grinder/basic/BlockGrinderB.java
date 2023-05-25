package org.zeith.tech.modules.processing.blocks.grinder.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.BlockUnaryRecipeMachineB;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;

public class BlockGrinderB
		extends BlockUnaryRecipeMachineB<TileGrinderB>
{
	public BlockGrinderB()
	{
		super(TileGrinderB.class);
	}
	
	@Override
	public @Nullable TileGrinderB newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileGrinderB(TilesZT_Processing.BASIC_GRINDER, pos, state);
	}
}