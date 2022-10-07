package org.zeith.tech.modules.transport.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.tech.core.client.renderer.*;
import org.zeith.tech.modules.transport.blocks.fluid_pipe.BlockFluidPipe;
import org.zeith.tech.modules.transport.blocks.fluid_pipe.TileFluidPipe;

public class TileRendererFluidPipe
		implements IBESR<TileFluidPipe>
{
	static final Direction[] DIRECTIONS = Direction.values();
	
	final Cuboid cuboid = new Cuboid();
	
	@Override
	public void render(TileFluidPipe entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		var fluid = entity.getClientAverage(partial);
		if(!fluid.isEmpty())
		{
			cuboid.setTexture(ZeithTechRenderer.getFluidTexture(fluid, FluidTextureType.STILL));
			
			lighting = ZeithTechRenderer.calculateGlowLight(lighting, fluid);
			int argb = ZeithTechRenderer.getColorARGB(fluid);
			
			var fluidsSrc = buf.getBuffer(Sheets.translucentCullBlockSheet());
			
			float fill = fluid.getAmount() / (float) entity.tank.getCapacity();
			
			Direction prev = null;
			Direction.Axis opposites = null;
			
			for(var dir : DIRECTIONS)
				if(entity.getBlockState().getValue(BlockFluidPipe.DIR2PROP.get(dir)))
				{
					if(prev == null)
						prev = dir;
					else if(prev.getAxis() == dir.getAxis())
						opposites = prev.getAxis();
					else
						opposites = null;
					
					float fillRel = 1F;
					
					var pipe = entity.getRelativeTraversable(dir, fluid).orElse(null);
					if(pipe != null)
					{
						var rel = pipe.getClientAverage(partial);
						float fillRem = rel.getAmount() / (float) pipe.tank.getCapacity();
						fillRel = (fill + fillRem) / 2F;
					}
					
					renderFluid(fluid, argb, dir, fillRel, partial, matrix, fluidsSrc, lighting, overlay);
				}
			
			if(opposites != Direction.Axis.Y)
				renderFluid(fluid, argb, null, fill, partial, matrix, fluidsSrc, lighting, overlay);
		}
	}
	
	public void renderFluid(FluidStack fluid, int argb, @Nullable Direction to, float fill, float partial, PoseStack matrix, VertexConsumer buf, int lighting, int overlay)
	{
		float zero = 0F;
		float one = 1F;
		float thicc = 5.98F / 16;
		float thiccByTwo = thicc / 2;
		float min = 5.01F / 16;
		float max = 10.99F / 16;
		
		if(to == null)
		{
			cuboid.bounds(min, min, min, max, min + thicc * fill, max);
		} else
		{
			cuboid.setSideRender(to, false);
			
			switch(to)
			{
				case DOWN -> cuboid.bounds(0.5F - thiccByTwo * fill, zero, 0.5F - thiccByTwo * fill, 0.5F + thiccByTwo * fill, min, 0.5F + thiccByTwo * fill);
				case UP -> cuboid.bounds(0.5F - thiccByTwo * fill, min, 0.5F - thiccByTwo * fill, 0.5F + thiccByTwo * fill, one, 0.5F + thiccByTwo * fill);
				
				case WEST -> cuboid.bounds(zero, min, min, min, min + thicc * fill, max);
				case EAST -> cuboid.bounds(max, min, min, one, min + thicc * fill, max);
				
				case NORTH -> cuboid.bounds(min, min, zero, max, min + thicc * fill, min);
				case SOUTH -> cuboid.bounds(min, min, max, max, min + thicc * fill, one);
			}
		}
		
		CuboidRenderer.renderCube(cuboid, matrix, buf, argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
		
		if(to != null) cuboid.setSideRender(to, true);
	}
}