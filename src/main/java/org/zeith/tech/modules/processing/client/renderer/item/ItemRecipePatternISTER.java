package org.zeith.tech.modules.processing.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.api.crafting.impl.ItemStackResult;
import org.zeith.tech.api.item.IRecipePatternItem;
import org.zeith.tech.core.client.renderer.item.ZeithTechISTER;

public class ItemRecipePatternISTER
		extends ZeithTechISTER
{
	public static final ItemRecipePatternISTER INSTANCE = new ItemRecipePatternISTER();
	
	@Override
	public void renderByItem(@NotNull ItemStack stack, ItemTransforms.@NotNull TransformType transformType, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int uv2, int overlay)
	{
		if(stack.getItem() instanceof IRecipePatternItem pat && (transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.FIXED))
		{
			var recipe = pat.getProvidedRecipe(stack);
			if(recipe != null && recipe.getResult() instanceof ItemStackResult res)
			{
				var recipeOut = res.getBaseOutput();
				
				pose.pushPose();
				pose.translate(13.5F / 16F, 2.5F / 16F, 0);
				pose.scale(0.25F, 0.25F, 0.5F);
				
				var mc = Minecraft.getInstance();
				var ir = mc.getItemRenderer();
				ir.renderStatic(recipeOut, ItemTransforms.TransformType.GUI, uv2, overlay, pose, bufferSource, 0);
				
				pose.popPose();
			}
		}
		
		// Apply base item model
		renderAllOverrides(stack, transformType, pose, bufferSource, uv2, overlay);
	}
}