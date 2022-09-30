package org.zeith.tech.common.blocks.item_pipe;

import net.minecraft.core.*;
import net.minecraft.world.Containers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.tile.BlockEntityTypeModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BlockItemPipe
		extends BaseEntityBlock
		implements ICreativeTabBlock, IRegisterListener
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
	
	
	public static final VoxelShape CORE_CENTRAL_SHAPE = box(5.5, 5.5, 5.5, 10.5, 10.5, 10.5);
	public static final VoxelShape CORE_SMOL_SHAPE = box(6, 6, 6, 10, 10, 10);
	public static final Map<Direction, VoxelShape> DIR2SHAPE = Map.of(
			Direction.UP, box(6, 10, 6, 10, 16, 10),
			Direction.DOWN, box(6, 0, 6, 10, 6, 10),
			Direction.NORTH, box(6, 6, 0, 10, 10, 6),
			Direction.EAST, box(10, 6, 6, 16, 10, 10),
			Direction.SOUTH, box(6, 6, 10, 10, 10, 16),
			Direction.WEST, box(0, 6, 6, 6, 10, 10)
	);
	
	public static final BooleanProperty CORE_CENTRAL = BooleanProperty.create("core_central");
	public static final BooleanProperty CORE_X = BooleanProperty.create("core_x");
	public static final BooleanProperty CORE_Y = BooleanProperty.create("core_y");
	public static final BooleanProperty CORE_Z = BooleanProperty.create("core_z");
	
	public static final Map<Direction.Axis, BooleanProperty> AXIS2PROP = Map.of(
			Direction.Axis.X, CORE_X,
			Direction.Axis.Y, CORE_Y,
			Direction.Axis.Z, CORE_Z
	);
	
	protected final PipeProperties properties;
	
	private final Map<BlockState, VoxelShape> shapeCache = new HashMap<>();
	private final Function<BlockState, VoxelShape> shapeGenerator = (state) ->
	{
		var central = state.getValue(CORE_CENTRAL) ? CORE_CENTRAL_SHAPE : CORE_SMOL_SHAPE;
		
		return Shapes.or(central,
				DIR2PROP.entrySet()
						.stream()
						.filter(dir -> state.getValue(dir.getValue()))
						.map(dir -> DIR2SHAPE.get(dir.getKey()))
						.toArray(VoxelShape[]::new)
		);
	};
	
	public BlockItemPipe(PipeProperties props)
	{
		super(props.blockProps);
		this.properties = props;
		
		var bs = defaultBlockState()
				.setValue(CORE_X, false)
				.setValue(CORE_Y, false)
				.setValue(CORE_Z, false);
		for(Direction d : DIRECTIONS)
			bs = bs.setValue(DIR2PROP.get(d), false);
		registerDefaultState(bs);
	}
	
	public float getPipeSpeed()
	{
		return properties.getPipeSpeed();
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		DIR2PROP.values().forEach(builder::add);
		builder.add(CORE_CENTRAL, CORE_X, CORE_Y, CORE_Z);
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
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return shapeCache.computeIfAbsent(state, shapeGenerator);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction ignore0, BlockState ignore1, LevelAccessor accessor, BlockPos pos, BlockPos ignore2)
	{
		var pipe = Cast.cast(accessor.getBlockEntity(pos), TileItemPipe.class);
		if(pipe != null)
		{
			Direction prev = null, cur = null;
			
			int connected = 0;
			
			for(Direction d : DIRECTIONS)
			{
				var c = pipe.doesConnectTo(d);
				state = state.setValue(DIR2PROP.get(d), c);
				if(c)
				{
					++connected;
					prev = cur;
					cur = d;
				}
			}
			
			if(connected == 2 && prev != null && cur != null && cur.getAxis() == prev.getAxis())
			{
				state = state.setValue(CORE_CENTRAL, false);
				for(Direction.Axis axis : AXIS2PROP.keySet())
					state = state.setValue(AXIS2PROP.get(axis), axis == cur.getAxis());
			} else
				state = state.setValue(CORE_CENTRAL, true).setValue(CORE_X, false).setValue(CORE_Y, false).setValue(CORE_Z, false);
		}
		return state;
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var accessor = ctx.getLevel();
		var pos = ctx.getClickedPos();
		
		var state = defaultBlockState();
		
		var pipe = Cast.cast(accessor.getBlockEntity(pos), TileItemPipe.class);
		if(pipe == null)
		{
			pipe = new TileItemPipe(pos, state);
			pipe.setLevel(accessor);
		}
		
		Direction prev = null, cur = null;
		
		int connected = 0;
		
		for(Direction d : DIRECTIONS)
		{
			var c = pipe.doesConnectTo(d);
			state = state.setValue(DIR2PROP.get(d), c);
			if(c)
			{
				++connected;
				prev = cur;
				cur = d;
			}
		}
		
		if(connected == 2 && prev != null && cur != null && cur.getAxis() == prev.getAxis())
		{
			state = state.setValue(CORE_CENTRAL, false);
			for(Direction.Axis axis : AXIS2PROP.keySet())
				state = state.setValue(AXIS2PROP.get(axis), axis == cur.getAxis());
		} else
			state = state.setValue(CORE_CENTRAL, true).setValue(CORE_X, false).setValue(CORE_Y, false).setValue(CORE_Z, false);
		
		return state;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileItemPipe(pos, state);
	}
	
	@Override
	public void onPostRegistered()
	{
		BlockEntityTypeModifier.addBlocksToEntityType(TileItemPipe.ITEM_PIPE, this);
	}
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return ZeithTech.TAB;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return BlockAPI.ticker();
	}
	
	@Override
	public void onRemove(BlockState prevState, Level world, BlockPos pos, BlockState newState, boolean flag64)
	{
		if(!prevState.is(newState.getBlock()))
		{
			BlockEntity b = world.getBlockEntity(pos);
			if(b instanceof TileItemPipe pipe)
			{
				NonNullList<ItemStack> drops = NonNullList.create();
				for(ItemInPipe item : pipe.contents.getAll())
				{
					drops.add(item.getContents());
					item.setContents(ItemStack.EMPTY);
				}
				Containers.dropContents(world, pos, drops);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(prevState, world, pos, newState, flag64);
		}
	}
}