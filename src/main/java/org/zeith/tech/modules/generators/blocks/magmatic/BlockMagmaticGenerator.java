package org.zeith.tech.modules.generators.blocks.magmatic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.voxels.VoxelShapeCache;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;

public class BlockMagmaticGenerator
		extends BlockBaseMachine<TileMagmaticGenerator>
{
	public static final VoxelShape SHAPE = Shapes.or(
			box(0, 0, 0, 16, 11, 16),
			box(1, 4, 1, 15, 16, 8),
			box(8, 4, 1, 15, 16, 15)
	);
	
	public BlockMagmaticGenerator()
	{
		super(TileMagmaticGenerator.class);
	}
	
	private final VoxelShapeCache cache = new VoxelShapeCache(this, (state, $) ->
	{
		var rotation = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		return Shapes.or(
				Shapes.join(box(0, 0, 0, 16, 11, 16),
						box(1, 4, 1, 15, 11, 15),
						BooleanOp.ONLY_FIRST),
				$.box(rotation, 1, 4, 1, 15, 16, 8),
				$.box(rotation, 8, 4, 1, 15, 16, 15),
				$.box(rotation, 2, 3, 9, 7, 16, 14)
		);
	});
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return cache.get(state);
	}
	
	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileMagmaticGenerator(pos, state);
	}
}
