package org.zeith.tech.common.blocks.hevea.tap;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.INoItemBlock;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.api.voxels.VoxelShapeCache;
import org.zeith.tech.common.blocks.BaseEntityBlockZT;
import org.zeith.tech.init.BlocksZT;
import org.zeith.tech.init.ItemsZT;

import java.util.List;

public class BlockHeveaTreeTap
		extends BaseEntityBlockZT
		implements INoItemBlock
{
	static
	{
		MinecraftForge.EVENT_BUS.addListener(BlockHeveaTreeTap::sticksPlaceHook);
	}
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty TAP = BooleanProperty.create("tap");
	public static final BooleanProperty BOWL = BooleanProperty.create("bowl");
	public static final BooleanProperty RESIN = BooleanProperty.create("resin");
	
	public BlockHeveaTreeTap(Properties props)
	{
		super(props, BlockHarvestAdapter.MineableType.AXE);
		
		registerDefaultState(defaultBlockState()
				.setValue(TAP, false)
				.setValue(BOWL, false)
				.setValue(RESIN, false)
		);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, TAP, BOWL, RESIN);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileHeveaTreeTap(pos, state);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		var lst = NonNullList.<ItemStack> create();
		lst.add(new ItemStack(Items.STICK, 1 + builder.getLevel().random.nextInt(4)));
		if(state.getValue(TAP))
			lst.add(new ItemStack(ItemsZT.TREE_TAP));
		if(state.getValue(BOWL))
			lst.add(new ItemStack(state.getValue(RESIN) ? ItemsZT.BOWL_OF_RESIN : Items.BOWL));
		return lst;
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
	{
		var rs = reader.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
		return rs.is(BlocksZT.STRIPPED_HEVEA_LOG) || rs.is(BlocksZT.STRIPPED_HEVEA_WOOD);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level lvl, BlockPos pos, Block p_52528_, BlockPos p_52529_, boolean p_52530_)
	{
		if(!state.canSurvive(lvl, pos))
		{
			BlockEntity blockentity = state.hasBlockEntity() ? lvl.getBlockEntity(pos) : null;
			dropResources(state, lvl, pos, blockentity);
			lvl.destroyBlock(pos, false);
			
			for(Direction dir : Direction.values())
				lvl.updateNeighborsAt(pos.relative(dir), this);
		}
	}
	
	private final VoxelShapeCache voxelCache = new VoxelShapeCache((state, $) ->
	{
		Direction d = state.getValue(FACING);
		
		var finalShape = Shapes.or($.box(d, 10, 0, 15.5, 11, 5, 16),
				$.box(d, 5, 0, 15.5, 6, 5, 16),
				$.box(d, 4.75, 5, 15.25, 4.75 + 6.5, 6, 16),
				$.box(d, 4.75, 5.5, 8.75, 5.75, 6, 8.75 + 6.5),
				$.box(d, 10.25, 5.5, 8.75, 11.25, 6, 8.75 + 6.5)
		);
		
		if(state.getValue(TAP))
			finalShape = Shapes.or(finalShape, $.box(d, 7.75, 7.25, 14, 7.75 + 1.5, 9, 16));
		
		if(state.getValue(BOWL))
			finalShape = Shapes.or(finalShape,
					$.box(d, 6, 5.5, 10, 10, 6, 14),
					$.box(d, 10, 6, 10, 10.5, 6.5, 14),
					$.box(d, 5.5, 6, 10, 6, 6.5, 14),
					$.box(d, 5.5, 6, 9.5, 10.5, 6.5, 10),
					$.box(d, 5.5, 6, 14, 10.5, 6.5, 14.5),
					$.box(d, 5.5, 6.5, 14.5, 10.5, 7.5, 15),
					$.box(d, 5, 6.5, 9.25, 5.5, 7.5, 14.75),
					$.box(d, 10.5, 6.5, 9.25, 11, 7.5, 14.75),
					$.box(d, 5.5, 6.5, 9, 10.5, 7.5, 9.5)
			);
		
		return finalShape;
	});
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return voxelCache.get(state);
	}
	
	@Override
	public boolean isOcclusionShapeFullBlock(BlockState p_222959_, BlockGetter p_222960_, BlockPos p_222961_)
	{
		return false;
	}
	
	@Override
	public boolean isCollisionShapeFullBlock(BlockState p_181242_, BlockGetter p_181243_, BlockPos p_181244_)
	{
		return false;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level lvl, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		var stack = player.getItemInHand(hand);
		
		if(!state.getValue(TAP) && stack.is(ItemsZT.TREE_TAP))
		{
			stack.shrink(1);
			lvl.setBlockAndUpdate(pos, state.setValue(TAP, true));
			return InteractionResult.SUCCESS;
		}
		
		if(!state.getValue(BOWL) && stack.is(Items.BOWL))
		{
			stack.shrink(1);
			lvl.setBlockAndUpdate(pos, state.setValue(BOWL, true));
			return InteractionResult.SUCCESS;
		}
		
		if(state.getValue(BOWL))
		{
			ItemStack drop = new ItemStack(state.getValue(RESIN) ? ItemsZT.BOWL_OF_RESIN : Items.BOWL);
			lvl.setBlockAndUpdate(pos, state.setValue(BOWL, false).setValue(RESIN, false));
			player.getInventory().add(drop);
			if(!drop.isEmpty() && !lvl.isClientSide())
				Containers.dropItemStack(lvl, player.getX(), player.getY(), player.getZ(), drop);
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return BlockAPI.ticker();
	}
	
	private static void sticksPlaceHook(PlayerInteractEvent.RightClickBlock e)
	{
		var item = e.getItemStack();
		var hit = e.getHitVec();
		if(hit.getDirection().getAxis() != Direction.Axis.Y && !item.isEmpty() && item.is(Items.STICK) && (item.getCount() >= 5 || e.getEntity().getAbilities().instabuild))
		{
			var lvl = e.getLevel();
			
			var state = lvl.getBlockState(hit.getBlockPos());
			if(state.is(BlocksZT.STRIPPED_HEVEA_LOG) || state.is(BlocksZT.STRIPPED_HEVEA_WOOD))
			{
				var placePos = hit.getBlockPos().relative(e.getFace());
				
				if(lvl.isUnobstructed(lvl.getBlockState(placePos), placePos, CollisionContext.of(e.getEntity())))
				{
					var newState = BlocksZT.HEVEA_TREE_TAP.defaultBlockState()
							.setValue(FACING, e.getFace())
							.setValue(TAP, false)
							.setValue(BOWL, false)
							.setValue(RESIN, false);
					
					lvl.setBlock(placePos, newState, 11);
					
					newState.getBlock().setPlacedBy(lvl, placePos, newState, e.getEntity(), e.getItemStack());
					
					if(e.getEntity() instanceof ServerPlayer sp)
					{
						CriteriaTriggers.PLACED_BLOCK.trigger(sp, placePos, item);
					}
					
					lvl.gameEvent(GameEvent.BLOCK_PLACE, placePos, GameEvent.Context.of(e.getEntity(), newState));
					SoundType soundtype = newState.getSoundType(lvl, placePos, e.getEntity());
					lvl.playSound(e.getEntity(), placePos, SoundType.WOOD.getStepSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					
					if(!e.getEntity().getAbilities().instabuild)
						item.shrink(5);
					
					e.setCancellationResult(InteractionResult.SUCCESS);
					e.setCanceled(true);
				}
			}
		}
	}
}