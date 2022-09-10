package org.zeith.tech.common.blocks.fuelgen.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.common.blocks.base.BlockBaseMachine;

public class BlockFuelGeneratorB
		extends BlockBaseMachine<TileFuelGeneratorB>
{
	public BlockFuelGeneratorB()
	{
		super(TileFuelGeneratorB.class);
	}
	
	@Override
	public @Nullable TileFuelGeneratorB newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileFuelGeneratorB(pos, state);
	}
}