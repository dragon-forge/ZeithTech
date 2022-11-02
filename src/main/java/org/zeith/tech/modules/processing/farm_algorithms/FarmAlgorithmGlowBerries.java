package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.misc.SoundConfiguration;
import org.zeith.tech.api.misc.farm.*;

import java.util.List;

public class FarmAlgorithmGlowBerries
		extends FarmAlgorithm
{
	public FarmAlgorithmGlowBerries()
	{
		super(new Properties()
				.upsideDown(true)
		);
	}
	
	@Override
	public int getColor()
	{
		return 0xF19645;
	}
	
	@Override
	public @NotNull Ingredient getProgrammingItem()
	{
		return Ingredient.of(Items.GLOW_BERRIES);
	}
	
	@Override
	public @NotNull EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack)
	{
		if(stack.is(Items.GLOW_BERRIES)) return EnumFarmItemCategory.PLANT;
		if(stack.is(ItemTags.DIRT)) return EnumFarmItemCategory.SOIL;
		if(stack.is(Items.BONE_MEAL)) return EnumFarmItemCategory.FERTILIZER;
		return EnumFarmItemCategory.UNKNOWN;
	}
	
	@Override
	public AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var dirtPos = platform.below();
		var vinePos = dirtPos.below();
		
		// Place land
		if(!Blocks.CAVE_VINES.defaultBlockState().canSurvive(level, vinePos))
		{
			if(level.isEmptyBlock(dirtPos))
			{
				ItemStack match = new ItemStack(Items.DIRT);
				
				var inv = controller.getInventory(EnumFarmItemCategory.SOIL);
				for(int i = 0; i < inv.getSlots(); ++i)
				{
					var it = inv.getStackInSlot(i);
					if(!it.isEmpty() && categorizeItem(controller, it) == EnumFarmItemCategory.SOIL)
					{
						match = it.copy().split(1);
						break;
					}
				}
				
				if(!match.isEmpty() && match.getItem() instanceof BlockItem bi)
					controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.SOIL, match),
							dirtPos, bi.getBlock().defaultBlockState(), 0, 1);
				
				return AlgorithmUpdateResult.RETRY;
			} else
			{
				controller.queueBlockHarvest(dirtPos, 2);
				return AlgorithmUpdateResult.SUCCESS;
			}
		}
		
		var vineState = level.getBlockState(vinePos);
		
		if(!(vineState.getBlock() instanceof CaveVines))
		{
			controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, new ItemStack(Items.GLOW_BERRIES)),
					vinePos, Blocks.CAVE_VINES.defaultBlockState(), 100, 1);
			
			return AlgorithmUpdateResult.RETRY;
		} else
		{
			BlockPos lowestBerry = null;
			
			var cPos = vinePos;
			BlockState st;
			while((st = level.getBlockState(cPos)).getBlock() instanceof CaveVines)
			{
				if(CaveVines.hasGlowBerries(st))
					lowestBerry = cPos;
				cPos = cPos.below();
			}
			
			if(lowestBerry != null)
			{
				var berryState = level.getBlockState(lowestBerry);
				
				float f = Mth.randomBetween(level.random, 0.8F, 1.2F);
				var sound = new SoundConfiguration(SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, f);
				
				controller.queueBlockTransformation(lowestBerry, berryState, berryState.setValue(CaveVines.BERRIES, false),
						List.of(new ItemStack(Items.GLOW_BERRIES)), sound, 10, 1);
				
				return AlgorithmUpdateResult.RETRY;
			}
		}
		
		return AlgorithmUpdateResult.PASS;
	}
	
	@Override
	public boolean tryFertilize(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		return false;
	}
}
