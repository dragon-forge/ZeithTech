package org.zeith.tech.modules.processing.farm_algorithms;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.misc.farm.*;
import org.zeith.tech.api.utils.LazyValue;

import java.util.*;

public class FarmAlgorithmChorus
		extends FarmAlgorithm
{
	protected final LazyValue<Ingredient> item = LazyValue.of(() -> Ingredient.of(Items.CHORUS_FLOWER));
	
	public FarmAlgorithmChorus()
	{
		super(new Properties()
				.upsideDown(false)
		);
	}
	
	@Override
	public int getColor()
	{
		return 0x6F4665;
	}
	
	protected boolean isChorusPosition(BlockPos delta)
	{
		int x = delta.getX(), z = delta.getZ();
		return x % 4 == 0 && z % 4 == 0;
	}
	
	@Override
	public @NotNull Ingredient getProgrammingItem()
	{
		return item.getValue();
	}
	
	@Override
	public @NotNull EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack)
	{
		if(getProgrammingItem().test(stack)) return EnumFarmItemCategory.PLANT;
		if(stack.is(Items.END_STONE)) return EnumFarmItemCategory.SOIL;
		return EnumFarmItemCategory.UNKNOWN;
	}
	
	@Override
	public AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform)
	{
		var stonePos = platform.above();
		var stoneState = level.getBlockState(stonePos);
		
		// Place end stone
		if(!stoneState.is(Blocks.END_STONE))
		{
			if(level.isEmptyBlock(stonePos))
			{
				ItemStack match = new ItemStack(Items.END_STONE);
				
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
							stonePos, bi.getBlock().defaultBlockState(), 0, 1);
				
			} else controller.queueBlockHarvest(stonePos, 2);
			
			return AlgorithmUpdateResult.RETRY;
		}
		
		var delta = platform.subtract(controller.getFarmPosition().offset(-6, 0, -6));
		
		if(isChorusPosition(delta))
		{
			var chorusPos = stonePos.above();
			
			if(level.isEmptyBlock(chorusPos))
			{
				controller.queueBlockPlacement(controller.createItemConsumer(EnumFarmItemCategory.PLANT, new ItemStack(Items.CHORUS_FLOWER)),
						chorusPos, Blocks.CHORUS_FLOWER.defaultBlockState(), 0, 0);
				
				return AlgorithmUpdateResult.SUCCESS;
			} else if(level.getBlockState(chorusPos).is(Blocks.CHORUS_PLANT))
			{
				List<BlockPos> flowers = new ArrayList<>();
				List<BlockPos> deadFlowers = new ArrayList<>();
				List<BlockPos> plants = new ArrayList<>();
				plants.add(chorusPos);
				
				for(int i = 0; i < plants.size(); i++)
				{
					var ppos = plants.get(i);
					var pstate = level.getBlockState(ppos);
					
					if(!pstate.is(Blocks.CHORUS_PLANT))
					{
						plants.remove(i);
						--i;
						continue;
					}
					
					for(var prop : PipeBlock.PROPERTY_BY_DIRECTION.entrySet())
						if(pstate.getValue(prop.getValue()))
						{
							var dir = prop.getKey();
							var opos = ppos.relative(dir);
							if(!plants.contains(opos))
							{
								var ostate = level.getBlockState(opos);
								if(ostate.is(Blocks.CHORUS_PLANT)) plants.add(opos);
								else if(ostate.is(Blocks.CHORUS_FLOWER))
								{
									var age = ostate.getValue(ChorusFlowerBlock.AGE);
									flowers.add(opos);
									if(age > 4) deadFlowers.add(opos);
								}
							}
						}
				}
				
				if(flowers.size() == deadFlowers.size())
				{
					Collections.reverse(plants); // make the plant order go from up to down.
					flowers.addAll(plants);
					for(int i = 0; i < flowers.size(); i++)
						controller.queueBlockHarvest(flowers.get(i), flowers.size() - i);
					
					return AlgorithmUpdateResult.SUCCESS;
				}
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