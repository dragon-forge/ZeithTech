package org.zeith.tech.api.misc;

import net.minecraft.resources.ResourceLocation;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.tile.slots.SlotRole;

import java.util.stream.Stream;

public record AuxiliaryPortDefinition(ResourceLocation auxPortTextureInput, ResourceLocation auxPortTextureOutput, ResourceLocation auxPortOverlayTexture)
{
	public static final AuxiliaryPortDefinition ENERGY_DEFINITION = new AuxiliaryPortDefinition(
			new ResourceLocation(ZeithTechAPI.MOD_ID, "block/aux_io_port/energy_in"),
			new ResourceLocation(ZeithTechAPI.MOD_ID, "block/aux_io_port/energy_out"),
			new ResourceLocation(ZeithTechAPI.MOD_ID, "block/aux_io_port/item_overlay")
	);
	
	public static final AuxiliaryPortDefinition ITEM_DEFINITION = new AuxiliaryPortDefinition(
			new ResourceLocation(ZeithTechAPI.MOD_ID, "block/aux_io_port/item_in"),
			new ResourceLocation(ZeithTechAPI.MOD_ID, "block/aux_io_port/item_out"),
			new ResourceLocation(ZeithTechAPI.MOD_ID, "block/aux_io_port/item_overlay")
	);
	
	public static final AuxiliaryPortDefinition FLUID_DEFINITION = new AuxiliaryPortDefinition(
			new ResourceLocation(ZeithTechAPI.MOD_ID, "block/aux_io_port/fluid_in"),
			new ResourceLocation(ZeithTechAPI.MOD_ID, "block/aux_io_port/fluid_out"),
			new ResourceLocation(ZeithTechAPI.MOD_ID, "block/aux_io_port/fluid_overlay")
	);
	
	public Stream<ResourceLocation> textures()
	{
		return Stream.of(auxPortTextureInput, auxPortTextureOutput, auxPortOverlayTexture);
	}
	
	public ResourceLocation forRole(SlotRole role)
	{
		return role.input() ? auxPortTextureInput : auxPortTextureOutput;
	}
}