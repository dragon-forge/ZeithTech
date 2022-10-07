package org.zeith.tech.modules.transport.blocks.fluid_pipe;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.core.adapter.BlockEntityAdapter;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.block.IMultiFluidLoggableBlock;
import org.zeith.tech.api.block.ZeithTechStateProperties;
import org.zeith.tech.api.voxels.VoxelShapeCache;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.blocks.BaseEntityBlockZT;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;

import java.util.List;
import java.util.Map;

public class BlockFluidPipe
		extends BaseEntityBlockZT
		implements ICreativeTabBlock, IRegisterListener, IMultiFluidLoggableBlock
{
	static final Direction[] DIRECTIONS = Direction.values();
	public static final Map<Direction, BooleanProperty> DIR2PROP = Map.of(
			Direction.UP, BlockStateProperties.UP,
			Direction.DOWN, BlockStateProperties.DOWN,
			Direction.NORTH, BlockStateProperties.NORTH,
			Direction.EAST, BlockStateProperties.EAST,
			Direction.SOUTH, BlockStateProperties.SOUTH,
			Direction.WEST, BlockStateProperties.WEST
	);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public static final IntegerProperty LIGHT_LEVEL = ZeithTechStateProperties.LIGHT_LEVEL;
	
	public final FluidPipeProperties properties;
	
	public BlockFluidPipe(FluidPipeProperties properties, BlockHarvestAdapter.MineableType toolType)
	{
		super(properties.properties().lightLevel(s -> s.getValue(LIGHT_LEVEL)));
		this.properties = properties;
		
		addBlockTag(toolType.blockTag());
		
		shapeCache = new VoxelShapeCache(this, (state, $) -> Shapes.or(CORE_SHAPE,
				DIR2PROP.entrySet()
						.stream()
						.filter(dir -> state.getValue(dir.getValue()))
						.map(dir -> DIR2SHAPE.get(dir.getKey()))
						.toArray(VoxelShape[]::new)
		));
		
		dropsSelf();
	}
	
	public static final Map<FlowingFluid, BooleanProperty> MINING_PIPE_FLUID_STATES = Map.of(
			Fluids.WATER, WATERLOGGED
	);
	
	@Override
	public Map<FlowingFluid, BooleanProperty> getFluidLoggableProperties()
	{
		return MINING_PIPE_FLUID_STATES;
	}
	
	@Override
	protected BlockItem newBlockItem(Item.Properties props)
	{
		return new BlockItem(this, props)
		{
			@Override
			public void appendHoverText(ItemStack stack, @Nullable Level lvl, List<Component> tooltip, TooltipFlag flags)
			{
				tooltip.add(Component.translatable("info." + ZeithTech.MOD_ID + "_transport.fluid_pipe_transfer", Component.literal(Integer.toString(properties.transferVolume()))).withStyle(ChatFormatting.GRAY));
			}
		};
	}
	
	public static final VoxelShape CORE_SHAPE = box(5, 5, 5, 11, 11, 11);
	
	public static final Map<Direction, VoxelShape> DIR2SHAPE = Map.of(
			Direction.UP, box(5, 11, 5, 11, 16, 11),
			Direction.DOWN, box(5, 0, 5, 11, 5, 11),
			Direction.NORTH, box(5, 5, 0, 11, 11, 5),
			Direction.EAST, box(11, 5, 5, 16, 11, 11),
			Direction.SOUTH, box(5, 5, 11, 11, 11, 16),
			Direction.WEST, box(0, 5, 5, 5, 11, 11)
	);
	
	private final VoxelShapeCache shapeCache;
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return shapeCache.get(state);
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return getFluidLoggedState(state);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		getFluidLoggableProperties().values().forEach(builder::add);
		DIR2PROP.values().forEach(builder::add);
		builder.add(LIGHT_LEVEL);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction ignore0, BlockState ignore1, LevelAccessor accessor, BlockPos pos, BlockPos ignore2)
	{
		state = updateFluidLoggedShape(accessor, pos, state);
		
		var pipe = Cast.cast(accessor.getBlockEntity(pos), TileFluidPipe.class);
		if(pipe != null)
			for(Direction d : DIRECTIONS)
				state = state.setValue(DIR2PROP.get(d), pipe.doesConnectTo(d));
		
		return state;
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var accessor = ctx.getLevel();
		var pos = ctx.getClickedPos();
		
		var state = getFluidLoggedStateForPlacement(ctx, defaultBlockState());
		
		var pipe = Cast.cast(accessor.getBlockEntity(pos), TileFluidPipe.class);
		if(pipe == null)
		{
			pipe = new TileFluidPipe(pos, state);
			pipe.setLevel(accessor);
		}
		
		for(Direction d : DIRECTIONS)
			state = state.setValue(DIR2PROP.get(d), pipe.doesConnectTo(d));
		
		return state;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState p_49232_)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	public boolean isCollisionShapeFullBlock(BlockState p_181242_, BlockGetter p_181243_, BlockPos p_181244_)
	{
		return false;
	}
	
	@Override
	public boolean isOcclusionShapeFullBlock(BlockState p_222959_, BlockGetter p_222960_, BlockPos p_222961_)
	{
		return false;
	}
	
	@Override
	public void onPostRegistered()
	{
		BlockEntityAdapter.addBlocksToEntityType(TilesZT_Transport.FLUID_PIPE, this);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileFluidPipe(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return BlockAPI.ticker();
	}
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return ZeithTech.TAB;
	}
}