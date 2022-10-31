package org.zeith.tech.api.block.multiblock.blast_furnace;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.api.block.multiblock.base.IMultiBlockElement;

public interface IBlastFurnaceCasingBlock
		extends IMultiBlockElement
{
	BlastFurnaceTier getBlastFurnaceTier(Level level, BlockPos pos, BlockState state);
	
	float getTemperatureLoss(Level level, BlockPos pos, BlockState state);
	
	float getTemperatureReflectivityCoef(Level level, BlockPos pos, BlockState state);
	
	default BlockState crackRandomly(ServerLevel level, BlockPos pos, BlockState state, RandomSource rng)
	{
		return rng.nextInt(getBreakRarity()) == 0 ? getDamagedState(state) : state;
	}
	
	default BlockState getDamagedState(BlockState state)
	{
		return state;
	}
	
	default int getBreakRarity()
	{
		return 10;
	}
	
	enum BlastFurnaceTier
	{
		BASIC,
		ELECTRIC;
	}
}