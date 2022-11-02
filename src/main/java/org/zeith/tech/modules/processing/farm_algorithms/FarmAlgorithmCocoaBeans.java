package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.misc.farm.*;
import org.zeith.tech.api.utils.LazyValue;

import java.util.ArrayList;
import java.util.List;

public class FarmAlgorithmCocoaBeans
		extends FarmAlgorithm
{
	protected final LazyValue<Ingredient> item = LazyValue.of(() -> Ingredient.of(Items.COCOA_BEANS));
	
	public FarmAlgorithmCocoaBeans()
	{
		super(new Properties()
				.upsideDown(false)
		);
	}
	
	@Override
	public int getColor()
	{
		return 0x9f603b;
	}
	
	@Override
	public @NotNull Ingredient getProgrammingItem()
	{
		return item.getValue();
	}
	
	@Override
	public @NotNull EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack)
	{
		if(stack.is(Items.BONE_MEAL)) return EnumFarmItemCategory.FERTILIZER;
		if(item.getValue().test(stack)) return EnumFarmItemCategory.PLANT;
		
		if(stack.getItem() instanceof BlockItem bi && bi.getBlock().defaultBlockState().is(BlockTags.JUNGLE_LOGS))
			return EnumFarmItemCategory.SOIL;
		
		return EnumFarmItemCategory.UNKNOWN;
	}
	
	protected boolean isWoodPosition(BlockPos delta)
	{
		int x = delta.getX(), z = delta.getZ();
		return (x + (1 - (z / 2) % 2) + 2) % 3 == 0 && z % 2 == 0;
	}
	
	@Override
	public AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var delta = platform.subtract(controller.getFarmPosition().offset(-6, 0, -6));
		
		var isWoodXZ = isWoodPosition(delta);
		
		Direction cocoaPlaceDir = null;
		for(var d : Direction.Plane.HORIZONTAL)
			if(isWoodPosition(delta.relative(d)))
				cocoaPlaceDir = d;
		var isCocoa = cocoaPlaceDir != null;
		
		BlockState cocoaState;
		for(int yAdd = 3; yAdd > 0; --yAdd)
		{
			var pos = platform.above(yAdd);
			if(level.isEmptyBlock(pos)) continue;
			if((isWoodXZ && !level.getBlockState(pos).is(BlockTags.JUNGLE_LOGS))
					|| (!isWoodXZ &&
					(!isCocoa || !(cocoaState = level.getBlockState(pos)).is(Blocks.COCOA) || cocoaState.getValue(CocoaBlock.AGE) == 2)
			))
			{
				controller.queueBlockHarvest(pos, 1);
				return AlgorithmUpdateResult.RETRY;
			}
		}
		
		for(int yAdd = 1; yAdd < 4; ++yAdd)
		{
			var pos = platform.above(yAdd);
			var air = level.isEmptyBlock(pos);
			
			if(air && isWoodXZ)
			{
				var soilInv = controller.getInventory(EnumFarmItemCategory.SOIL);
				for(int i = 0; i < soilInv.getSlots(); ++i)
				{
					var stack = soilInv.getStackInSlot(i);
					if(!stack.isEmpty() && stack.getItem() instanceof BlockItem bi && bi.getBlock().defaultBlockState().is(BlockTags.JUNGLE_LOGS))
					{
						var newState = bi.getBlock().defaultBlockState();
						controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.SOIL, stack.copy().split(1)), pos, newState, 0, 1);
						return AlgorithmUpdateResult.RETRY;
					}
				}
			}
			
			if(air && isCocoa)
			{
				var soilInv = controller.getInventory(EnumFarmItemCategory.PLANT);
				for(int i = 0; i < soilInv.getSlots(); ++i)
				{
					var stack = soilInv.getStackInSlot(i);
					if(!stack.isEmpty() && stack.getItem() instanceof BlockItem bi && stack.is(Items.COCOA_BEANS))
					{
						var state = bi.getBlock().getStateForPlacement(controller.createPlaceContext(level, pos, stack.copy(), cocoaPlaceDir));
						if(state != null && state.is(Blocks.COCOA))
						{
							controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, stack.copy().split(1)), pos, state, 10, 1);
							return AlgorithmUpdateResult.RETRY;
						}
						
						break;
					}
				}
			}
		}
		
		return AlgorithmUpdateResult.PASS;
	}
	
	@Override
	public boolean tryFertilize(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		List<BlockPos> cocoas = new ArrayList<>();
		
		for(int yAdd = 3; yAdd > 0; --yAdd)
		{
			var cropPos = platform.above(yAdd);
			var cropState = level.getBlockState(cropPos);
			
			if(cropState.getBlock() instanceof BonemealableBlock gr
					&& gr.isValidBonemealTarget(level, cropPos, cropState, level.isClientSide)
					&& gr.isBonemealSuccess(level, level.random, cropPos, cropState))
				cocoas.add(cropPos);
		}
		
		if(!cocoas.isEmpty())
		{
			var cropPos = cocoas.get(level.random.nextInt(cocoas.size()));
			var cropState = level.getBlockState(cropPos);
			
			if(cropState.getBlock() instanceof BonemealableBlock gr
					&& gr.isValidBonemealTarget(level, cropPos, cropState, level.isClientSide)
					&& gr.isBonemealSuccess(level, level.random, cropPos, cropState))
			{
				gr.performBonemeal(level, level.random, cropPos, cropState);
				level.levelEvent(1505, cropPos, 0);
				return true;
			}
		}
		
		return false;
	}
}