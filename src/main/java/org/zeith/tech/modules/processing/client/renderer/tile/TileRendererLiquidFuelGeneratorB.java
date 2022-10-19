package org.zeith.tech.modules.processing.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import org.zeith.hammerlib.client.render.FluidRendererHelper;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.hammerlib.client.utils.FluidTextureType;
import org.zeith.tech.core.client.renderer.*;
import org.zeith.tech.modules.generators.blocks.fuel_generator.liquid.basic.TileLiquidFuelGeneratorB;

public class TileRendererLiquidFuelGeneratorB
		implements IBESR<TileLiquidFuelGeneratorB>
{
	final Cuboid cuboid = new Cuboid();
	
	@Override
	public void render(TileLiquidFuelGeneratorB entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		var fluid = entity.tankSmooth.getClientAverage(partial);
		if(!fluid.isEmpty())
		{
			cuboid.setTexture(FluidRendererHelper.getFluidTexture(fluid, FluidTextureType.STILL));
			lighting = FluidRendererHelper.calculateGlowLight(lighting, fluid);
			
			int argb = FluidRendererHelper.getColorARGB(fluid);
			
			var fluidsSrc = buf.getBuffer(Sheets.translucentCullBlockSheet());
			float fill = fluid.getAmount() / (float) entity.storage.getCapacity();
			
			RotatedRenderHelper.rotateHorizontalPoseStack(matrix, entity.getFront(), 0.51F / 16F, 3.95F / 16F, 0.51F / 16F);
			cuboid.bounds(0, 0, 0, 0.24875F, 8.1F / 16F * fill, 0.24875F);
			
			CuboidRenderer.renderCube(cuboid, matrix, fluidsSrc, argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
		}
	}
}