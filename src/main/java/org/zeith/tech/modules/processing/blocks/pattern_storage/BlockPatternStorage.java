package org.zeith.tech.modules.processing.blocks.pattern_storage;

import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.voxels.VoxelShapeCache;
import org.zeith.tech.core.client.renderer.item.BlockItemWithAltISTER;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;
import org.zeith.tech.modules.shared.BaseZT;

import java.util.List;
import java.util.function.Consumer;

public class BlockPatternStorage
		extends BaseEntityBlock
		implements ICustomBlockItem
{
	public BlockPatternStorage()
	{
		super(Block.Properties
				.of(Material.METAL)
				.requiresCorrectToolForDrops()
				.sound(SoundType.METAL)
				.strength(1.5F));
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, Tiers.IRON, this);
	}
	
	protected final VoxelShapeCache shapeCache = new VoxelShapeCache(this, ((state, $) ->
	{
		var dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		return Shapes.or(
				box(1, 0, 1, 15, 5, 15),
				box(1, 13, 1, 15, 15, 15),
				$.box(dir, 1, 5, 2, 2, 13, 14),
				$.box(dir, 1, 5, 14, 15, 13, 15),
				$.box(dir, 14, 5, 2, 15, 13, 14)
		);
	}));
	
	protected final VoxelShapeCache shelfCache = new VoxelShapeCache(this, ((state, $) ->
	{
		var dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		return Shapes.or(
				$.box(dir, 3, 5, 2, 13, 6, 14),
				$.box(dir, 2, 5, 14, 14, 11, 15),
				$.box(dir, 1, 5, 1, 15, 13, 2),
				$.box(dir, 13, 5, 2, 14, 11, 14),
				$.box(dir, 2, 5, 2, 3, 11, 14),
				$.box(dir, 6, 10, 0, 10, 11, 1)
		);
	}));
	
	public VoxelShape getShelfShape(BlockState state, float openness)
	{
		var dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		
		float progress = openness;
		progress = 1.0F - progress;
		progress = 1F - progress * progress * progress + 0.01F;
		progress *= 0.3125F;
		
		double moveX = dir == Direction.EAST ? progress : dir == Direction.WEST ? -progress : 0;
		double moveZ = dir == Direction.SOUTH ? progress : dir == Direction.NORTH ? -progress : 0;
		
		return shelfCache.get(state).move(moveX, 0, moveZ);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
	{
		if(level.getBlockEntity(pos) instanceof TilePatternStorage entity)
			return Shapes.or(shapeCache.get(state), getShelfShape(state, entity.getOpenness(ZeithTechAPI.get().getPartialTick())));
		return shapeCache.get(state);
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(stack.hasTag())
		{
			TilePatternStorage tank;
			
			if(level.getBlockEntity(pos) instanceof TilePatternStorage tile) tank = tile;
			else
			{
				tank = newBlockEntity(pos, state);
				level.setBlockEntity(tank);
			}
			
			tank.loadFromItem(stack);
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		NonNullList<ItemStack> stacks = NonNullList.create();
		BlockEntity tile = builder.getParameter(LootContextParams.BLOCK_ENTITY);
		if(tile instanceof TilePatternStorage te)
		{
			stacks.add(te.generateItem(this));
			te.patterns.clear(); // remove them if they are gathered into an item.
		} else
			stacks.add(new ItemStack(this));
		return stacks;
	}
	
	@Nullable
	@Override
	public TilePatternStorage newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TilePatternStorage(pos, state);
	}
	
	@Override
	public void onRemove(BlockState prevState, Level world, BlockPos pos, BlockState newState, boolean flag64)
	{
		if(!prevState.is(newState.getBlock()))
			super.onRemove(prevState, world, pos, newState, flag64);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState p_49232_)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray)
	{
		ContainerAPI.openContainerTile(player, Cast.cast(world.getBlockEntity(pos), TilePatternStorage.class));
		return InteractionResult.SUCCESS;
	}
	
	@javax.annotation.Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return defaultBlockState()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getPlayer() != null
						? ctx.getPlayer().getDirection().getOpposite()
						: Direction.NORTH);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return BlockAPI.ticker();
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		return new BlockItem(this, BaseZT.itemProps())
		{
			@Override
			public void initializeClient(Consumer<IClientItemExtensions> consumer)
			{
				BlockItemWithAltISTER.INSTANCE
						.bind(BlockPatternStorage.this, TilesZT_Processing.PATTERN_STORAGE)
						.ifPresent(consumer);
			}
		};
	}
}
