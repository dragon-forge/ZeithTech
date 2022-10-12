package org.zeith.tech.modules.processing.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.Cuboid;
import org.zeith.tech.modules.processing.blocks.waste_processor.TileWasteProcessor;

public class TileRendererWasteProcessor
		implements IBESR<TileWasteProcessor>
{
	final ModelPart animatedPart;
	
	final Cuboid cuboid = new Cuboid();
	
	public TileRendererWasteProcessor()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition bone = partdefinition.addOrReplaceChild("bone",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-12.0F, -13.0F, 4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));
		
		animatedPart = LayerDefinition.create(meshdefinition, 32, 32).bakeRoot();
	}
	
	private static final RenderType CUTOUT_BLOCK_OFF = RenderType.entityCutout(new ResourceLocation(ZeithTech.MOD_ID, "textures/processing/block/waste_processor/anim_on.png"));
	private static final RenderType CUTOUT_BLOCK_ON = RenderType.entityCutout(new ResourceLocation(ZeithTech.MOD_ID, "textures/processing/block/waste_processor/anim_off.png"));
	
	@Override
	public void render(TileWasteProcessor entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		double progress = (Mth.cos(entity.rotator.getActualRotation(partial) * 0.017453292519943295F) + 1F) / 2F;
		
		matrix.pushPose();
		matrix.translate(1, 1.5 + 0.125F * progress, 1);
		matrix.mulPose(Vector3f.XP.rotationDegrees(180));
		animatedPart.render(matrix, buf.getBuffer(entity.isEnabled() ? CUTOUT_BLOCK_OFF : CUTOUT_BLOCK_ON), lighting, overlay, 1F, 1F, 1F, 1F);
		matrix.popPose();
	}
}