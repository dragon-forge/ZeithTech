package org.zeith.tech.api.modules.processing;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.api.misc.farm.AlgorithmUpdateResult;
import org.zeith.tech.api.misc.farm.IFarmController;

import java.util.*;

public class FarmData
{
	private final List<Item> farmlandPlaceables = new ArrayList<>();
	private final Map<Item, ICropSubAlgorithm> cropSubAlgorithmsByItem = new HashMap<>();
	private final Map<Block, ICropSubAlgorithm> cropSubAlgorithmsByBlock = new HashMap<>();
	
	public void addFarmlandPlaceable(Item dirt)
	{
		farmlandPlaceables.add(dirt);
	}
	
	public boolean isFarmlandPlaceable(ItemStack stack)
	{
		return farmlandPlaceables.stream().anyMatch(stack::is);
	}
	
	public void registerCropSubAlgorithm(ICropSubAlgorithm subAlgorithm, List<Item> seeds, List<Block> crops)
	{
		for(var seed : seeds)
			cropSubAlgorithmsByItem.put(seed, subAlgorithm);
		for(var crop : crops)
			cropSubAlgorithmsByBlock.put(crop, subAlgorithm);
	}
	
	public Optional<ICropSubAlgorithm> findCropSubAlgorithm(Item item)
	{
		return Optional.ofNullable(cropSubAlgorithmsByItem.get(item));
	}
	
	public Optional<ICropSubAlgorithm> findCropSubAlgorithm(Block block)
	{
		return Optional.ofNullable(cropSubAlgorithmsByBlock.get(block));
	}
	
	public interface ICropSubAlgorithm
	{
		/**
		 * Plant the given crop at the specified position.
		 *
		 * @param controller
		 * 		the farm controller responsible for managing the farm
		 * @param level
		 * 		the server level where the farm is located
		 * @param platform
		 * 		the position of the platform block in the farm
		 * @param cropPos
		 * 		the position where the crop should be planted
		 * @param seedStack
		 * 		the stack containing the crop item to be planted
		 *
		 * @return true if the planting was successful, false otherwise
		 */
		boolean plant(IFarmController controller, ServerLevel level, BlockPos platform, BlockPos cropPos, ItemStack seedStack);
		
		/**
		 * Take care of the given crop at the specified position.
		 *
		 * @param controller
		 * 		the farm controller responsible for managing the farm
		 * @param level
		 * 		the server level where the farm is located
		 * @param platform
		 * 		the position of the platform block in the farm
		 * @param cropPos
		 * 		the position of the crop to be taken care of
		 * @param cropState
		 * 		the current state of the crop
		 *
		 * @return An {@link AlgorithmUpdateResult} indicating what should the farm do afterwards.
		 */
		AlgorithmUpdateResult takeCareOfPlant(IFarmController controller, ServerLevel level, BlockPos platform, BlockPos cropPos, BlockState cropState);
	}
}