package org.zeith.tech.modules.transport.blocks.item_pipe;

import net.minecraft.core.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.core.adapter.BlockEntityAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.block.IPipeCuttable;
import org.zeith.tech.api.enums.SideConfig;
import org.zeith.tech.api.tile.facade.FacadeData;
import org.zeith.tech.api.voxels.VoxelShapeCache;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.blocks.BaseEntityBlockZT;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;

import java.util.Map;

public class BlockItemPipe
		extends BaseEntityBlockZT
		implements ICreativeTabBlock, IRegisterListener, SimpleWaterloggedBlock, IPipeCuttable
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
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public static final Map<Direction.Axis, BooleanProperty> AXIS2PROP = Map.of(
			Direction.Axis.X, CORE_X,
			Direction.Axis.Y, CORE_Y,
			Direction.Axis.Z, CORE_Z
	);
	
	protected final ItemPipeProperties properties;
	
	public BlockItemPipe(ItemPipeProperties props)
	{
		super(props.properties());
		this.properties = props;
		
		addBlockTag(BlockTags.MINEABLE_WITH_PICKAXE);
		
		var bs = defaultBlockState()
				.setValue(WATERLOGGED, false)
				.setValue(CORE_X, false)
				.setValue(CORE_Y, false)
				.setValue(CORE_Z, false);
		
		for(Direction d : DIRECTIONS)
			bs = bs.setValue(DIR2PROP.get(d), false);
		
		registerDefaultState(bs);
		dropsSelf();
	}
	
	public float getPipeSpeed()
	{
		return properties.pipeSpeed();
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		DIR2PROP.values().forEach(builder::add);
		builder.add(CORE_CENTRAL, CORE_X, CORE_Y, CORE_Z, WATERLOGGED);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state)
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
	
	private final VoxelShapeCache shapeCache = new VoxelShapeCache(this, (state, $) ->
	{
		var central = state.getValue(CORE_CENTRAL) ? CORE_CENTRAL_SHAPE : CORE_SMOL_SHAPE;
		
		return Shapes.or(central,
				DIR2PROP.entrySet()
						.stream()
						.filter(dir -> state.getValue(dir.getValue()))
						.map(dir -> DIR2SHAPE.get(dir.getKey()))
						.toArray(VoxelShape[]::new)
		);
	});
	
	public VoxelShape getShapeWithNoFacades(BlockState state)
	{
		return shapeCache.get(state);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext p_60558_)
	{
		if(level.getBlockEntity(pos) instanceof TileItemPipe pipe)
			return pipe.facades.orShapes(getShapeWithNoFacades(state));
		return getShapeWithNoFacades(state);
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		if(level.getBlockEntity(pos) instanceof TileItemPipe pipe)
			return pipe.facades.pickFacade(pos, target.getLocation()).orElseGet(() -> super.getCloneItemStack(state, target, level, pos, player));
		return super.getCloneItemStack(state, target, level, pos, player);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction ignore0, BlockState ignore1, LevelAccessor accessor, BlockPos pos, BlockPos ignore2)
	{
		if(state.getValue(WATERLOGGED))
			accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
		
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
		
		var state = defaultBlockState()
				.setValue(WATERLOGGED, accessor.getFluidState(pos).getType() == Fluids.WATER);
		
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
		BlockEntityAdapter.addBlocksToEntityType(TilesZT_Transport.ITEM_PIPE, this);
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
				for(FacadeData.FacadeFace facadeFace : pipe.facades.getFaces().values())
					drops.add(facadeFace.facadeItem());
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
	
	@Override
	public VoxelShape getConnectionBoundary(BlockState state, Direction to)
	{
		return DIR2SHAPE.get(to);
	}
	
	@Override
	public boolean performCut(BlockState state, UseOnContext context, Direction cutPart)
	{
		if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof TileItemPipe pipe)
		{
			var idx = cutPart.ordinal();
			var cfg = pipe.sideConfigs.get(idx);
			pipe.sideConfigs.set(idx, cfg == SideConfig.DISABLE ? SideConfig.NONE : SideConfig.DISABLE);
			
			if(context.getLevel().getBlockEntity(context.getClickedPos().relative(cutPart)) instanceof TileItemPipe pipe2)
				pipe2.sideConfigs.set(cutPart.getOpposite().ordinal(), pipe.sideConfigs.get(idx));
			
			return true;
		}
		return true;
	}
}