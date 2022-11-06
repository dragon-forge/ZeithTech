package org.zeith.tech.modules.transport.blocks.energy_wire;

import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;

import java.util.List;
import java.util.Map;

public class BlockEnergyWire
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
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public final EnergyWireProperties properties;
	
	protected final Map<Direction, VoxelShape> DIR2SHAPE;
	
	public BlockEnergyWire(EnergyWireProperties properties)
	{
		super(properties.blockProps());
		this.properties = properties;
		
		addBlockTag(TagsZT.Blocks.MINEABLE_WITH_WIRE_CUTTER);
		
		this.DIR2SHAPE = properties.insulated() ? DIR2SHAPE_INSULATED : DIR2SHAPE_UNINSULATED;
		
		shapeCache = new VoxelShapeCache(this, (state, $) -> Shapes.or(this.properties.insulated() ? CORE_SHAPE_INSULATED : CORE_SHAPE_UNINSULATED,
				DIR2PROP.entrySet()
						.stream()
						.filter(dir -> state.getValue(dir.getValue()))
						.map(dir -> DIR2SHAPE.get(dir.getKey()))
						.toArray(VoxelShape[]::new)
		));
		
		dropsSelf();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flags)
	{
		tooltip.add(Component.translatable("info." + ZeithTech.MOD_ID + "_transport.wire_max_io", Component.literal(Integer.toString(properties.tier().maxTransfer()))).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("info." + ZeithTech.MOD_ID + "_transport.wire_loss", Component.literal(Float.toString(properties.energyLoss()))).withStyle(ChatFormatting.GRAY));
	}
	
	public static final VoxelShape CORE_SHAPE_UNINSULATED = box(7, 7, 7, 9, 9, 9);
	public static final VoxelShape CORE_SHAPE_INSULATED = box(6.5, 6.5, 6.5, 9.5, 9.5, 9.5);
	public static final Map<Direction, VoxelShape> DIR2SHAPE_UNINSULATED = Map.of(
			Direction.UP, box(7, 9, 7, 9, 16, 9),
			Direction.DOWN, box(7, 0, 7, 9, 7, 9),
			Direction.NORTH, box(7, 7, 0, 9, 9, 7),
			Direction.EAST, box(9, 7, 7, 16, 9, 9),
			Direction.SOUTH, box(7, 7, 9, 9, 9, 16),
			Direction.WEST, box(0, 7, 7, 7, 9, 9)
	);
	public static final Map<Direction, VoxelShape> DIR2SHAPE_INSULATED = Map.of(
			Direction.UP, box(6.5, 9.5, 6.5, 9.5, 16, 9.5),
			Direction.DOWN, box(6.5, 0, 6.5, 9.5, 6.5, 9.5),
			Direction.NORTH, box(6.5, 6.5, 0, 9.5, 9.5, 6.5),
			Direction.EAST, box(9.5, 6.5, 6.5, 16, 9.5, 9.5),
			Direction.SOUTH, box(6.5, 6.5, 9.5, 9.5, 9.5, 16),
			Direction.WEST, box(0, 6.5, 6.5, 6.5, 9.5, 9.5)
	);
	
	private final VoxelShapeCache shapeCache;
	
	public VoxelShape getShapeWithNoFacades(BlockState state)
	{
		return shapeCache.get(state);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext p_60558_)
	{
		if(level.getBlockEntity(pos) instanceof TileEnergyWire wire)
			return wire.facades.orShapes(getShapeWithNoFacades(state));
		return getShapeWithNoFacades(state);
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		if(level.getBlockEntity(pos) instanceof TileEnergyWire wire)
			return wire.facades.pickFacade(pos, target.getLocation()).orElseGet(() -> super.getCloneItemStack(state, target, level, pos, player));
		return super.getCloneItemStack(state, target, level, pos, player);
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
		builder.add(WATERLOGGED);
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
	
	@Override
	public BlockState updateShape(BlockState state, Direction ignore0, BlockState ignore1, LevelAccessor accessor, BlockPos pos, BlockPos ignore2)
	{
		if(state.getValue(WATERLOGGED))
			accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
		
		var wire = Cast.cast(accessor.getBlockEntity(pos), TileEnergyWire.class);
		if(wire != null)
			for(Direction d : DIRECTIONS)
				state = state.setValue(DIR2PROP.get(d), wire.doesConnectTo(d));
		
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
		
		var wire = Cast.cast(accessor.getBlockEntity(pos), TileEnergyWire.class);
		if(wire == null)
		{
			wire = new TileEnergyWire(pos, state);
			wire.setLevel(accessor);
		}
		
		for(Direction d : DIRECTIONS)
			state = state.setValue(DIR2PROP.get(d), wire.doesConnectTo(d));
		
		return state;
	}
	
	@Override
	public void onPostRegistered()
	{
		BlockEntityAdapter.addBlocksToEntityType(TilesZT_Transport.ENERGY_WIRE, this);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileEnergyWire(pos, state);
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
	
	@Override
	public void onRemove(BlockState prevState, Level world, BlockPos pos, BlockState newState, boolean flag64)
	{
		if(!prevState.is(newState.getBlock()))
		{
			BlockEntity b = world.getBlockEntity(pos);
			if(b instanceof TileEnergyWire wire)
			{
				NonNullList<ItemStack> drops = NonNullList.create();
				for(FacadeData.FacadeFace facadeFace : wire.facades.getFaces().values())
					drops.add(facadeFace.facadeItem());
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
		if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof TileEnergyWire wire)
		{
			var idx = cutPart.ordinal();
			var cfg = wire.sideConfigs.get(idx);
			wire.sideConfigs.set(idx, cfg == SideConfig.DISABLE ? SideConfig.NONE : SideConfig.DISABLE);
			
			if(context.getLevel().getBlockEntity(context.getClickedPos().relative(cutPart)) instanceof TileEnergyWire wire2)
				wire2.sideConfigs.set(cutPart.getOpposite().ordinal(), wire.sideConfigs.get(idx));
			
			return true;
		}
		return true;
	}
}