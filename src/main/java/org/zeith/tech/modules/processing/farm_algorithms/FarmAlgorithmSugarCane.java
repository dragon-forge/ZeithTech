package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.misc.farm.*;
import org.zeith.tech.api.utils.LazyValue;
import org.zeith.tech.modules.processing.farm_algorithms.base.FarmAlgorithmPlantBased;
import org.zeith.tech.modules.shared.init.BlocksZT;

public class FarmAlgorithmSugarCane
		extends FarmAlgorithmPlantBased
{
	
	protected final LazyValue<Ingredient> item = LazyValue.of(() -> Ingredient.of(Items.SUGAR_CANE));
	
	public FarmAlgorithmSugarCane()
	{
		super(new Properties()
						.upsideDown(false),
				(SugarCaneBlock) Blocks.SUGAR_CANE,
				Direction.UP
		);
	}
	
	@Override
	public int getColor()
	{
		return 0x92DC6D;
	}
	
	@Override
	public @NotNull Ingredient getProgrammingItem()
	{
		return item.getValue();
	}
	
	protected boolean isWaterPosition(BlockPos delta)
	{
		int x = delta.getX(), z = delta.getZ();
		return (x + z * 2) % 5 == 1;
	}
	
	@Override
	public @NotNull EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack)
	{
		if(stack.is(Items.SAND) || stack.is(Items.RED_SAND) || stack.is(Items.DIRT) || stack.is(Items.GRASS_BLOCK)) return EnumFarmItemCategory.SOIL;
		if(stack.is(BlocksZT.PLASTIC_CASING.asItem())) return EnumFarmItemCategory.SOIL;
		if(getProgrammingItem().test(stack)) return EnumFarmItemCategory.PLANT;
		return EnumFarmItemCategory.UNKNOWN;
	}
	
	@Override
	public AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var delta = platform.subtract(controller.getFarmPosition().offset(-6, 0, -6));
		
		var soilOrWaterPos = platform.above();
		
		if(isWaterPosition(delta))
		{
			if(level.isEmptyBlock(soilOrWaterPos))
			{
				var tank = controller.getWaterInventory();
				if(tank.getFluidAmount() >= FluidType.BUCKET_VOLUME)
				{
					var fstate = tank.getFluid().getFluid().defaultFluidState();
					if(fstate.is(FluidTags.WATER))
					{
						var state = fstate.createLegacyBlock();
						if(state.getMaterial().isLiquid())
						{
							tank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
							level.setBlockAndUpdate(soilOrWaterPos, state);
						}
					}
				}
				
				return AlgorithmUpdateResult.RETRY;
			}
			
			var state = level.getBlockState(soilOrWaterPos);
			if(!level.getFluidState(soilOrWaterPos).is(FluidTags.WATER))
			{
				if(state.getBlock() instanceof LiquidBlockContainer container)
				{
					var tank = controller.getWaterInventory();
					if(tank.getFluidAmount() >= FluidType.BUCKET_VOLUME && container.canPlaceLiquid(level, soilOrWaterPos, state, tank.getFluid().getFluid()))
					{
						if(container.placeLiquid(level, soilOrWaterPos, state, tank.getFluid().getFluid().defaultFluidState()))
						{
							tank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
						}
					}
				} else
				{
					controller.queueBlockHarvest(soilOrWaterPos, 2);
					return AlgorithmUpdateResult.RETRY;
				}
				
				return AlgorithmUpdateResult.RETRY;
			} else if(!state.is(BlocksZT.PLASTIC_CASING))
			{
				var plantInv = controller.getInventory(EnumFarmItemCategory.SOIL);
				for(int i = 0; i < plantInv.getSlots(); ++i)
				{
					var stack = plantInv.getStackInSlot(i);
					if(stack.is(BlocksZT.PLASTIC_CASING.asItem()))
					{
						var stateOpt = getStateForPlacement(controller, soilOrWaterPos, stack);
						var newState = stateOpt.orElse(null);
						if(newState != null)
						{
							controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.SOIL, stack.copy().split(1)), soilOrWaterPos, newState, 0, 1);
							return AlgorithmUpdateResult.RETRY;
						}
					}
				}
			}
		} else
		{
			var hasSoil = false;
			
			var state = level.getBlockState(soilOrWaterPos);
			
			if(level.isEmptyBlock(soilOrWaterPos) || state.getBlock() instanceof LiquidBlock)
			{
				var plantInv = controller.getInventory(EnumFarmItemCategory.SOIL);
				for(int i = 0; i < plantInv.getSlots(); ++i)
				{
					var stack = plantInv.getStackInSlot(i);
					var stateOpt = getStateForPlacement(controller, soilOrWaterPos, stack);
					var newState = stateOpt.orElse(null);
					if(newState != null)
					{
						controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.SOIL, stack.copy().split(1)), soilOrWaterPos, newState, 0, 1);
						return AlgorithmUpdateResult.RETRY;
					}
				}
			} else if(state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK))
			{
				hasSoil = true;
			} else if(!level.isEmptyBlock(soilOrWaterPos) && !(state.getBlock() instanceof LiquidBlock))
			{
				controller.queueBlockHarvest(soilOrWaterPos, 2);
				return AlgorithmUpdateResult.RETRY;
			}
			
			var canePos = soilOrWaterPos.above();
			
			if(hasSoil)
			{
				if(!level.getBlockState(canePos).is(getDefaultPlantState().getBlock()))
				{
					if(level.isEmptyBlock(canePos))
						controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, getProgrammingItem(), 1),
								canePos, getDefaultPlantState(), 100, 0);
					else
					{
						controller.queueBlockHarvest(canePos, 2);
						return AlgorithmUpdateResult.RETRY;
					}
				} else
				{
					var maxCane = canePos;
					
					int harvest = 0;
					
					var grownCane = canePos.above();
					while(true)
					{
						if(level.getBlockState(grownCane).is(Blocks.SUGAR_CANE))
						{
							++harvest;
							maxCane = grownCane;
							grownCane = grownCane.above();
							continue;
						}
						
						break;
					}
					
					// We have found more bamboo above the original one, might as well harvest it.
					if(maxCane.getY() > canePos.getY())
					{
						controller.queueBlockHarvest(maxCane, 0);
						return harvest > 1 ? AlgorithmUpdateResult.RETRY : AlgorithmUpdateResult.SUCCESS;
					}
				}
			}
		}
		
		return AlgorithmUpdateResult.PASS;
	}
}