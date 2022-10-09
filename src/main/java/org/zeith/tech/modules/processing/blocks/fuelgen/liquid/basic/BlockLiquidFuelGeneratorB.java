package org.zeith.tech.modules.processing.blocks.fuelgen.liquid.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.voxels.VoxelShapeCache;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;

public class BlockLiquidFuelGeneratorB
		extends BlockBaseMachine<TileLiquidFuelGeneratorB>
{
	public BlockLiquidFuelGeneratorB()
	{
		super(TileLiquidFuelGeneratorB.class);
	}
	
	public final VoxelShapeCache shapeCache = new VoxelShapeCache(this, (state, $) ->
	{
		var var = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		
		var mainBox = $.box(var, 0, 0, 0, 16, 16, 16);
		var hollow = $.box(var, 0, 3, 0, 5.5, 13, 5.5);
		var tube = $.box(var, 0.5, 3, 0.5, 4.5, 13, 4.5);
		
		var frontBox = $.box(var, 6, 5, -0.25, 14, 11, 0.75);
		
		return Shapes.or(Shapes.join(mainBox, hollow, BooleanOp.ONLY_FIRST), tube, frontBox);
	});
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
	{
		return shapeCache.get(state);
	}
	
	@Override
	public @Nullable TileLiquidFuelGeneratorB newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileLiquidFuelGeneratorB(pos, state);
	}
}