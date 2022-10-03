package org.zeith.tech.modules.processing.blocks.electric_furnace.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;

public class BlockElectricFurnaceB
		extends BlockBaseMachine<TileElectricFurnaceB>
{
	public BlockElectricFurnaceB()
	{
		super(TileElectricFurnaceB.class);
	}
	
	@Override
	public @Nullable TileElectricFurnaceB newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileElectricFurnaceB(pos, state);
	}
}