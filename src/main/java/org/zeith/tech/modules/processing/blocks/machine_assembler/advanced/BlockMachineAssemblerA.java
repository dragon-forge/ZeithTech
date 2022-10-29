package org.zeith.tech.modules.processing.blocks.machine_assembler.advanced;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;

public class BlockMachineAssemblerA
		extends BlockBaseMachine<TileMachineAssemblerA>
{
	private static final VoxelShape BASIC_ASSEMBLER_SHAPE = Shapes.or(
			box(0, 0, 0, 16, 3, 16),
			box(1, 3, 1, 15, 5, 15),
			box(3, 5, 3, 13, 7, 13),
			box(2, 7, 2, 14, 9, 14),
			box(3, 9, 3, 13, 11, 13),
			box(1, 11, 1, 15, 13, 15),
			box(-1, 13, -1, 17, 15, 17)
	);
	
	public BlockMachineAssemblerA()
	{
		super(TileMachineAssemblerA.class);
	}
	
	@Override
	public @Nullable TileMachineAssemblerA newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileMachineAssemblerA(pos, state);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx)
	{
		return Shapes.or(
				box(0, 0, 0, 16, 3, 16),
				box(1, 3, 1, 15, 5, 15),
				box(3, 5, 3, 13, 7, 13),
				box(2, 7, 2, 14, 9, 14),
				box(3, 9, 3, 13, 11, 13),
				box(1, 11, 1, 15, 13, 15),
				box(-1, 13, -1, 17, 15, 17)
		);
	}
}