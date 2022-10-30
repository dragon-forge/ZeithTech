package org.zeith.tech.modules.processing.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.RotatedRenderHelper;
import org.zeith.tech.modules.processing.blocks.pattern_storage.TilePatternStorage;

public class TileRendererPatternStorage
		implements IBESR<TilePatternStorage>
{
	protected final ModelPart root, file;
	
	public TileRendererPatternStorage()
	{
		{
			MeshDefinition meshdefinition = new MeshDefinition();
			PartDefinition partdefinition = meshdefinition.getRoot();
			PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(32, 18).addBox(-19.0F, -11.0F, 2.0F, 12.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
					.texOffs(16, 11).addBox(-19.0F, -11.0F, 13.0F, 12.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
					.texOffs(0, 11).addBox(-20.0F, -13.0F, 1.0F, 1.0F, 8.0F, 14.0F, new CubeDeformation(0.0F))
					.texOffs(18, 21).addBox(-7.0F, -11.0F, 2.0F, 1.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
					.texOffs(0, 0).addBox(-19.0F, -6.0F, 3.0F, 12.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
					.texOffs(0, 0).addBox(-21.0F, -11.0F, 6.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
			this.root = LayerDefinition.create(meshdefinition, 64, 64).bakeRoot();
		}
		
		{
			MeshDefinition meshdefinition = new MeshDefinition();
			PartDefinition partdefinition = meshdefinition.getRoot();
			PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
			PartDefinition file = root.addOrReplaceChild("file", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
			file.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 50)
							.addBox(-0.5F, -2.0F, -5.0F, 1.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
					PartPose.offsetAndRotation(-18.0F, -8.0F, 8.0F, 0.0F, 0.0F, 0.3054F));
			this.file = LayerDefinition.create(meshdefinition, 64, 64).bakeRoot();
		}
	}
	
	private static final RenderType CUTOUT_BLOCK = RenderType.entityCutout(new ResourceLocation(ZeithTech.MOD_ID, "textures/processing/block/pattern_storage/animated.png"));
	
	@Override
	public void render(TilePatternStorage entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		float openness = entity.getOpenness(partial);
		openness = 1.0F - openness;
		openness = openness * openness * openness;
		
		matrix.pushPose();
		RotatedRenderHelper.rotateHorizontalPoseStack(matrix, entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), 0F, 0F, 0F);
		matrix.translate(0, 1.5, 1 + 0.3125F * openness);
		matrix.mulPose(Vector3f.XP.rotationDegrees(180));
		
		matrix.mulPose(Vector3f.YP.rotationDegrees(90));
		
		var b = buf.getBuffer(CUTOUT_BLOCK);
		root.render(matrix, b, lighting, overlay, 1F, 1F, 1F, 1F);
		
		if(openness < 1)
		{
			matrix.translate(10 / 16D, 0, 0);
			for(int i = 0; i < Math.min(11, entity.slotCount.getInt()); ++i)
			{
				file.render(matrix, b, lighting, overlay, 1F, 1F, 1F, 1F);
				matrix.translate(-1 / 16D, 0, 0);
			}
		}
		
		matrix.popPose();
	}
}