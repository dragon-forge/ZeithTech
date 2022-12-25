package org.zeith.tech.core.mixins.compat.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.tech.api.tile.multiblock.IMultiblockHydratesFarmland;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;

@Mixin(RichSoilFarmlandBlock.class)
public class RichSoilFarmlandBlockMixin
{
	@Inject(
			method = "hasWater",
			at = @At("HEAD"),
			cancellable = true,
			remap = false
	)
	private static void hasWater_ZT(LevelReader reader, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
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