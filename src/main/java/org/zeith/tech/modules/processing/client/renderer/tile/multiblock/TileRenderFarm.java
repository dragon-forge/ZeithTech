package org.zeith.tech.modules.processing.client.renderer.tile.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.zeith.hammerlib.client.render.FluidRendererHelper;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.hammerlib.client.utils.FluidTextureType;
import org.zeith.hammerlib.util.colors.ColorHelper;
import org.zeith.hammerlib.util.mcf.RotationHelper;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.core.client.renderer.*;
import org.zeith.tech.modules.processing.blocks.farm.TileFarm;

public class TileRenderFarm
		implements IBESR<TileFarm>
{
	private final ModelPart bone;
	
	public TileRenderFarm()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-24.0F, -32.0F, -24.0F, 48.0F, 32.0F, 48.0F, new CubeDeformation(0.0F))
				.texOffs(0, 80).addBox(-16.0F, -41.0F, -16.0F, 32.0F, 9.0F, 32.0F, new CubeDeformation(0.0F))
				.texOffs(96, 80).addBox(-8.0F, -49.0F, -8.0F, 16.0F, 8.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-4.0F, -65.0F, -4.0F, 8.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		
		this.bone = LayerDefinition.create(meshdefinition, 256, 256).bakeRoot();
	}
	
	public static final RenderType FARM_TYPE = RenderType.entityCutout(ZeithTechAPI.id("textures/processing/entity/farm/base.png"));
	public static final RenderType FARM_OVERLAY_TYPE = RenderType.entityCutout(ZeithTechAPI.id("textures/processing/entity/farm/overlay.png"));
	
	@Override
	public void render(TileFarm entity, float partial, PoseStack pose, MultiBufferSource buf, int lighting, int overlay)
	{
		if(!entity.isMultiblockValid()) return;
		
		lighting = entity.updateLighting(tile ->
		{
			Level level = tile.getLevel();
			int i;
			if(level != null)
			{
				i = 0;
				
				for(var direction : Direction.values())
				{
					var pos = tile.getBlockPos().relative(direction, 2);
					
					for(Direction d : Direction.values())
					{
						if(d.getAxis() != direction.getAxis())
						{
							BlockPos pos1 = pos.relative(d);
							
							Direction d2 = RotationHelper.rotate(d, direction.getAxis(), Direction.AxisDirection.POSITIVE, Rotation.CLOCKWISE_90);
							BlockPos pos2 = pos1.relative(d2);
							
							i = Math.max(i, LevelRenderer.getLightColor(level, pos1));
							i = Math.max(i, LevelRenderer.getLightColor(level, pos2));
						}
					}
				}
			} else i = 0xF000F0;
			return i;
		});
		
		pose.pushPose();
		
		RotatedRenderHelper.rotateHorizontalPoseStack(pose, entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite(), 0.5F, 0.5F, 0.5F);
		pose.mulPose(Vector3f.XP.rotationDegrees(180));
		
		bone.render(pose, buf.getBuffer(FARM_TYPE), lighting, overlay, 1F, 1F, 1F, 1F);
		
		int rgb = entity.algorithmInventory.getItem(0).getBarColor();
		float r = ColorHelper.getRed(rgb), g = ColorHelper.getGreen(rgb), b = ColorHelper.getBlue(rgb);
		bone.render(pose, buf.getBuffer(FARM_OVERLAY_TYPE), lighting, overlay, r, g, b, 1F);
		
		pose.popPose();
		
		
		final Cuboid cuboid = new Cuboid();
		var fluid = entity.tankSmooth.getClientAverage(partial);
		if(!fluid.isEmpty())
		{
			pose.pushPose();
			cuboid.bounds(0.01F, 1.5625F, 0.01F, 0.99F, Mth.lerp(fluid.getAmount() / (float) entity.water.getCapacity(), 1.5625F, 2F), 0.99F);
			cuboid.setTexture(FluidRendererHelper.getFluidTexture(fluid, FluidTextureType.STILL));
			lighting = FluidRendererHelper.calculateGlowLight(lighting, fluid);
			int argb = FluidRendererHelper.getColorARGB(fluid);
			CuboidRenderer.renderCube(cuboid, pose, buf.getBuffer(Sheets.translucentCullBlockSheet()), argb, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
			pose.popPose();
		}
	}
}