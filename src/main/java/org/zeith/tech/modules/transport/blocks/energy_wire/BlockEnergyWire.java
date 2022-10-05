package org.zeith.tech.modules.transport.blocks.energy_wire;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.tile.BlockEntityTypeModifier;
import org.zeith.tech.api.voxels.VoxelShapeCache;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.blocks.BaseEntityBlockZT;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;

import java.util.List;
import java.util.Map;

public class BlockEnergyWire
		extends BaseEntityBlockZT
		implements ICreativeTabBlock, IRegisterListener, SimpleWaterloggedBlock
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
	
	public BlockEnergyWire(EnergyWireProperties properties)
	{
		super(properties.blockProps());
		this.properties = properties;
		
		addBlockTag(TagsZT.Blocks.MINEABLE_WITH_WIRE_CUTTER);
		
		shapeCache = new VoxelShapeCache((state, $) -> Shapes.or(this.properties.insulated() ? CORE_SHAPE_INSULATED : CORE_SHAPE_UNINSULATED,
				DIR2PROP.entrySet()
						.stream()
						.filter(dir -> state.getValue(dir.getValue()))
						.map(dir -> (this.properties.insulated() ? DIR2SHAPE_INSULATED : DIR2SHAPE_UNINSULATED).get(dir.getKey()))
						.toArray(VoxelShape[]::new)
		));
		
		dropsSelf();
	}
	
	@Override
	protected BlockItem newBlockItem(Item.Properties props)
	{
		return new BlockItem(this, props)
		{
			@Override
			public void appendHoverText(ItemStack stack, @Nullable Level lvl, List<Component> tooltip, TooltipFlag flags)
			{
				tooltip.add(Component.translatable("info." + ZeithTech.MOD_ID + "_transport.wire_max_io", Component.literal(Integer.toString(properties.tier().maxFE()))));
				tooltip.add(Component.translatable("info." + ZeithTech.MOD_ID + "_transport.wire_loss", Component.literal(Float.toString(properties.energyLoss()))));
			}
		};
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
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return shapeCache.get(state);
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
		BlockEntityTypeModifier.addBlocksToEntityType(TilesZT_Transport.ENERGY_WIRE, this);
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
}