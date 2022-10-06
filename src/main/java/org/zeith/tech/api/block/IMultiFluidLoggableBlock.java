package org.zeith.tech.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.*;

import java.util.Map;

public interface IMultiFluidLoggableBlock
		extends SimpleWaterloggedBlock
{
	Map<FlowingFluid, BooleanProperty> getFluidLoggableProperties();
	
	default BlockState getStateWithNoFluids(BlockState state)
	{
		for(var entry : getFluidLoggableProperties().entrySet())
			state = state.setValue(entry.getValue(), false);
		return state;
	}
	
	default BlockState updateFluidLoggedShape(LevelAccessor accessor, BlockPos pos, BlockState state)
	{
		for(var entry : getFluidLoggableProperties().entrySet())
			if(state.getValue(entry.getValue()))
				accessor.scheduleTick(pos, entry.getKey(), entry.getKey().getTickDelay(accessor));
		return state;
	}
	
	default BlockState getFluidLoggedStateForPlacement(BlockPlaceContext ctx, BlockState def)
	{
		var accessor = ctx.getLevel();
		var pos = ctx.getClickedPos();
		
		for(var entry : getFluidLoggableProperties().entrySet())
			def = def.setValue(entry.getValue(), accessor.getFluidState(pos).getType() == entry.getKey());
		
		return def;
	}
	
	@Override
	default boolean canPlaceLiquid(BlockGetter getter, BlockPos pos, BlockState state, Fluid fluid)
	{
		var props = getFluidLoggableProperties();
		if(props.values().stream().anyMatch(state::getValue))
			return false;
		return fluid instanceof FlowingFluid && props.containsKey(fluid);
	}
	
	default FluidState getFluidLoggedState(BlockState state)
	{
		for(var entry : getFluidLoggableProperties().entrySet())
			if(state.getValue(entry.getValue()))
				return entry.getKey().getSource(false);
		
		return Fluids.EMPTY.defaultFluidState();
	}
	
	@Override
	default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid)
	{
		var props = getFluidLoggableProperties();
		
		if(props.values().stream().anyMatch(state::getValue))
			return false;
		
		for(var prop : props.entrySet())
		{
			if(!state.getValue(prop.getValue()) && fluid.getType() == prop.getKey())
			{
				if(!level.isClientSide())
				{
					level.setBlock(pos, state.setValue(prop.getValue(), true), 3);
					level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	default ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state)
	{
		for(var prop : getFluidLoggableProperties().entrySet())
		{
			if(state.getValue(prop.getValue()))
			{
				var bucket = prop.getKey().getBucket();
				if(bucket == Items.AIR) continue;
				level.setBlock(pos, state.setValue(prop.getValue(), false), 3);
				if(!state.canSurvive(level, pos))
					level.destroyBlock(pos, true);
				return new ItemStack(bucket);
			}
		}
		
		return ItemStack.EMPTY;
	}
}