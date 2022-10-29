package org.zeith.tech.core.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.tile.ILoadableFromItem;

import java.util.*;

public class BlockItemWithAltISTER
		extends ZeithTechISTER
		implements IClientItemExtensions
{
	public static final BlockItemWithAltISTER INSTANCE = new BlockItemWithAltISTER();
	
	protected final Map<Block, BlockEntity> map = new HashMap<>();
	
	public BlockItemWithAltISTER()
	{
	}
	
	public Optional<BlockItemWithAltISTER> bind(Block block, BlockEntity tile)
	{
		if(tile != null) map.put(block, tile);
		return Optional.ofNullable(tile != null ? this : null);
	}
	
	public Optional<BlockItemWithAltISTER> bind(Block block, BlockEntityType<?> type)
	{
		return bind(block, type.create(BlockPos.ZERO, block.defaultBlockState()));
	}
	
	@Override
	public BlockEntityWithoutLevelRenderer getCustomRenderer()
	{
		return this;
	}
	
	@Override
	public void renderByItem(@NotNull ItemStack stack, ItemTransforms.@NotNull TransformType transformType, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int uv2, int overlay)
	{
		if(stack.getItem() instanceof BlockItem ib)
		{
			Block block = ib.getBlock();
			
			BlockEntity entity;
			
			if(map.containsKey(block))
				entity = map.get(block);
			else return;
			
			// Force-load the tile to make sure it is displayed as if it was placed in-world.
			if(entity instanceof ILoadableFromItem loadable)
				loadable.loadFromItem(stack);
			
			pose.pushPose();
			Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(entity, pose, bufferSource, uv2, overlay);
			pose.popPose();
		}
		
		// Apply base item model
		renderAllOverrides(stack, transformType, pose, bufferSource, uv2, overlay);
	}
}