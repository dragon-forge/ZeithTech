package org.zeith.tech.common.blocks.machine_assembler.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.common.blocks.base.machine.BlockBaseMachine;

public class BlockMachineAssemblerB
		extends BlockBaseMachine<TileMachineAssemblerB>
{
	private static final VoxelShape BASIC_ASSEMBLER_SHAPE = Shapes.or(
			Block.box(0, 0, 0, 16, 5, 16),
			Block.box(5, 6, 5, 11, 10, 11),
			Block.box(2, 5, 2, 14, 6, 14),
			Block.box(3, 10, 3, 13, 13, 13),
			Block.box(0, 13, 0, 16, 14, 16),
			Block.box(-1, 14, -1, 17, 14.5, 17)
	);
	
	public BlockMachineAssemblerB()
	{
		super(TileMachineAssemblerB.class);
	}
	
	@Override
	public @Nullable TileMachineAssemblerB newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileMachineAssemblerB(pos, state);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx)
	{
		return BASIC_ASSEMBLER_SHAPE;
	}
}