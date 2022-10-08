package org.zeith.tech.modules.processing.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.tech.core.client.renderer.RotatedRenderHelper;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.TileElectricFurnaceB;

public class TileRendererElectricFurnaceB
		implements IBESR<TileElectricFurnaceB>
{
	@Override
	public void render(TileElectricFurnaceB entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		var mc = Minecraft.getInstance();
		var ir = mc.getItemRenderer();
		
		RotatedRenderHelper.rotateHorizontalPoseStack(matrix, entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), 0.5F, 0.2F, 0.15F);
		matrix.mulPose(Vector3f.XP.rotationDegrees(90));
		ir.renderStatic(entity.inputItemDisplay.get(), ItemTransforms.TransformType.GROUND, lighting, overlay, matrix, buf, 0);
	}
}