package org.zeith.tech.core.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class ZeithTechRenderer
{
	public static TextureAtlasSprite getBaseFluidTexture(@NotNull Fluid fluid, @NotNull FluidTextureType type)
	{
		IClientFluidTypeExtensions properties = IClientFluidTypeExtensions.of(fluid);
		ResourceLocation spriteLocation;
		if(type == FluidTextureType.STILL)
		{
			spriteLocation = properties.getStillTexture();
		} else
		{
			spriteLocation = properties.getFlowingTexture();
		}
		
		return getSprite(spriteLocation);
	}
	
	public static TextureAtlasSprite getFluidTexture(@NotNull FluidStack fluidStack, @NotNull FluidTextureType type)
	{
		IClientFluidTypeExtensions properties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
		return getSprite(type == FluidTextureType.STILL ? properties.getStillTexture(fluidStack) : properties.getFlowingTexture(fluidStack));
	}
	
	public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation)
	{
		return Minecraft.getInstance()
				.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
				.apply(spriteLocation);
	}
	
	public static int calculateGlowLight(int combinedLight, @NotNull FluidStack fluid)
	{
		return fluid.isEmpty() ? combinedLight : calculateGlowLight(combinedLight, fluid.getFluid().getFluidType().getLightLevel(fluid));
	}
	
	public static int calculateGlowLight(int combinedLight, int glow)
	{
		return combinedLight & -65536 | Math.max(Math.min(glow, 15) << 4, combinedLight & '\uffff');
	}
	
	public static int getColorARGB(@NotNull FluidStack fluidStack)
	{
		return IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
	}
}