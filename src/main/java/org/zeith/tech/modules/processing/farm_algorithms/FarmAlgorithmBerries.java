package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.misc.SoundConfiguration;
import org.zeith.tech.api.misc.farm.*;
import org.zeith.tech.api.utils.LazyValue;
import org.zeith.tech.modules.processing.farm_algorithms.base.FarmAlgorithmPlantBased;

import java.util.ArrayList;
import java.util.List;

public class FarmAlgorithmBerries
		extends FarmAlgorithmPlantBased
{
	protected final LazyValue<Ingredient> item = LazyValue.of(() ->
	{
		List<Item> items = new ArrayList<>();
		for(Item item : ForgeRegistries.ITEMS)
			if(item instanceof BlockItem ib && ib.getBlock() instanceof SweetBerryBushBlock)
				items.add(item);
		return Ingredient.of(items.toArray(Item[]::new));
	});
	
	public FarmAlgorithmBerries()
	{
		super(new Properties()
						.upsideDown(false),
				(SweetBerryBushBlock) Blocks.SWEET_BERRY_BUSH,
				Direction.UP
		);
	}
	
	@Override
	public int getColor()
	{
		return 0x7F0000;
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
		if(getProgrammingItem().test(stack)) return EnumFarmItemCategory.PLANT;
		if(isValidSoil(controller, controller.getFarmPosition(), stack)) return EnumFarmItemCategory.SOIL;
		return EnumFarmItemCategory.UNKNOWN;
	}
	
	@Override
	public AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var dirtPos = platform.above();
		
		var cropPos = dirtPos.above();
		var cropState = level.getBlockState(cropPos);
		
		var hasSoil = false;
		
		if(level.isEmptyBlock(dirtPos))
		{
			var plantInv = controller.getInventory(EnumFarmItemCategory.SOIL);
			for(int i = 0; i < plantInv.getSlots(); ++i)
			{
				var stack = plantInv.getStackInSlot(i);
				var stateOpt = getStateForPlacement(controller, dirtPos, stack);
				var newState = stateOpt.orElse(null);
				if(newState != null)
				{
					controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.SOIL, stack.copy().split(1)), dirtPos, newState, 0, 1);
					return AlgorithmUpdateResult.RETRY;
				}
			}
		} else if(canSustainPlant(controller, dirtPos))
		{
			hasSoil = true;
		} else if(!level.isEmptyBlock(dirtPos))
		{
			controller.queueBlockHarvest(dirtPos, 2);
			return AlgorithmUpdateResult.RETRY;
		}
		
		if(cropState.getBlock() instanceof SweetBerryBushBlock)
		{
			if(cropState.getValue(SweetBerryBushBlock.AGE) == 3)
			{
				int j = 1 + level.random.nextInt(2);
				var drops = List.of(new ItemStack(Items.SWEET_BERRIES, j + 1));
				
				controller.queueBlockTransformation(cropPos, cropState, cropState.setValue(SweetBerryBushBlock.AGE, 0), drops,
						new SoundConfiguration(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F),
						5, 5);
				
				return AlgorithmUpdateResult.SUCCESS;
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
						if(!stack.isEmpty() && stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SweetBerryBushBlock bush)
						{
							var newState = bush.getPlant(level, cropPos);
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
}