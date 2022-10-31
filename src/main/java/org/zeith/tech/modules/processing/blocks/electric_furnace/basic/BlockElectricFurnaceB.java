package org.zeith.tech.modules.processing.blocks.electric_furnace.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.voxels.VoxelShapeCache;
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
	
	public final VoxelShapeCache shapeCache = new VoxelShapeCache(this, (state, $) ->
			Shapes.join(
					box(0, 0, 0, 16, 16, 16),
					$.box(state.getValue(BlockStateProperties.HORIZONTAL_FACING), 2, 2, 0, 14, 10, 8),
					BooleanOp.ONLY_FIRST
			)
	);
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return shapeCache.get(state);
	}
	
	@Override
	public boolean useShapeForLightOcclusion(BlockState p_60576_)
	{
		return true;
	}
}