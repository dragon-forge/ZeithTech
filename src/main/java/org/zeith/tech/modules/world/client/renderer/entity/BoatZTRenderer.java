package org.zeith.tech.modules.world.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.zeith.tech.modules.world.entity.BoatZT;

import java.util.Map;
import java.util.stream.Stream;

public class BoatZTRenderer
		extends EntityRenderer<BoatZT>
{
	private final Map<BoatZT.Type, Pair<ResourceLocation, BoatModel>> boatResources;
	
	public static EntityRendererProvider<BoatZT> make(boolean chest)
	{
		return ctx -> new BoatZTRenderer(ctx, chest);
	}
	
	public BoatZTRenderer(EntityRendererProvider.Context ctx, boolean chest)
	{
		super(ctx);
		this.shadowRadius = 0.8F;
		this.boatResources = Stream.of(BoatZT.Type.values()).collect(ImmutableMap.toImmutableMap((p_173938_) ->
		{
			return p_173938_;
		}, (p_234575_) ->
		{
			return Pair.of(new ResourceLocation(getTextureLocation(p_234575_, chest)), this.createBoatModel(ctx, p_234575_, chest));
		}));
	}
	
	private BoatModel createBoatModel(EntityRendererProvider.Context ctx, BoatZT.Type type, boolean chest)
	{
		ModelLayerLocation loc = chest ? createChestBoatModelName(type) : createBoatModelName(type);
		
		return chest ? new ChestBoatModel(ctx.bakeLayer(loc))
				: new BoatModel(ctx.bakeLayer(loc));
	}
	
	public static ModelLayerLocation createBoatModelName(BoatZT.Type type)
	{
		return new ModelLayerLocation(new ResourceLocation("zeithtech", "boat/" + type.getName()), "main");
	}
	
	public static ModelLayerLocation createChestBoatModelName(BoatZT.Type type)
	{
		return new ModelLayerLocation(new ResourceLocation("zeithtech", "chest_boat/" + type.getName()), "main");
	}
	
	private static String getTextureLocation(BoatZT.Type type, boolean chest)
	{
		return chest ? "zeithtech:textures/entity/chest_boat/" + type.getName() + ".png" : "zeithtech:textures/entity/boat/" + type.getName() + ".png";
	}
	
	@Override
	public void render(BoatZT boat, float p_113930_, float p_113931_, PoseStack mat, MultiBufferSource src, int p_113934_)
	{
		mat.pushPose();
		mat.translate(0.0D, 0.375D, 0.0D);
		mat.mulPose(Axis.YP.rotationDegrees(180.0F - p_113930_));
		float f = (float) boat.getHurtTime() - p_113931_;
		float f1 = boat.getDamage() - p_113931_;
		if(f1 < 0.0F)
		{
			f1 = 0.0F;
		}
		
		if(f > 0.0F)
		{
			mat.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) boat.getHurtDir()));
		}
		
		float f2 = boat.getBubbleAngle(p_113931_);
		if(!Mth.equal(f2, 0.0F))
		{
			mat.mulPose((new Quaternionf()).setAngleAxis(boat.getBubbleAngle(p_113931_) * ((float) Math.PI / 180F), 1.0F, 0.0F, 1.0F));
		}
		
		Pair<ResourceLocation, BoatModel> pair = getModelWithLocation(boat);
		ResourceLocation tex = pair.getFirst();
		BoatModel model = pair.getSecond();
		mat.scale(-1.0F, -1.0F, 1.0F);
		mat.mulPose(Axis.YP.rotationDegrees(90.0F));
		model.setupAnim(boat, p_113931_, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer buf = src.getBuffer(model.renderType(tex));
		model.renderToBuffer(mat, buf, p_113934_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		if(!boat.isUnderWater())
		{
			VertexConsumer buf0 = src.getBuffer(RenderType.waterMask());
			model.waterPatch().render(mat, buf0, p_113934_, OverlayTexture.NO_OVERLAY);
		}
		
		mat.popPose();
		super.render(boat, p_113930_, p_113931_, mat, src, p_113934_);
	}
	
	@Deprecated // forge: override getModelWithLocation to change the texture / model
	@Override
	public ResourceLocation getTextureLocation(BoatZT boat)
	{
		return getModelWithLocation(boat).getFirst();
	}
	
	public Pair<ResourceLocation, BoatModel> getModelWithLocation(BoatZT boat)
	{
		return this.boatResources.get(boat.getBoatTypeZT());
	}
}