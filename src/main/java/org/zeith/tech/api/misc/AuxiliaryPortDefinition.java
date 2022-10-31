package org.zeith.tech.api.misc;

import net.minecraft.resources.ResourceLocation;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.tile.slots.SlotRole;

import java.util.stream.Stream;

public record AuxiliaryPortDefinition(ResourceLocation auxPortTextureInput, ResourceLocation auxPortTextureOutput, ResourceLocation auxPortTextureBoth, ResourceLocation auxPortOverlayTexture)
{
	public static final AuxiliaryPortDefinition ENERGY_DEFINITION = new AuxiliaryPortDefinition(
			ZeithTechAPI.id("block/aux_io_port/energy_in"),
			ZeithTechAPI.id("block/aux_io_port/energy_out"),
			ZeithTechAPI.id("block/aux_io_port/energy_both"),
			ZeithTechAPI.id("block/aux_io_port/item_overlay")
	);
	
	public static final AuxiliaryPortDefinition ITEM_DEFINITION = new AuxiliaryPortDefinition(
			ZeithTechAPI.id("block/aux_io_port/item_in"),
			ZeithTechAPI.id("block/aux_io_port/item_out"),
			ZeithTechAPI.id("block/aux_io_port/item_both"),
			ZeithTechAPI.id("block/aux_io_port/item_overlay")
	);
	
	public static final AuxiliaryPortDefinition FLUID_DEFINITION = new AuxiliaryPortDefinition(
			ZeithTechAPI.id("block/aux_io_port/fluid_in"),
			ZeithTechAPI.id("block/aux_io_port/fluid_out"),
			ZeithTechAPI.id("block/aux_io_port/fluid_both"),
			ZeithTechAPI.id("block/aux_io_port/fluid_overlay")
	);
	
	public Stream<ResourceLocation> textures()
	{
		return Stream.of(auxPortTextureInput, auxPortTextureOutput, auxPortTextureBoth, auxPortOverlayTexture);
	}
	
	public ResourceLocation forRole(SlotRole role)
	{
		return role.input() ? (role.output() ? auxPortTextureBoth : auxPortTextureInput) : auxPortTextureOutput;
	}
}