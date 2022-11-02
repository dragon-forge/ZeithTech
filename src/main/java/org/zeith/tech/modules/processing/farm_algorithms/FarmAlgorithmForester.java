package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.misc.farm.*;
import org.zeith.tech.api.utils.LazyValue;
import org.zeith.tech.modules.processing.blocks.farm.actions.BreakBlockAction;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FarmAlgorithmForester
		extends FarmAlgorithm
{
	public FarmAlgorithmForester()
	{
		super(new Properties()
				.upsideDown(false)
		);
	}
	
	protected final LazyValue<Ingredient> saplings = LazyValue.of(() ->
	{
		List<Item> items = new ArrayList<>();
		for(Item item : ForgeRegistries.ITEMS)
			if(item instanceof BlockItem ib && ib.getBlock() instanceof SaplingBlock)
				items.add(item);
		return Ingredient.of(items.toArray(Item[]::new));
	});
	
	@Override
	public int getColor()
	{
		return 0xE0BE91;
	}
	
	@Override
	public @NotNull Ingredient getProgrammingItem()
	{
		return saplings.getValue();
	}
	
	@Override
	public @NotNull EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack)
	{
		if(stack.getItem() instanceof BlockItem bi)
		{
			if(bi.getBlock() instanceof SaplingBlock)
				return EnumFarmItemCategory.PLANT;
			var state = bi.getBlock().defaultBlockState();
			if(state.canSustainPlant(controller.getFarmLevel(), controller.getFarmPosition(), Direction.UP, (SaplingBlock) Blocks.OAK_SAPLING))
				return EnumFarmItemCategory.SOIL;
		}
		
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
		
		if(level.isEmptyBlock(dirtPos))
		{
			var plantInv = controller.getInventory(EnumFarmItemCategory.SOIL);
			for(int i = 0; i < plantInv.getSlots(); ++i)
			{
				var stack = plantInv.getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() instanceof BlockItem bi)
				{
					var newState = bi.getBlock().defaultBlockState();
					
					if(newState.canSustainPlant(level, dirtPos, Direction.UP, (SaplingBlock) Blocks.OAK_SAPLING))
					{
						controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.SOIL, stack.copy().split(1)), dirtPos, newState, 0, 1);
						return AlgorithmUpdateResult.RETRY;
					}
				}
			}
		} else if(dirtState.canSustainPlant(level, dirtPos, Direction.UP, (SaplingBlock) Blocks.OAK_SAPLING))
		{
			hasSoil = true;
		} else if(!level.isEmptyBlock(dirtPos) && level.isEmptyBlock(cropPos))
		{
			controller.queueBlockHarvest(dirtPos, 2);
			return AlgorithmUpdateResult.RETRY;
		}
		
		if(cropState.getBlock() instanceof SaplingBlock)
			return AlgorithmUpdateResult.PASS;
		
		if(level.isEmptyBlock(cropPos))
		{
			if(hasSoil)
			{
				var plantInv = controller.getInventory(EnumFarmItemCategory.PLANT);
				for(int i = 0; i < plantInv.getSlots(); ++i)
				{
					var stack = plantInv.getStackInSlot(i);
					if(!stack.isEmpty() && stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SaplingBlock sapling)
					{
						var newState = sapling.getPlant(level, cropPos);
						controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, stack.copy().split(1)), cropPos, newState, 100, 1);
						return AlgorithmUpdateResult.RETRY;
					}
				}
			}
		} else
		{
			List<BlockPos> treePositions = new ArrayList<>();
			List<BlockPos> leafPositions = new ArrayList<>();
			
			List<BlockPos> discoverPositions = new ArrayList<>();
			discoverPositions.add(cropPos);
			
			for(int i = 0; i < discoverPositions.size(); i++)
			{
				var pos = discoverPositions.get(i);
				var state = level.getBlockState(pos);
				
				if(state.is(BlockTags.LOGS))
					treePositions.add(pos);
				else if(state.is(BlockTags.LEAVES))
					leafPositions.add(pos);
				else continue;
				
				for(BlockPos opos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1)))
				{
					if(discoverPositions.contains(opos)) continue;
					discoverPositions.add(opos.immutable());
				}
			}
			
			leafPositions.sort(Comparator.comparingDouble(controller.getFarmPosition()::distSqr));
			treePositions.sort(Comparator.comparingDouble(controller.getFarmPosition()::distSqr));
			
			int sp = treePositions.size();
			
			controller.queueMultipleBlockHarvests(Stream.concat(
					IntStream.range(0, leafPositions.size())
							.mapToObj(i -> new BreakBlockAction(leafPositions.get(i), sp + i)),
					IntStream.range(0, sp)
							.mapToObj(i -> new BreakBlockAction(treePositions.get(i), i))
			).toList());
			
			return AlgorithmUpdateResult.RETRY;
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