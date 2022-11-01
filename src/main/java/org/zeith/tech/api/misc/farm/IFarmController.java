package org.zeith.tech.api.misc.farm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public interface IFarmController
{
	@Nullable
	FarmAlgorithm getAlgorithm();
	
	Level getFarmLevel();
	
	BlockPos getFarmPosition();
	
	FluidTank getWaterInventory();
	
	boolean hasPlatform(BlockPos platform);
	
	IItemHandlerModifiable getInventory(EnumFarmItemCategory category);
	
	default FarmItemConsumer createItemConsumer(EnumFarmItemCategory category, Ingredient item, int amount)
	{
		return new FarmItemConsumer(category, item, amount);
	}
	
	default FarmItemConsumer createItemConsumer(EnumFarmItemCategory category, ItemStack item)
	{
		if(item.isEmpty()) return createItemConsumer(category, Ingredient.EMPTY, 0);
		return createItemConsumer(category, Ingredient.of(item.copy().split(1)), item.getCount());
	}
	
	void queueBlockPlacement(FarmItemConsumer itemConsumer, BlockPos pos, BlockState toPlace, int waterUsage, int priority);
	
	void queueBlockHarvest(BlockPos pos, int priority);
}