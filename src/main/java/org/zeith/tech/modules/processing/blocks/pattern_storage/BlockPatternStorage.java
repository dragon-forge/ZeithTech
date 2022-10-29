package org.zeith.tech.modules.processing.blocks.pattern_storage;

import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.core.ZeithTech;

import java.util.List;

public class BlockPatternStorage
		extends BaseEntityBlock
		implements ICreativeTabBlock
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
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return ZeithTech.TAB;
	}
	
	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int event, int data)
	{
		super.triggerEvent(state, level, pos, event, data);
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity != null && blockentity.triggerEvent(event, data);
	}
}
