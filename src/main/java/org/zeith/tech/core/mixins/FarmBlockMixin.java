package org.zeith.tech.core.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.tech.api.block.IFarmlandBlock;
import org.zeith.tech.api.tile.multiblock.IMultiblockHydratesFarmland;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;

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
	
	@Inject(
			method = "isNearWater",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void isNearWater_ZT(LevelReader reader, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
	{
		for(var check : BlockPos.betweenClosed(pos.offset(-5, 0, -5), pos.offset(5, 1, 5)))
		{
			if(reader.getBlockEntity(check) instanceof TileMultiBlockPart mbp)
			{
				var multiblock = mbp.findMultiBlock();
				if(multiblock.isPresent() && multiblock.orElseThrow() instanceof IMultiblockHydratesFarmland hydrator)
				{
					if(hydrator.doesHydrate(pos))
					{
						cir.setReturnValue(true);
						return;
					}
				}
			}
		}
	}
}