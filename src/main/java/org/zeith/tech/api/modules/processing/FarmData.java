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
		boolean plant(IFarmController controller, ServerLevel level, BlockPos platform, BlockPos cropPos, ItemStack seedStack);
		
		AlgorithmUpdateResult takeCareOfPlant(IFarmController controller, ServerLevel level, BlockPos platform, BlockPos cropPos, BlockState cropState);
	}
}