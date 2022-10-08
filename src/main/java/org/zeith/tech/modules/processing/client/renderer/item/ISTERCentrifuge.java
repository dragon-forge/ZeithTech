package org.zeith.tech.modules.processing.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.core.client.renderer.item.ZeithTechISTER;
import org.zeith.tech.modules.processing.blocks.fluid_centrifuge.TileFluidCentrifuge;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;

public class ISTERCentrifuge
		extends ZeithTechISTER
{
	public static final ISTERCentrifuge INSTANCE = new ISTERCentrifuge();
	
	private final TileFluidCentrifuge centrifuge = new TileFluidCentrifuge(BlockPos.ZERO, BlocksZT_Processing.FLUID_CENTRIFUGE.defaultBlockState());
	
	@Override
	public void renderByItem(@NotNull ItemStack stack, ItemTransforms.@NotNull TransformType transformType, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int uv2, int overlay)
	{
		if(stack.getItem() instanceof BlockItem ib)
		{
			Block block = ib.getBlock();
			
			BlockState state = block.defaultBlockState();
			BlockEntity entity;
			
			if(state.is(BlocksZT_Processing.FLUID_CENTRIFUGE))
			{
				entity = this.centrifuge;
			} else return;
			
			pose.pushPose();
			this.blockEntRenderDispatcher.renderItem(entity, pose, bufferSource, uv2, overlay);
			pose.popPose();
			
			// Apply base item model
			renderAllOverrides(stack, transformType, pose, bufferSource, uv2, overlay);
		}
	}
}