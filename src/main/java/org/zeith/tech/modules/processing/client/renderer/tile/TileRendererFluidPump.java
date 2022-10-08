package org.zeith.tech.modules.processing.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.hammerlib.util.colors.ColorHelper;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.*;
import org.zeith.tech.modules.processing.blocks.fluid_pump.TileFluidPump;

public class TileRendererFluidPump
		implements IBESR<TileFluidPump>
{
	final ModelPart animatedPart;
	
	final Cuboid cuboid = new Cuboid();
	
	public TileRendererFluidPump()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();
		
		PartDefinition anim = part.addOrReplaceChild("anim",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-15.0F, -13.0F, 1.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));
		
		animatedPart = LayerDefinition.create(mesh, 64, 64).bakeRoot();
	}
	
	private static final RenderType CUTOUT_BLOCK = RenderType.entityCutout(new ResourceLocation(ZeithTech.MOD_ID, "textures/processing/block/fluid_pump/anim.png"));
	
	@Override
	public void render(TileFluidPump entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		double progress = (Mth.cos(entity.rotator.getActualRotation(partial) * 0.017453292519943295F) + 1F) / 2F;
		
		matrix.pushPose();
		matrix.translate(1, 1.5 - 0.5625 * progress, 1);
		matrix.mulPose(Vector3f.XP.rotationDegrees(180));
		animatedPart.render(matrix, buf.getBuffer(CUTOUT_BLOCK), lighting, overlay, 1F, 1F, 1F, 1F);
		matrix.popPose();
		
		var fluid = entity.tankSmooth.getClientAverage(partial);
		if(!fluid.isEmpty())
		{
			cuboid.setTexture(ZeithTechRenderer.getFluidTexture(fluid, FluidTextureType.STILL));
			lighting = ZeithTechRenderer.calculateGlowLight(lighting, fluid);
			
			int argb = ZeithTechRenderer.getColorARGB(fluid);
			
			argb = ColorHelper.packARGB(ColorHelper.getAlpha(argb) * 0.98F, ColorHelper.getRed(argb), ColorHelper.getGreen(argb), ColorHelper.getBlue(argb));
			
			var fluidsSrc = buf.getBuffer(Sheets.translucentCullBlockSheet());
			float fill = fluid.getAmount() / (float) entity.fluidTank.getCapacity();
			
			cuboid.bounds(1F / 16F, 3.01F / 16F, 1F / 16F, 15F / 16F, 3.01F / 16F + 8.98F * fill / 16F, 15F / 16F);
			
			CuboidRenderer.renderCube(cuboid, matrix, fluidsSrc, argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
		}
	}
}