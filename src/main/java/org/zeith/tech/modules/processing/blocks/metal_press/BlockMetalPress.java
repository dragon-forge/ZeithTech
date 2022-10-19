package org.zeith.tech.modules.processing.blocks.metal_press;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.blocks.IHitsDifferentTargetBlock;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.block.ZeithTechStateProperties;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.item.BlockItemWithAltISTER;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;
import org.zeith.tech.modules.shared.blocks.TileCapabilityProxy;

import java.util.List;
import java.util.function.Consumer;

public class BlockMetalPress
		extends BlockBaseMachine<TileMetalPress>
		implements ICustomBlockItem, IHitsDifferentTargetBlock
{
	public static final VoxelShape SHAPE_LOW = Shapes.or(
			box(0, 0, 0, 16, 4, 16),
			box(0, 4, 0, 2, 25, 2),
			box(14, 4, 0, 16, 25, 2),
			box(0, 4, 14, 2, 25, 16),
			box(14, 4, 14, 16, 25, 16),
			box(0, 25, 0, 16, 32, 16)
	);
	
	public static final VoxelShape SHAPE_TOP = SHAPE_LOW.move(0, -1, 0);
	
	public BlockMetalPress()
	{
		super(TileMetalPress.class);
		
		registerDefaultState(defaultBlockState().setValue(ZeithTechStateProperties.OFFSET, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(ZeithTechStateProperties.OFFSET);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var state = super.getStateForPlacement(ctx);
		if(!canSurvive(state, ctx.getLevel(), ctx.getClickedPos()))
			return null;
		return state;
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		if(level.isEmptyBlock(pos.above()))
			level.setBlockAndUpdate(pos.above(), state.setValue(ZeithTechStateProperties.OFFSET, true));
		super.setPlacedBy(level, pos, state, placer, stack);
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		if(state.getValue(ZeithTechStateProperties.OFFSET))
			return level.getBlockState(pos.below()).equals(state.setValue(ZeithTechStateProperties.OFFSET, false));
		return level.getBlockState(pos.above()).equals(state.setValue(ZeithTechStateProperties.OFFSET, true)) || level.isEmptyBlock(pos.above());
	}
	
	@Override
	public void onRemove(BlockState prevState, Level world, BlockPos pos, BlockState newState, boolean flag64)
	{
		if(!prevState.is(newState.getBlock()))
		{
			var np = pos.relative(prevState.getValue(ZeithTechStateProperties.OFFSET) ? Direction.DOWN : Direction.UP);
			world.destroyBlock(np, true);
		}
		
		super.onRemove(prevState, world, pos, newState, flag64);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		return state.getValue(ZeithTechStateProperties.OFFSET) ? List.of() : List.of(new ItemStack(this));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext p_60558_)
	{
		var offset = state.getValue(ZeithTechStateProperties.OFFSET);
		
		if(level.getBlockEntity(offset ? pos.below() : pos) instanceof TileMetalPress entity)
		{
			var partial = ZeithTechAPI.get().getPartialTick();
			float progress = Mth.lerp(partial, entity.prevProgress, entity.currentProgress);
			int mp = Math.max(entity._maxProgress, 1);
			
			float y = 19.0F;
			y *= Math.min(entity.fallTimer > 0 ? (1F - (entity.fallTimer + partial) / 5F) : 1F, progress / (mp - 7));
			
			var box = box(1, 4 + y, 1, 15, 6 + y, 15);
			if(offset) box = box.move(0, -1, 0);
			
			return Shapes.or(offset ? SHAPE_TOP : SHAPE_LOW, box);
		}
		
		return offset ? SHAPE_TOP : SHAPE_LOW;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return state.getValue(ZeithTechStateProperties.OFFSET) ? RenderShape.INVISIBLE : RenderShape.MODEL;
	}
	
	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return state.getValue(ZeithTechStateProperties.OFFSET) ? new TileCapabilityProxy(pos, state).setPosition(pos.immutable().below()) : new TileMetalPress(pos, state);
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		return new BlockItem(this, new Item.Properties().tab(ZeithTech.TAB))
		{
			@Override
			public void initializeClient(Consumer<IClientItemExtensions> consumer)
			{
				BlockItemWithAltISTER.INSTANCE
						.bind(BlockMetalPress.this, TilesZT_Processing.METAL_PRESS)
						.ifPresent(consumer);
			}
		};
	}
	
	@Override
	public BlockPos alterHitPosition(Level level, BlockPos pos, BlockState state)
	{
		if(state.getValue(ZeithTechStateProperties.OFFSET))
			return pos.below();
		return pos;
	}
}
