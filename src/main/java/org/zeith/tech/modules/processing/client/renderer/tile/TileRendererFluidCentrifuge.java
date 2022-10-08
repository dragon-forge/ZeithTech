package org.zeith.tech.modules.processing.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.*;
import org.zeith.tech.modules.processing.blocks.fluid_centrifuge.TileFluidCentrifuge;

public class TileRendererFluidCentrifuge
		implements IBESR<TileFluidCentrifuge>
{
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "static"), "main");
	
	final ModelPart animatedPart;
	
	final Cuboid cuboid = new Cuboid();
	
	
	public TileRendererFluidCentrifuge()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition partdefinition = mesh.getRoot();
		
		PartDefinition root = partdefinition.addOrReplaceChild("root",
				CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 21.0F, 8.0F)
		);
		
		PartDefinition ring = root.addOrReplaceChild("ring",
				CubeListBuilder.create()
						.texOffs(16, 0).addBox(-12.0F, -13.0F, 4.0F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(8, 23).addBox(-6.0F, -13.0F, 6.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(0, 16).addBox(-12.0F, -13.0F, 6.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(14, 14).addBox(-12.0F, -13.0F, 10.0F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(8.0F, 3.0F, -8.0F));
		
		animatedPart = LayerDefinition.create(mesh, 64, 64).bakeRoot();
	}
	
	private static final RenderType CUTOUT_BLOCK_OFF = RenderType.entityCutout(new ResourceLocation(ZeithTech.MOD_ID, "textures/processing/block/fluid_centrifuge_anim_off.png"));
	private static final RenderType CUTOUT_BLOCK_ON = RenderType.entityCutout(new ResourceLocation(ZeithTech.MOD_ID, "textures/processing/block/fluid_centrifuge_anim_on.png"));
	
	@Override
	public void render(TileFluidCentrifuge entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		var cutout = buf.getBuffer(entity.isEnabled() ? CUTOUT_BLOCK_ON : CUTOUT_BLOCK_OFF);
		
		float rotation = entity.rotator.getActualRotation(partial);
		
		matrix.pushPose();
		matrix.translate(0.5, 0, 0.5);
		matrix.mulPose(Vector3f.YP.rotationDegrees(rotation));
		matrix.translate(0.5, 1.5, 0.5);
		matrix.mulPose(Vector3f.XP.rotationDegrees(180));
		animatedPart.render(matrix, cutout, lighting, overlay, 1F, 1F, 1F, 1F);
		matrix.popPose();
		
		var fluid = entity.outputTank.getClientAverage(partial);
		if(!fluid.isEmpty())
		{
			cuboid.setTexture(ZeithTechRenderer.getFluidTexture(fluid, FluidTextureType.STILL));
			lighting = ZeithTechRenderer.calculateGlowLight(lighting, fluid);
			int argb = ZeithTechRenderer.getColorARGB(fluid);
			var fluidsSrc = buf.getBuffer(Sheets.translucentCullBlockSheet());
			float fill = fluid.getAmount() / (float) entity.outputFluid.getCapacity();
			
			cuboid.bounds(4.01F / 16F, 6.01F / 16F, 4.01F / 16F, 11.99F / 16F, 6.01F / 16F + 6.98F * fill / 16F, 11.99F / 16F);
			
			matrix.pushPose();
			matrix.translate(0.5, 0, 0.5);
			matrix.mulPose(Vector3f.YP.rotationDegrees(rotation));
			matrix.translate(-0.5, 0, -0.5);
			CuboidRenderer.renderCube(cuboid, matrix, fluidsSrc, argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
			matrix.popPose();
		}
	}
}