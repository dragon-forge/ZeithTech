package org.zeith.tech.modules.processing.fluids;

import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
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
import org.zeith.tech.api.ZeithTechAPI;

import java.util.function.Consumer;

public class FluidTypeGas
		extends FluidType
{
	public static final ResourceLocation GAS_STILL = ZeithTechAPI.id("processing/block/gas"),
			GAS_FLOW = ZeithTechAPI.id("processing/block/gas_flow");
	
	public static FluidType create()
	{
		return new FluidTypeGas(Properties.create()
				.canDrown(false)
				.canSwim(false)
				.density(150)
				.viscosity(80)
				.motionScale(0D)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
				.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		);
	}
	
	protected FluidTypeGas(Properties properties)
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
				return GAS_STILL;
			}
			
			@Override
			public ResourceLocation getFlowingTexture()
			{
				return GAS_FLOW;
			}
			
			@Override
			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
			{
				return fluidFogColor;
			}
		});
	}
}