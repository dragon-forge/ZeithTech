package org.zeith.tech.modules.processing.fluids;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.modules.processing.init.DamageTypesZT_Processing;

import java.util.function.Consumer;

public class FluidTypeSulfuricAcid
		extends FluidType
{
	public static final ResourceLocation ACID_STILL = ZeithTechAPI.id("processing/block/sulfuric_acid"),
			ACID_FLOW = ZeithTechAPI.id("processing/block/sulfuric_acid_flow"),
			ACID_RENDER_OVERLAY = ZeithTechAPI.id("textures/misc/under_sulfuric_acid.png");
	
	public static FluidType create()
	{
		return new FluidTypeSulfuricAcid(Properties.create()
				.canDrown(true)
				.canSwim(false)
				.density(1500)
				.viscosity(1500)
				.motionScale(0.014D)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
				.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		);
	}
	
	protected FluidTypeSulfuricAcid(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity)
	{
		if(entity.hurtTime <= 0)
			entity.hurt(DamageTypesZT_Processing.SULFURIC_ACID, 3F);
		return false;
	}
	
	@Override
	public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack)
	{
		return false;
	}
	
	@Override
	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
	{
		consumer.accept(new IClientFluidTypeExtensions()
		{
			@Override
			public ResourceLocation getStillTexture()
			{
				return ACID_STILL;
			}
			
			@Override
			public ResourceLocation getFlowingTexture()
			{
				return ACID_FLOW;
			}
			
			@Override
			public ResourceLocation getRenderOverlayTexture(Minecraft mc)
			{
				return ACID_RENDER_OVERLAY;
			}
			
			@Override
			public void renderOverlay(Minecraft mc, PoseStack poseStack)
			{
				for(int i = 0; i < 10; ++i)
					ScreenEffectRenderer.renderFluid(mc, poseStack, ACID_RENDER_OVERLAY);
			}
			
			@Override
			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
			{
				return fluidFogColor;
			}
		});
	}
}