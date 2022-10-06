package org.zeith.tech.modules.world.fluids.crude_oil;

import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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

public class FluidTypeCrudeOil
		extends FluidType
{
	public static FluidType create()
	{
		return new FluidTypeCrudeOil(FluidType.Properties.create()
				.canDrown(true)
				.density(3000)
				.viscosity(6000)
				.motionScale(0.002D)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
				.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		);
	}
	
	protected FluidTypeCrudeOil(Properties properties)
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
			private static final ResourceLocation OIL_STILL = new ResourceLocation(ZeithTech.MOD_ID, "block/crude_oil"),
					OIL_FLOW = new ResourceLocation(ZeithTech.MOD_ID, "block/crude_oil_flow"),
					OIL_RENDER_OVERLAY = new ResourceLocation(ZeithTech.MOD_ID, "textures/misc/under_crude_oil.png");
			
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
			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
			{
				fluidFogColor.mul(0.2F);
				return fluidFogColor;
			}
		});
	}
}