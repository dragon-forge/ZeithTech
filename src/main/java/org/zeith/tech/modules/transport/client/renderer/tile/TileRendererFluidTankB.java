package org.zeith.tech.modules.transport.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.zeith.hammerlib.client.render.FluidRendererHelper;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.hammerlib.client.utils.FluidTextureType;
import org.zeith.tech.core.client.renderer.Cuboid;
import org.zeith.tech.core.client.renderer.CuboidRenderer;
import org.zeith.tech.modules.transport.blocks.fluid_tank.basic.TileFluidTankB;

public class TileRendererFluidTankB
		implements IBESR<TileFluidTankB>
{
	final Cuboid cuboid = new Cuboid();
	
	@Override
	public void render(TileFluidTankB entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		var fluid = entity.tankSmooth.getClientAverage(partial);
		if(!entity.hasLevel())
			fluid = entity.storage.getFluid();
		
		if(!fluid.isEmpty())
		{
			var up = entity.getBlockState().getValue(BlockStateProperties.UP);
			var down = entity.getBlockState().getValue(BlockStateProperties.DOWN);
			
			var hasFluidAbove = false;
			if(entity.hasLevel() && up && entity.getLevel().getBlockEntity(entity.getBlockPos().above()) instanceof TileFluidTankB tank)
				hasFluidAbove = !tank.storage.isEmpty();
			
			float min = down ? 0 : 0.0625F;
			float height = up && down ? 1F : up || down ? 0.9375F : 0.875F;
			
			cuboid.setTexture(FluidRendererHelper.getFluidTexture(fluid, FluidTextureType.STILL));
			lighting = FluidRendererHelper.calculateGlowLight(lighting, fluid);
			
			int argb = FluidRendererHelper.getColorARGB(fluid);
			
			var fluidsSrc = buf.getBuffer(Sheets.translucentCullBlockSheet());
			float fill = fluid.getAmount() / (float) entity.storage.getCapacity();
			
			cuboid.setSideRender(Direction.UP, fill < 1F || !hasFluidAbove);
			cuboid.setSideRender(Direction.DOWN, false);
			
			cuboid.bounds(3F / 16F, min, 3F / 16F, 13F / 16F, min + height * fill, 13F / 16F);
			
			CuboidRenderer.renderCube(cuboid, matrix, fluidsSrc, argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
		}
	}
}