package org.zeith.tech.modules.transport.blocks.energy_cell.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;

public class BlockEnergyCellB
		extends BlockBaseMachine<TileEnergyCellB>
{
	public BlockEnergyCellB()
	{
		super(TileEnergyCellB.class);
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		if(level.getBlockEntity(pos) instanceof TileEnergyCellB cell)
			return Math.round(cell.energy.getFillRate() * 15F);
		return 0;
	}
	
	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileEnergyCellB(pos, state);
	}
}
