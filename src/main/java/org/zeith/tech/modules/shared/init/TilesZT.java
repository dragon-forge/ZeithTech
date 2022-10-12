package org.zeith.tech.modules.shared.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.modules.shared.blocks.aux_io_port.TileAuxiliaryIOPort;

@SimplyRegister
public interface TilesZT
{
	@RegistryName("auxiliary_io_port")
	BlockEntityType<TileAuxiliaryIOPort> AUXILIARY_IO_PORT = BlockAPI.createBlockEntityType(TileAuxiliaryIOPort::new, BlocksZT.AUXILIARY_IO_PORT);
}