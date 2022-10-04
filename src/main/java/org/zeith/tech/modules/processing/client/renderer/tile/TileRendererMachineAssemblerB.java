package org.zeith.tech.modules.processing.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.TileMachineAssemblerB;
import org.zeith.tech.modules.shared.client.renderer.RotatedRenderHelper;

public class TileRendererMachineAssemblerB
		implements IBESR<TileMachineAssemblerB>
{
	@Override
	public void render(TileMachineAssemblerB entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		var mc = Minecraft.getInstance();
		var ir = mc.getItemRenderer();
		
		RotatedRenderHelper.rotateHorizontalPoseStack(matrix, entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), 0.5F, 0.90625F, 0.44F);
		
		matrix.mulPose(Vector3f.XP.rotationDegrees(90F));
		
		matrix.scale(0.35F, 0.35F, 0.35F);
		
		float distance = 1 / 1.8F;
		
		if(!entity.craftResult.get().isEmpty() && entity.craftTime.get() > 0)
			distance *= 1F - entity.getProgress(partial) / (float) entity.craftTime.getInt();
		
		for(int x = 0; x < 5; ++x)
			for(int y = 0; y < 5; ++y)
			{
				var stack = entity.craftingInventory.getItem(x + y * 5);
				
				int xr = x - 2, yr = y - 2;
				
				matrix.translate(-xr * distance, -yr * distance, -0.001);
				ir.renderStatic(stack, ItemTransforms.TransformType.GROUND, lighting, overlay, matrix, buf, 0);
				matrix.translate(xr * distance, yr * distance, 0);
			}
	}
}