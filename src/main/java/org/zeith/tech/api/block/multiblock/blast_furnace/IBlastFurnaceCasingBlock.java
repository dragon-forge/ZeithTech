package org.zeith.tech.api.block.multiblock.blast_furnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.api.block.multiblock.base.IMultiBlockElement;

public interface IBlastFurnaceCasingBlock
		extends IMultiBlockElement
{
	BlastFurnaceTier getBlastFurnaceTier(Level level, BlockPos pos, BlockState state);
	
	float getTemperatureLoss(Level level, BlockPos pos, BlockState state);
	
	float getTemperatureReflectivityCoef(Level level, BlockPos pos, BlockState state);
	
	enum BlastFurnaceTier
	{
		BASIC,
		ELECTRIC;
	}
}