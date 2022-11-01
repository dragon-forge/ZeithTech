package org.zeith.tech.core.mixins;

import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.*;
import org.zeith.tech.api.block.IFarmlandBlock;

@Mixin(FarmBlock.class)
@Implements({
		@Interface(iface = IFarmlandBlock.class, prefix = "zt$")
})
public class FarmBlockMixin
{
	@Shadow
	@Final
	public static int MAX_MOISTURE;
	
	@Shadow
	@Final
	public static IntegerProperty MOISTURE;
	
	public int zt$getMaxMoistLevel()
	{
		return MAX_MOISTURE;
	}
	
	public int zt$getMoistLevel(BlockState state)
	{
		return state.getValue(MOISTURE);
	}
	
	public BlockState zt$withMoistLevel(BlockState state, int moistLevel)
	{
		return state.setValue(MOISTURE, moistLevel);
	}
}