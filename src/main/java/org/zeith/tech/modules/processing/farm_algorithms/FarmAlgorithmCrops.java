package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.block.IFarmlandBlock;
import org.zeith.tech.api.misc.SoundConfiguration;
import org.zeith.tech.api.misc.farm.*;
import org.zeith.tech.api.utils.*;

import java.util.ArrayList;
import java.util.List;

public class FarmAlgorithmCrops
		extends FarmAlgorithm
{
	public FarmAlgorithmCrops()
	{
		super(new Properties()
				.upsideDown(false)
		);
	}
	
	@Override
	public int getColor()
	{
		return 0x5EFF00;
	}
	
	protected final LazyValue<Ingredient> item = LazyValue.of(() ->
	{
		List<Item> items = new ArrayList<>();
		for(Item item : ForgeRegistries.ITEMS)
			if(item instanceof BlockItem ib && ib.getBlock() instanceof CropBlock)
				items.add(item);
		return Ingredient.of(items.toArray(Item[]::new));
	});
	
	@Override
	public @NotNull Ingredient getProgrammingItem()
	{
		return item.getValue();
	}
	
	@Override
	public @NotNull EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack)
	{
		if(getProgrammingItem().test(stack)) return EnumFarmItemCategory.PLANT;
		if(stack.is(Items.DIRT)) return EnumFarmItemCategory.SOIL;
		if(stack.is(Items.BONE_MEAL)) return EnumFarmItemCategory.FERTILIZER;
		return EnumFarmItemCategory.UNKNOWN;
	}
	
	@Override
	public AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var dirtPos = platform.above();
		var dirtState = level.getBlockState(dirtPos);
		
		var cropPos = dirtPos.above();
		var cropState = level.getBlockState(cropPos);
		
		var hasSoil = false;
		
		BlockState tilled;
		
		if(level.isEmptyBlock(dirtPos))
		{
			var plantInv = controller.getInventory(EnumFarmItemCategory.SOIL);
			for(int i = 0; i < plantInv.getSlots(); ++i)
			{
				var stack = plantInv.getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() instanceof BlockItem bi)
				{
					var newState = bi.getBlock().defaultBlockState();
					
					controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.SOIL, stack.copy().split(1)), dirtPos, newState, 0, 1);
					return AlgorithmUpdateResult.RETRY;
				}
			}
		} else if((tilled = InteractionHelper.getTilledState(level, controller.getAsPlayer(level), dirtPos)) != null && tilled.is(Blocks.FARMLAND))
		{
			var sound = new SoundConfiguration(SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1F, 1F);
			
			controller.queueBlockTransformation(dirtPos, dirtState, tilled,
					List.of(), sound, 100, 0);
			
			return AlgorithmUpdateResult.RETRY;
		} else if(dirtState.getBlock() instanceof IFarmlandBlock)
		{
			hasSoil = true;
		} else if(!level.isEmptyBlock(dirtPos) && level.isEmptyBlock(cropPos))
		{
			controller.queueBlockHarvest(dirtPos, 2);
			return AlgorithmUpdateResult.RETRY;
		}
		
		if(cropState.getBlock() instanceof CropBlock crop)
		{
			if(crop.isMaxAge(cropState))
			{
				var drops = InventoryHelper.getBlockDropsAt(level, cropPos);
				var removedPlantable = false;
				
				for(var drop : drops)
				{
					if(!drop.isEmpty() && drop.getItem() instanceof BlockItem bi && bi.getBlock() == crop)
					{
						removedPlantable = true;
						drop.shrink(1);
						break;
					}
				}
				
				if(removedPlantable) // If we found this crop's seed item, replant it back instantly.
					controller.queueBlockTransformation(cropPos, cropState, crop.getStateForAge(0), drops, SoundConfiguration.place(cropState.getSoundType()), 5, 5);
				else
					controller.queueBlockHarvest(cropPos, 5);
				
				return AlgorithmUpdateResult.RETRY;
			}
			
			return AlgorithmUpdateResult.PASS;
		} else
		{
			if(level.isEmptyBlock(cropPos))
			{
				if(hasSoil)
				{
					var plantInv = controller.getInventory(EnumFarmItemCategory.PLANT);
					for(int i = 0; i < plantInv.getSlots(); ++i)
					{
						var stack = plantInv.getStackInSlot(i);
						if(!stack.isEmpty() && stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof CropBlock crop)
						{
							var newState = crop.getPlant(level, cropPos);
							controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, stack.copy().split(1)), cropPos, newState, 100, 1);
							return AlgorithmUpdateResult.RETRY;
						}
					}
				}
			} else
			{
				controller.queueBlockHarvest(cropPos, 2);
				return AlgorithmUpdateResult.RETRY;
			}
		}
		
		return AlgorithmUpdateResult.PASS;
	}
	
	@Override
	public boolean tryFertilize(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var cropPos = platform.above(2);
		var cropState = level.getBlockState(cropPos);
		
		if(cropState.getBlock() instanceof BonemealableBlock gr
				&& gr.isValidBonemealTarget(level, cropPos, cropState, level.isClientSide)
				&& gr.isBonemealSuccess(level, level.random, cropPos, cropState))
		{
			gr.performBonemeal(level, level.random, cropPos, cropState);
			level.levelEvent(1505, cropPos, 0);
			return true;
		}
		
		return false;
	}
}