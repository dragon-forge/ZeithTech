package org.zeith.tech.modules.world.blocks.tap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.tech.modules.shared.init.ItemsZT;
import org.zeith.tech.modules.world.blocks.BlockHeveaLog;
import org.zeith.tech.modules.world.init.BlocksZT_World;
import org.zeith.tech.modules.world.init.TilesZT_World;

public class TileHeveaTreeTap
		extends TileSyncableTickable
		implements IItemHandler
{
	public TileHeveaTreeTap(BlockPos pos, BlockState state)
	{
		this(TilesZT_World.HEVEA_TREE_TAP, pos, state);
	}
	
	public TileHeveaTreeTap(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void update()
	{
		var state = level.getBlockState(worldPosition);
		if(atTickRate(100) && getType().isValid(state))
		{
			var treePos = worldPosition.relative(state.getValue(BlockHeveaTreeTap.FACING).getOpposite());
			
			BlockState treeState;
			while((treeState = level.getBlockState(treePos)).is(BlocksZT_World.STRIPPED_HEVEA_LOG) || treeState.is(BlocksZT_World.STRIPPED_HEVEA_WOOD))
			{
				if(treeState.hasProperty(BlockHeveaLog.LEAKING) && treeState.getValue(BlockHeveaLog.LEAKING))
				{
					if(treePos.getY() > worldPosition.getY())
					{
						level.setBlockAndUpdate(treePos, treeState.setValue(BlockHeveaLog.LEAKING, false));
						level.setBlockAndUpdate(treePos.below(), level.getBlockState(treePos.below()).setValue(BlockHeveaLog.LEAKING, true));
					} else if(state.getValue(BlockHeveaTreeTap.TAP) && state.getValue(BlockHeveaTreeTap.BOWL) && !state.getValue(BlockHeveaTreeTap.RESIN) && isOnServer())
					{
						level.setBlockAndUpdate(worldPosition, state.setValue(BlockHeveaTreeTap.RESIN, true));
						level.setBlockAndUpdate(treePos, treeState.setValue(BlockHeveaLog.LEAKING, false));
					}
					break;
				}
				
				treePos = treePos.above();
			}
		}
	}
	
	@Override
	public int getSlots()
	{
		return 2;
	}
	
	@Override
	public @NotNull ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
	{
		var state = level.getBlockState(worldPosition);
		
		if(slot == 0 && stack.is(Items.BOWL) && !state.getValue(BlockHeveaTreeTap.BOWL))
		{
			if(!simulate)
				level.setBlockAndUpdate(worldPosition, state.setValue(BlockHeveaTreeTap.BOWL, true).setValue(BlockHeveaTreeTap.RESIN, false));
			var leftover = stack.copy();
			leftover.shrink(1);
			return leftover;
		}
		return stack;
	}
	
	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		var state = level.getBlockState(worldPosition);
		
		if(slot == 1 && amount > 0 && state.getValue(BlockHeveaTreeTap.BOWL) && state.getValue(BlockHeveaTreeTap.RESIN))
		{
			if(!simulate)
				level.setBlockAndUpdate(worldPosition, state.setValue(BlockHeveaTreeTap.BOWL, false).setValue(BlockHeveaTreeTap.RESIN, false));
			return new ItemStack(ItemsZT.BOWL_OF_RESIN);
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public int getSlotLimit(int slot)
	{
		return 1;
	}
	
	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack)
	{
		return slot == 0 && stack.is(Items.BOWL);
	}
	
	LazyOptional<IItemHandler> items = LazyOptional.of(() -> this);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ITEM_HANDLER)
			return items.cast();
		return super.getCapability(cap, side);
	}
}