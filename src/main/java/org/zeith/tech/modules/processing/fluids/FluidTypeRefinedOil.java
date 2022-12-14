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
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.ZeithTechAPI;

import java.util.function.Consumer;

public class FluidTypeRefinedOil
		extends FluidType
{
	public static final ResourceLocation OIL_STILL = ZeithTechAPI.id("processing/block/refined_oil"),
			OIL_FLOW = ZeithTechAPI.id("processing/block/refined_oil_flow"),
			OIL_RENDER_OVERLAY = ZeithTechAPI.id("textures/misc/under_refined_oil.png");
	
	public static FluidType create()
	{
		return new FluidTypeRefinedOil(Properties.create()
				.canDrown(true)
				.canSwim(false)
				.density(5000)
				.viscosity(8000)
				.motionScale(0.014D)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
				.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		);
	}
	
	protected FluidTypeRefinedOil(Properties properties)
	{
		super(properties);
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
				return OIL_STILL;
			}
			
			@Override
			public ResourceLocation getFlowingTexture()
			{
				return OIL_FLOW;
			}
			
			@Override
			public ResourceLocation getRenderOverlayTexture(Minecraft mc)
			{
				return OIL_RENDER_OVERLAY;
			}
			
			@Override
			public void renderOverlay(Minecraft mc, PoseStack poseStack)
			{
				for(int i = 0; i < 24; ++i)
					ScreenEffectRenderer.renderFluid(mc, poseStack, OIL_RENDER_OVERLAY);
			}
			
			@Override
			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
			{
				fluidFogColor.mul(0.1F);
				return fluidFogColor;
			}
		});
	}
}