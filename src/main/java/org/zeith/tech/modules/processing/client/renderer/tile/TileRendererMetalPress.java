package org.zeith.tech.modules.processing.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.RotatedRenderHelper;
import org.zeith.tech.modules.processing.blocks.metal_press.TileMetalPress;

public class TileRendererMetalPress
		implements IBESR<TileMetalPress>
{
	final ModelPart animatedPart;
	
	public TileRendererMetalPress()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 48).addBox(-15.0F, -25.0F, 1.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		animatedPart = LayerDefinition.create(meshdefinition, 64, 64).bakeRoot();
	}
	
	private static final RenderType SOLID = RenderType.entitySolid(new ResourceLocation(ZeithTech.MOD_ID, "textures/processing/block/metal_press.png"));
	
	@Override
	public void render(TileMetalPress entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		float progress = Mth.lerp(partial, entity.prevProgress, entity.currentProgress);
		int mp = Math.max(entity._maxProgress, 1);
		
		float y = 1.1875F;
		y *= Math.min(entity.fallTimer > 0 ? (1F - (entity.fallTimer + partial) / 5F) : 1F, progress / (mp - 7));
		
		matrix.pushPose();
		matrix.translate(1, 0.3125F + y, 1);
		matrix.mulPose(Vector3f.XP.rotationDegrees(180));
		animatedPart.render(matrix, buf.getBuffer(SOLID), lighting, overlay, 1F, 1F, 1F, 1F);
		matrix.popPose();
		
		var mc = Minecraft.getInstance();
		var ir = mc.getItemRenderer();
		
		RotatedRenderHelper.rotateHorizontalPoseStack(matrix, entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), 0.5F, 0.265625F, 0.35F);
		matrix.mulPose(Vector3f.XP.rotationDegrees(90));
		ir.renderStatic(entity.inputItemDisplay.get(), ItemTransforms.TransformType.GROUND, lighting, overlay, matrix, buf, 0);
	}
}