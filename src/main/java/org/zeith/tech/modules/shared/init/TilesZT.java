package org.zeith.tech.modules.shared.init;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.modules.shared.blocks.TileCapabilityProxy;
import org.zeith.tech.modules.shared.blocks.aux_io_port.TileAuxiliaryIOPort;

import java.util.HashSet;

@SimplyRegister
public interface TilesZT
{
	@RegistryName("auxiliary_io_port")
	BlockEntityType<TileAuxiliaryIOPort> AUXILIARY_IO_PORT = BlockAPI.createBlockEntityType(TileAuxiliaryIOPort::new, BlocksZT.AUXILIARY_IO_PORT);
	
	@RegistryName("capability_proxy")
	BlockEntityType<TileCapabilityProxy> CAPABILITY_PROXY = new BlockEntityType<>(TileCapabilityProxy::new, new HashSet<>(), null)
	{
		@Override
		public boolean isValid(BlockState state)
		{
			return state.getBlock() instanceof EntityBlock;
		}
	};
}