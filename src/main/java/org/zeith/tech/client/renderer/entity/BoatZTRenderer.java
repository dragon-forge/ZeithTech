package org.zeith.tech.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.zeith.tech.common.entity.BoatZT;

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
		ModelLayerLocation loc = new ModelLayerLocation(new ResourceLocation("zeithtech", (chest ? "chest_" : "") + "boat/" + type.getName()), "main");
		return new BoatModel(ctx.bakeLayer(loc), chest);
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
	public void render(BoatZT boat, float p_113930_, float p_113931_, PoseStack p_113932_, MultiBufferSource p_113933_, int p_113934_)
	{
		p_113932_.pushPose();
		p_113932_.translate(0.0D, 0.375D, 0.0D);
		p_113932_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_113930_));
		float f = (float) boat.getHurtTime() - p_113931_;
		float f1 = boat.getDamage() - p_113931_;
		if(f1 < 0.0F)
		{
			f1 = 0.0F;
		}
		
		if(f > 0.0F)
		{
			p_113932_.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) boat.getHurtDir()));
		}
		
		float f2 = boat.getBubbleAngle(p_113931_);
		if(!Mth.equal(f2, 0.0F))
		{
			p_113932_.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), boat.getBubbleAngle(p_113931_), true));
		}
		
		Pair<ResourceLocation, BoatModel> pair = getModelWithLocation(boat);
		ResourceLocation resourcelocation = pair.getFirst();
		BoatModel boatmodel = pair.getSecond();
		p_113932_.scale(-1.0F, -1.0F, 1.0F);
		p_113932_.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		boatmodel.setupAnim(boat, p_113931_, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexconsumer = p_113933_.getBuffer(boatmodel.renderType(resourcelocation));
		boatmodel.renderToBuffer(p_113932_, vertexconsumer, p_113934_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		if(!boat.isUnderWater())
		{
			VertexConsumer vertexconsumer1 = p_113933_.getBuffer(RenderType.waterMask());
			boatmodel.waterPatch().render(p_113932_, vertexconsumer1, p_113934_, OverlayTexture.NO_OVERLAY);
		}
		
		p_113932_.popPose();
		super.render(boat, p_113930_, p_113931_, p_113932_, p_113933_, p_113934_);
	}
	
	private static void animatePaddle(BoatZT boat, int p_170466_, ModelPart part, float p_170468_)
	{
		float f = boat.getRowingTime(p_170466_, p_170468_);
		part.xRot = Mth.clampedLerp((-(float) Math.PI / 3F), -0.2617994F, (Mth.sin(-f) + 1.0F) / 2.0F);
		part.yRot = Mth.clampedLerp((-(float) Math.PI / 4F), ((float) Math.PI / 4F), (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F);
		if(p_170466_ == 1)
		{
			part.yRot = (float) Math.PI - part.yRot;
		}
		
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