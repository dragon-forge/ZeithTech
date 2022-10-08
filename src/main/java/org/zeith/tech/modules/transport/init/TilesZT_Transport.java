package org.zeith.tech.modules.transport.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.modules.transport.blocks.energy_wire.TileEnergyWire;
import org.zeith.tech.modules.transport.blocks.fluid_pipe.TileFluidPipe;
import org.zeith.tech.modules.transport.blocks.fluid_tank.basic.TileFluidTankB;
import org.zeith.tech.modules.transport.blocks.item_pipe.TileItemPipe;
import org.zeith.tech.modules.transport.client.renderer.tile.*;

import java.util.HashSet;

@SimplyRegister
public interface TilesZT_Transport
{
	@RegistryName("transport/fluid_tank/basic")
	@TileRenderer(TileRendererFluidTankB.class)
	BlockEntityType<TileFluidTankB> BASIC_FLUID_TANK = BlockAPI.createBlockEntityType(TileFluidTankB::new, BlocksZT_Transport.BASIC_FLUID_TANK);
	
	@RegistryName("transport/item_pipe")
	@TileRenderer(TileRendererItemPipe.class)
	BlockEntityType<TileItemPipe> ITEM_PIPE = new BlockEntityType<>(TileItemPipe::new, new HashSet<>(), null);
	
	@RegistryName("transport/energy_wire")
	BlockEntityType<TileEnergyWire> ENERGY_WIRE = new BlockEntityType<>(TileEnergyWire::new, new HashSet<>(), null);
	
	@RegistryName("transport/fluid_pipe")
	@TileRenderer(TileRendererFluidPipe.class)
	BlockEntityType<TileFluidPipe> FLUID_PIPE = new BlockEntityType<>(TileFluidPipe::new, new HashSet<>(), null);
}