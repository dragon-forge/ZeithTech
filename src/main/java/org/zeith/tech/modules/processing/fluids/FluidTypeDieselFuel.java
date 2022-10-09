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
import org.zeith.tech.core.ZeithTech;

import java.util.function.Consumer;

public class FluidTypeDieselFuel
		extends FluidType
{
	public static final ResourceLocation FUEL_STILL = new ResourceLocation(ZeithTech.MOD_ID, "processing/block/diesel_fuel"),
			FUEL_FLOW = new ResourceLocation(ZeithTech.MOD_ID, "processing/block/diesel_fuel_flow"),
			FUEL_RENDER_OVERLAY = new ResourceLocation(ZeithTech.MOD_ID, "textures/misc/under_diesel_fuel.png");
	
	public static FluidType create()
	{
		return new FluidTypeDieselFuel(Properties.create()
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
	
	protected FluidTypeDieselFuel(Properties properties)
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
				return FUEL_STILL;
			}
			
			@Override
			public ResourceLocation getFlowingTexture()
			{
				return FUEL_FLOW;
			}
			
			@Override
			public ResourceLocation getRenderOverlayTexture(Minecraft mc)
			{
				return FUEL_RENDER_OVERLAY;
			}
			
			@Override
			public void renderOverlay(Minecraft mc, PoseStack poseStack)
			{
				for(int i = 0; i < 16; ++i)
					ScreenEffectRenderer.renderFluid(mc, poseStack, FUEL_RENDER_OVERLAY);
			}
			
			@Override
			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
			{
				return fluidFogColor;
			}
		});
	}
}