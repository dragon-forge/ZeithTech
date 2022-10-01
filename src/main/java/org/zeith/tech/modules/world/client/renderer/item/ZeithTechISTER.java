package org.zeith.tech.modules.world.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ZeithTechISTER
		extends BlockEntityWithoutLevelRenderer
{
	protected final BlockEntityRenderDispatcher blockEntRenderDispatcher;
	
	protected ZeithTechISTER()
	{
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
		this.blockEntRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
	}
	
	protected EntityModelSet getEntityModels()
	{
		return Minecraft.getInstance().getEntityModels();
	}
	
	@Override
	public abstract void renderByItem(@NotNull ItemStack stack, @NotNull ItemTransforms.@NotNull TransformType transformType, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int i, int j);
}