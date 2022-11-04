package org.zeith.tech.modules.processing.farm_algorithms.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import org.zeith.tech.api.misc.farm.FarmAlgorithm;
import org.zeith.tech.api.misc.farm.IFarmController;

public abstract class FarmAlgorithmPlantBased
		extends FarmAlgorithm
{
	protected IPlantable plant;
	protected Direction plantDirection = Direction.UP;
	
	public FarmAlgorithmPlantBased(Properties properties, IPlantable plant)
	{
		super(properties);
		this.plant = plant;
	}
	
	public FarmAlgorithmPlantBased(Properties properties, IPlantable plant, Direction plantDirection)
	{
		super(properties);
		this.plant = plant;
		this.plantDirection = plantDirection;
	}
	
	public BlockState getDefaultPlantState()
	{
		return plant.getPlant(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
	}
	
	public boolean canBePlantedHere(ServerLevel level, BlockPos pos)
	{
		return getDefaultPlantState().canSurvive(level, pos);
	}
	
	public boolean isValidSoil(IFarmController controller, BlockPos soilPos, ItemStack item)
	{
		return getStateForPlacement(controller, soilPos, item)
				.map(state -> state.canSustainPlant(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, plantDirection, plant))
				.orElse(false);
	}
	
	public boolean canSustainPlant(IFarmController controller, BlockPos soilPos)
	{
		return controller.getFarmLevel().getBlockState(soilPos)
				.canSustainPlant(controller.getFarmLevel(), soilPos, plantDirection, plant)
				&& getDefaultPlantState().canSurvive(controller.getFarmLevel(), soilPos.relative(plantDirection));
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