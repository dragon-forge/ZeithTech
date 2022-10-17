package org.zeith.tech.modules.shared.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.api.item.IBurnableItem;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.shared.blocks.BlockMasut;
import org.zeith.tech.modules.shared.blocks.aux_io_port.BlockAuxiliaryIOPort;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;
import org.zeith.tech.modules.world.init.BlocksZT_World;

@SimplyRegister
public interface BlocksZT
		extends BlocksZT_World, BlocksZT_Transport, BlocksZT_Processing
{
	@RegistryName("auxiliary_io_port")
	BlockAuxiliaryIOPort AUXILIARY_IO_PORT = new BlockAuxiliaryIOPort();
	
	@RegistryName("masut")
	Block MASUT = new BlockMasut(BlockBehaviour.Properties.of(Material.DIRT).sound(SoundType.MUDDY_MANGROVE_ROOTS).strength(0.5F), BlockHarvestAdapter.MineableType.SHOVEL).burnable(IBurnableItem.constantBurnTime(200 * 80)).dropsSelf();
}