package org.zeith.tech.modules.processing.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.api.block.ZeithTechStateProperties;
import org.zeith.tech.modules.shared.blocks.SimpleBlockZT;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.world.init.FluidTypesZT_World;

import java.util.List;

public class BlockMiningPipe
		extends SimpleBlockZT
		implements SimpleWaterloggedBlock
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty OILLOGGED = ZeithTechStateProperties.CRUDE_OIL_LOGGED;
	
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
		
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(OILLOGGED, false));
	}
	
	@Override
	public boolean canPlaceLiquid(BlockGetter getter, BlockPos pos, BlockState state, Fluid fluid)
	{
		return fluid == Fluids.WATER || FluidTypesZT_World.CRUDE_OIL.is(fluid);
	}
	
	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid)
	{
		if(!state.getValue(BlockStateProperties.WATERLOGGED) && fluid.getType() == Fluids.WATER)
		{
			if(!level.isClientSide())
			{
				level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
				level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
			}
			
			return true;
		} else if(!state.getValue(OILLOGGED) && FluidTypesZT_World.CRUDE_OIL.is(fluid.getType()))
		{
			if(!level.isClientSide())
			{
				level.setBlock(pos, state.setValue(OILLOGGED, true), 3);
				level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
			}
			
			return true;
		} else
		{
			return false;
		}
	}
	
	@Override
	public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state)
	{
		if(state.getValue(BlockStateProperties.WATERLOGGED))
		{
			level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
			
			if(!state.canSurvive(level, pos))
				level.destroyBlock(pos, true);
			
			return new ItemStack(Items.WATER_BUCKET);
		} else if(state.getValue(OILLOGGED))
		{
			level.setBlock(pos, state.setValue(OILLOGGED, false), 3);
			
			if(!state.canSurvive(level, pos))
				level.destroyBlock(pos, true);
			
			return new ItemStack(FluidTypesZT_World.CRUDE_OIL.getBucket());
		} else
		{
			return ItemStack.EMPTY;
		}
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(WATERLOGGED, OILLOGGED);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction ignore0, BlockState ignore1, LevelAccessor accessor, BlockPos pos, BlockPos ignore2)
	{
		if(state.getValue(OILLOGGED))
			accessor.scheduleTick(pos, FluidTypesZT_World.CRUDE_OIL.getSource(), FluidTypesZT_World.CRUDE_OIL.getSource().getTickDelay(accessor));
		else if(state.getValue(WATERLOGGED))
			accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
		return state;
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var accessor = ctx.getLevel();
		var pos = ctx.getClickedPos();
		
		var state = defaultBlockState()
				.setValue(WATERLOGGED, accessor.getFluidState(pos).getType() == Fluids.WATER)
				.setValue(OILLOGGED, accessor.getFluidState(pos).getType() == FluidTypesZT_World.CRUDE_OIL.getSource());
		
		return state;
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) :
				state.getValue(OILLOGGED) ? FluidTypesZT_World.CRUDE_OIL.getSource().getSource(false) :
						super.getFluidState(state);
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