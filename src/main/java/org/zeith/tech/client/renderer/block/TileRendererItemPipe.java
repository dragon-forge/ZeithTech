package org.zeith.tech.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.tech.common.blocks.item_pipe.ItemInPipe;
import org.zeith.tech.common.blocks.item_pipe.TileItemPipe;

public class TileRendererItemPipe
		implements IBESR<TileItemPipe>
{
	private final Minecraft mc = Minecraft.getInstance();
	
	@Override
	public void render(TileItemPipe entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		for(ItemInPipe item : entity.contents.getAll())
		{
			var pos = item.getCurrentPosition(partial).subtract(Vec3.atLowerCornerOf(entity.getPosition()));
			var stack = item.getContents();
			
			matrix.pushPose();
			matrix.translate(pos.x, pos.y, pos.z);
			matrix.scale(0.25F, 0.25F, 0.25F);
			matrix.mulPose(Vector3f.YP.rotationDegrees(item.itemId.getMostSignificantBits() / 3F));
			mc.getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, lighting, overlay, matrix, buf, 0);
			matrix.popPose();
		}
	}
}