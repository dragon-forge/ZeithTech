package org.zeith.tech.modules.shared.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.shared.blocks.aux_io_port.BlockAuxiliaryIOPort;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;
import org.zeith.tech.modules.world.init.BlocksZT_World;

@SimplyRegister
public interface BlocksZT
		extends BlocksZT_World, BlocksZT_Transport, BlocksZT_Processing
{
	@RegistryName("auxiliary_io_port")
	BlockAuxiliaryIOPort AUXILIARY_IO_PORT = new BlockAuxiliaryIOPort();
}