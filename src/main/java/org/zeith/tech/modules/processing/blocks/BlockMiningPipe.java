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
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.modules.shared.blocks.SimpleBlockZT;
import org.zeith.tech.modules.shared.init.TagsZT;

import java.util.List;

public class BlockMiningPipe
		extends SimpleBlockZT
		implements SimpleWaterloggedBlock
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
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
		
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(WATERLOGGED);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction ignore0, BlockState ignore1, LevelAccessor accessor, BlockPos pos, BlockPos ignore2)
	{
		if(state.getValue(WATERLOGGED))
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
				.setValue(WATERLOGGED, accessor.getFluidState(pos).getType() == Fluids.WATER);
		
		return state;
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
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