package org.zeith.tech.modules.processing.blocks.base.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.core.ZeithTech;

import java.util.List;

public abstract class BlockBaseMachine<T extends TileBaseMachine<T>>
		extends BaseEntityBlock
		implements ICreativeTabBlock
{
	final Class<T> tileType;
	
	public BlockBaseMachine(Class<T> tileType)
	{
		this(tileType, Block.Properties
				.of(Material.METAL)
				.requiresCorrectToolForDrops()
				.sound(SoundType.METAL)
				.strength(1.5F)
		);
	}
	
	public BlockBaseMachine(Class<T> tileType, Block.Properties props)
	{
		super(props);
		this.tileType = tileType;
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, Tiers.IRON, this);
	}
	
	public Class<T> getTileType()
	{
		return tileType;
	}
	
	@Nullable
	@Override
	public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		return List.of(new ItemStack(this));
	}
	
	@Override
	public void onRemove(BlockState prevState, Level world, BlockPos pos, BlockState newState, boolean flag64)
	{
		if(!prevState.is(newState.getBlock()))
		{
			BlockEntity tileentity = world.getBlockEntity(pos);
			if(tileType.isInstance(tileentity))
			{
				var drops = tileType.cast(tileentity).generateMachineDrops();
				if(drops != null && !drops.isEmpty()) Containers.dropContents(world, pos, drops);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(prevState, world, pos, newState, flag64);
		}
	}
	
	@javax.annotation.Nullable
	@Override
	public <R extends BlockEntity> BlockEntityTicker<R> getTicker(Level level, BlockState state, BlockEntityType<R> type)
	{
		return BlockAPI.ticker();
	}
	
	@Override
	public RenderShape getRenderShape(BlockState p_49232_)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(BlockStateProperties.ENABLED);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray)
	{
		ContainerAPI.openContainerTile(player, Cast.cast(world.getBlockEntity(pos), tileType));
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return defaultBlockState()
				.setValue(BlockStateProperties.ENABLED, false)
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