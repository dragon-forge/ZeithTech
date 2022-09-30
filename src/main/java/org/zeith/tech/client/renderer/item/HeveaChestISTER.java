package org.zeith.tech.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.init.BlocksZT;

public class HeveaChestISTER
		extends ZeithTechISTER
{
	public static final HeveaChestISTER HEVEA_CHEST = new HeveaChestISTER();
	
	private final ChestBlockEntity chest = new ChestBlockEntity(BlockPos.ZERO, BlocksZT.HEVEA_CHEST.defaultBlockState());
	private final ChestBlockEntity trappedChest = new TrappedChestBlockEntity(BlockPos.ZERO, BlocksZT.HEVEA_TRAPPED_CHEST.defaultBlockState());
	
	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_108831_, PoseStack p_108832_, MultiBufferSource p_108833_, int p_108834_, int p_108835_)
	{
		Item item = stack.getItem();
		if(item instanceof BlockItem)
		{
			Block block = ((BlockItem) item).getBlock();
			
			BlockState state = block.defaultBlockState();
			BlockEntity entity;
			
			if(state.is(BlocksZT.HEVEA_CHEST))
			{
				entity = this.chest;
			} else if(state.is(BlocksZT.HEVEA_TRAPPED_CHEST))
			{
				entity = this.trappedChest;
			} else return;
			
			this.blockEntRenderDispatcher.renderItem(entity, p_108832_, p_108833_, p_108834_, p_108835_);
		}
	}
}