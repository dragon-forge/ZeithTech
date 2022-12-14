package org.zeith.tech.modules.generators.blocks.fuel_generator.solid.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.generators.blocks.BlockGeneratorBase;

public class BlockSolidFuelGeneratorB
		extends BlockGeneratorBase<TileSolidFuelGeneratorB>
{
	public BlockSolidFuelGeneratorB()
	{
		super(TileSolidFuelGeneratorB.class);
		withDefaultGeneration(20);
	}
	
	@Override
	public @Nullable TileSolidFuelGeneratorB newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileSolidFuelGeneratorB(pos, state)
				.setCurrentGenPerTick(defaultGeneration);
	}
}