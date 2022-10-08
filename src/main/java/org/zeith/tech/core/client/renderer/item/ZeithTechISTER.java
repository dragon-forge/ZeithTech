package org.zeith.tech.core.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
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
	
	public void renderAllOverrides(@NotNull ItemStack stack, @NotNull ItemTransforms.@NotNull TransformType transformType, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int uv2, int overlay)
	{
		var mc = Minecraft.getInstance();
		var ir = mc.getItemRenderer();
		
		var isterModel = ir.getModel(stack, mc.level, mc.player, 0);
		var overrides = isterModel.getOverrides().getOverrides();
		
		for(var override : overrides)
		{
			var overridenModel = override.model;
			
			if(overridenModel != null)
			{
				boolean cull;
				if(transformType != ItemTransforms.TransformType.GUI && !transformType.firstPerson() && stack.getItem() instanceof BlockItem bi)
				{
					Block block = bi.getBlock();
					cull = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
				} else cull = true;
				
				for(var model : overridenModel.getRenderPasses(stack, cull))
				{
					for(var type : model.getRenderTypes(stack, cull))
					{
						VertexConsumer vertexconsumer;
						if(cull)
							vertexconsumer = ItemRenderer.getFoilBufferDirect(bufferSource, type, true, stack.hasFoil());
						else
							vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, type, true, stack.hasFoil());
						
						ir.renderModelLists(overridenModel, stack, uv2, overlay, pose, vertexconsumer);
					}
				}
			}
		}
	}
	
	@Override
	public abstract void renderByItem(@NotNull ItemStack stack, @NotNull ItemTransforms.@NotNull TransformType transformType, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int i, int j);
}