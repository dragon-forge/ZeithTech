package org.zeith.tech.modules.processing.blocks.facade_slicer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.voxels.VoxelShapeCache;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;

public class BlockFacadeSlicer
		extends BlockBaseMachine<TileFacadeSlicer>
{
	public BlockFacadeSlicer()
	{
		super(TileFacadeSlicer.class);
	}
	
	private final VoxelShapeCache shapeCache = new VoxelShapeCache(this, (state, $) ->
	{
		var front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		return Shapes.or(
				$.box(front, 0.5F, 1, 0.5F, 15.5F, 8, 15.5F),
				$.box(front, 6, 0, 6, 10, 1, 10),
				$.box(front, 1, 8, 7.99F, 15, 15, 8.01F),
				$.box(front, 0, 0, 0, 2, 9, 2),
				$.box(front, 14, 0, 0, 16, 9, 2),
				$.box(front, 0, 0, 14, 2, 9, 16),
				$.box(front, 14, 0, 14, 16, 9, 16)
		);
	});
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return shapeCache.get(state);
	}
	
	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileFacadeSlicer(pos, state);
	}
}