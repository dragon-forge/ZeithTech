package org.zeith.tech.modules.processing.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.TileMachineAssemblerB;

public class TileRendererMachineAssemblerB
		implements IBESR<TileMachineAssemblerB>
{
	@Override
	public void render(TileMachineAssemblerB entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		var mc = Minecraft.getInstance();
		var ir = mc.getItemRenderer();
		
		switch(entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))
		{
			case NORTH ->
			{
				matrix.translate(0.5, 0.90625, 0.44);
			}
			case SOUTH ->
			{
				matrix.translate(0.5, 0.90625, 0.56);
				matrix.mulPose(Vector3f.YP.rotationDegrees(180F));
			}
			case WEST ->
			{
				matrix.translate(0.44, 0.90625, 0.5);
				matrix.mulPose(Vector3f.YP.rotationDegrees(90F));
			}
			case EAST ->
			{
				matrix.translate(0.56, 0.90625, 0.5);
				matrix.mulPose(Vector3f.YP.rotationDegrees(270F));
			}
		}
		
		matrix.mulPose(Vector3f.XP.rotationDegrees(90F));
		
		matrix.scale(0.35F, 0.35F, 0.35F);
		
		float distance = 1 / 1.8F;
		
		if(entity.getActiveRecipe() != null && entity.craftTime.get() > 0)
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