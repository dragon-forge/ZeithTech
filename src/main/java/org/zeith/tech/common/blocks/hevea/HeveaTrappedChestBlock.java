package org.zeith.tech.common.blocks.hevea;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class HeveaTrappedChestBlock
		extends HeveaChestBlock
{
	public HeveaTrappedChestBlock(Properties props, Supplier<BlockEntityType<? extends ChestBlockEntity>> type)
	{
		super(props, type);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos p_154834_, BlockState p_154835_)
	{
		return new TrappedChestBlockEntity(p_154834_, p_154835_);
	}
	
	@Override
	protected Stat<ResourceLocation> getOpenChestStat()
	{
		return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
	}
	
	@Override
	public boolean isSignalSource(BlockState p_57587_)
	{
		return true;
	}
	
	@Override
	public int getSignal(BlockState p_57577_, BlockGetter p_57578_, BlockPos p_57579_, Direction p_57580_)
	{
		return Mth.clamp(ChestBlockEntity.getOpenCount(p_57578_, p_57579_), 0, 15);
	}
	
	@Override
	public int getDirectSignal(BlockState p_57582_, BlockGetter p_57583_, BlockPos p_57584_, Direction p_57585_)
	{
		return p_57585_ == Direction.UP ? p_57582_.getSignal(p_57583_, p_57584_, p_57585_) : 0;
	}
}