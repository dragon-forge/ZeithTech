package org.zeith.tech.modules.transport.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.tech.modules.transport.blocks.energy_wire.TileEnergyWire;
import org.zeith.tech.modules.transport.blocks.item_pipe.TileItemPipe;
import org.zeith.tech.modules.transport.client.renderer.tile.TileRendererItemPipe;

import java.util.HashSet;

@SimplyRegister
public interface TilesZT_Transport
{
	@RegistryName("item_pipe")
	@TileRenderer(TileRendererItemPipe.class)
	BlockEntityType<TileItemPipe> ITEM_PIPE = new BlockEntityType<>(TileItemPipe::new, new HashSet<>(), null);
	
	@RegistryName("energy_wire")
	BlockEntityType<TileEnergyWire> ENERGY_WIRE = new BlockEntityType<>(TileEnergyWire::new, new HashSet<>(), null);
}