package org.zeith.tech.common.blocks.base.machine;

import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import org.zeith.api.wrench.IWrenchable;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.hammerlib.tiles.TileSyncableTickable;

import java.util.List;
import java.util.stream.IntStream;

public abstract class TileBaseMachine<T extends TileBaseMachine<T>>
		extends TileSyncableTickable
		implements IContainerTile, IWrenchable
{
	public TileBaseMachine(BlockEntityType<T> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public abstract void update();
	
	public boolean isEnabled()
	{
		var s = level.getBlockState(worldPosition);
		if(s.getBlock() == getBlockState().getBlock() && s.hasProperty(BlockStateProperties.ENABLED))
			return s.getValue(BlockStateProperties.ENABLED);
		return false;
	}
	
	public void setEnabledState(boolean enabled)
	{
		var s = level.getBlockState(worldPosition);
		if(s.getBlock() == getBlockState().getBlock() && s.hasProperty(BlockStateProperties.ENABLED))
		{
			level.setBlockAndUpdate(worldPosition, s = s.setValue(BlockStateProperties.ENABLED, enabled));
			setBlockState(s);
		}
	}
	
	@Override
	public abstract ContainerBaseMachine<T> openContainer(Player player, int windowId);
	
	public abstract List<Container> getAllInventories();
	
	public NonNullList<ItemStack> generateMachineDrops()
	{
		var lst = NonNullList.<ItemStack> create();
		getAllInventories()
				.stream()
				.flatMap(ctr -> IntStream.range(0, ctr.getContainerSize()).mapToObj(ctr::getItem))
				.filter(i -> !i.isEmpty())
				.forEach(lst::add);
		return lst;
	}
	
	public boolean atWorldTickRate(int rate)
	{
		return level != null && level.getGameTime() % rate == 0;
	}
	
	@Override
	public boolean onWrenchUsed(UseOnContext context)
	{
		var d = context.getClickedFace();
		if(context.getPlayer().isShiftKeyDown()) d = d.getOpposite();
		
		var level = context.getLevel();
		if(level.isClientSide()) return true;
		var worldPosition = context.getClickedPos();
		
		BlockState state = level.getBlockState(worldPosition);
		if(state.getBlock() == getBlockState().getBlock() && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
		{
			Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
			final Direction origin = facing;
			if(d == Direction.UP)
				facing = facing.getClockWise();
			else if(d == Direction.DOWN)
				facing = facing.getCounterClockWise();
			else
				facing = d;
			if(origin != facing)
				level.setBlockAndUpdate(worldPosition, state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing));
		}
		
		return true;
	}
	
	public Direction getFront()
	{
		BlockState state = level.getBlockState(worldPosition);
		if(state.getBlock() == getBlockState().getBlock() && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
			return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		return null;
	}
	
	@Nullable
	@Override
	public Component getDisplayName()
	{
		return getBlockState().getBlock().getName();
	}
}