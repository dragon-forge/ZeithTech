package org.zeith.tech.modules.generators.client.renderer.tile;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.zeith.hammerlib.client.render.FluidRendererHelper;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.hammerlib.client.utils.FluidTextureType;
import org.zeith.tech.core.client.renderer.*;
import org.zeith.tech.modules.generators.blocks.magmatic.TileMagmaticGenerator;

public class TileRendererMagmaticGenerator
		implements IBESR<TileMagmaticGenerator>
{
	final Cuboid cuboid = new Cuboid();
	
	@Override
	public void render(TileMagmaticGenerator entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		RotatedRenderHelper.rotateHorizontalPoseStack(matrix, entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), 0F, 0F, 0F);
		
		var fluid = entity.storageSmooth.getClientAverage(partial);
		if(!fluid.isEmpty())
		{
			cuboid.setTexture(FluidRendererHelper.getFluidTexture(fluid, FluidTextureType.STILL));
			lighting = FluidRendererHelper.calculateGlowLight(lighting, fluid);
			
			int argb = FluidRendererHelper.getColorARGB(fluid);
			
			var fluidsSrc = buf.getBuffer(Sheets.translucentCullBlockSheet());
			float fill = fluid.getAmount() / (float) entity.storage.getCapacity();
			
			cuboid.bounds(2.01F / 16F, 4 / 16F, 9.01F / 16F, 6.98F / 16F, 4 / 16F + 11.98F / 16F * fill, 13.98F / 16F);
			
			CuboidRenderer.renderCube(cuboid, matrix, fluidsSrc, argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
		}
		
		fluid = entity.coolantSmooth.getClientAverage(partial);
		if(!fluid.isEmpty())
		{
			cuboid.setTexture(FluidRendererHelper.getFluidTexture(fluid, FluidTextureType.STILL));
			lighting = FluidRendererHelper.calculateGlowLight(lighting, fluid);
			
			int argb = FluidRendererHelper.getColorARGB(fluid);
			
			var fluidsSrc = buf.getBuffer(Sheets.translucentCullBlockSheet());
			float fill = fluid.getAmount() / (float) entity.coolant.getCapacity();
			
			cuboid.bounds(1.01F / 16F, 4 / 16F, 1.01F / 16F, 8 / 16F, 4 / 16F + 11.98F / 16F * fill, 7.98F / 16F);
			cuboid.setSideRender(Direction.EAST, false);
			CuboidRenderer.renderCube(cuboid, matrix, fluidsSrc, argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
			
			cuboid.bounds(8 / 16F, 4 / 16F, 1.01F / 16F, 14.98F / 16F, 4 / 16F + 11.98F / 16F * fill, 8 / 16F);
			cuboid.setSideRender(Direction.EAST, true);
			cuboid.setSideRender(Direction.WEST, false);
			cuboid.setSideRender(Direction.SOUTH, false);
			CuboidRenderer.renderCube(cuboid, matrix, fluidsSrc, argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
			
			cuboid.bounds(8.01F / 16F, 4 / 16F, 8 / 16F, 14.98F / 16F, 4 / 16F + 11.98F / 16F * fill, 14.98F / 16F);
			cuboid.setSideRender(Direction.WEST, true);
			cuboid.setSideRender(Direction.SOUTH, true);
			cuboid.setSideRender(Direction.NORTH, false);
			CuboidRenderer.renderCube(cuboid, matrix, fluidsSrc, argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
			
			cuboid.setSideRender(Predicates.alwaysTrue());
		}
	}
}