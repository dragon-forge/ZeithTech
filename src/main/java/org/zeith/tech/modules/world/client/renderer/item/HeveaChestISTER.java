package org.zeith.tech.modules.world.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.core.client.renderer.item.ZeithTechISTER;
import org.zeith.tech.modules.world.init.BlocksZT_World;

public class HeveaChestISTER
		extends ZeithTechISTER
{
	public static final HeveaChestISTER HEVEA_CHEST = new HeveaChestISTER();
	
	private final ChestBlockEntity chest = new ChestBlockEntity(BlockPos.ZERO, BlocksZT_World.HEVEA_CHEST.defaultBlockState());
	private final ChestBlockEntity trappedChest = new TrappedChestBlockEntity(BlockPos.ZERO, BlocksZT_World.HEVEA_TRAPPED_CHEST.defaultBlockState());
	
	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack pose, MultiBufferSource bufferSource, int uv2, int overlay)
	{
		if(stack.getItem() instanceof BlockItem ib)
		{
			Block block = ib.getBlock();
			
			BlockState state = block.defaultBlockState();
			BlockEntity entity;
			
			if(state.is(BlocksZT_World.HEVEA_CHEST))
			{
				entity = this.chest;
			} else if(state.is(BlocksZT_World.HEVEA_TRAPPED_CHEST))
			{
				entity = this.trappedChest;
			} else return;
			
			this.blockEntRenderDispatcher.renderItem(entity, pose, bufferSource, uv2, overlay);
		}
	}
}