package org.zeith.tech.modules.processing.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.api.block.IMultiFluidLoggableBlock;
import org.zeith.tech.api.block.ZeithTechStateProperties;
import org.zeith.tech.modules.shared.blocks.SimpleBlockZT;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.world.init.FluidsZT_World;

import java.util.List;
import java.util.Map;

public class BlockMiningPipe
		extends SimpleBlockZT
		implements IMultiFluidLoggableBlock
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty OILLOGGED = ZeithTechStateProperties.CRUDE_OIL_LOGGED;
	public static final BooleanProperty LAVALOGGED = ZeithTechStateProperties.LAVA_LOGGED;
	
	public static final VoxelShape PIPE_SHAPE =
			Shapes.join(box(5, 0, 5, 11, 16, 11),
					box(6, 0, 6, 10, 16, 10),
					BooleanOp.ONLY_FIRST);
	
	public BlockMiningPipe(Properties props)
	{
		super(props);
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, Tiers.STONE, this);
		addItemTag(TagsZT.Items.MINING_PIPE);
		addBlockTag(TagsZT.Blocks.MINING_PIPE);
		dropsSelf();
		
		registerDefaultState(getStateWithNoFluids(defaultBlockState()));
	}
	
	public static final Map<FlowingFluid, BooleanProperty> MINING_PIPE_FLUID_STATES = Map.of(
			FluidsZT_World.CRUDE_OIL.getSource(), OILLOGGED,
			Fluids.WATER, WATERLOGGED,
			Fluids.LAVA, LAVALOGGED
	);
	
	@Override
	public Map<FlowingFluid, BooleanProperty> getFluidLoggableProperties()
	{
		return MINING_PIPE_FLUID_STATES;
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
	
	final RandomSource rand = RandomSource.create();
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		if(rand.nextInt(10) > 0)
			return List.of(new ItemStack(this));
		else
			return super.getDrops(state, builder);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter lvl, BlockPos pos, CollisionContext ctx)
	{
		return PIPE_SHAPE;
	}
}