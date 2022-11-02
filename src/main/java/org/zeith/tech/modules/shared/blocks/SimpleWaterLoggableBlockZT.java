package org.zeith.tech.modules.shared.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.api.block.IMultiFluidLoggableBlock;

import java.util.Map;

public class SimpleWaterLoggableBlockZT
		extends SimpleBlockZT
		implements IMultiFluidLoggableBlock
{
	public static final Map<FlowingFluid, BooleanProperty> FLUID_STATES = Map.of(
			Fluids.WATER, BlockStateProperties.WATERLOGGED
	);
	
	public SimpleWaterLoggableBlockZT(Properties props, BlockHarvestAdapter.MineableType toolType, Tier miningTier)
	{
		super(props, toolType, miningTier);
		registerDefaultState(getStateWithNoFluids(defaultBlockState()));
	}
	
	public SimpleWaterLoggableBlockZT(Properties props, BlockHarvestAdapter.MineableType toolType)
	{
		super(props, toolType);
		registerDefaultState(getStateWithNoFluids(defaultBlockState()));
	}
	
	public SimpleWaterLoggableBlockZT(Properties props)
	{
		super(props);
		registerDefaultState(getStateWithNoFluids(defaultBlockState()));
	}
	
	@Override
	public Map<FlowingFluid, BooleanProperty> getFluidLoggableProperties()
	{
		return FLUID_STATES;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		getFluidLoggableProperties().values().forEach(builder::add);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction ignore0, BlockState ignore1, LevelAccessor accessor, BlockPos pos, BlockPos ignore2)
	{
		return updateFluidLoggedShape(accessor, pos, state);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return getFluidLoggedStateForPlacement(ctx, defaultBlockState());
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return getFluidLoggedState(state);
	}
}
