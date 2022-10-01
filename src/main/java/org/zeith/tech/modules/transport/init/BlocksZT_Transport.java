package org.zeith.tech.modules.transport.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.transport.blocks.item_pipe.BlockItemPipe;
import org.zeith.tech.modules.transport.blocks.item_pipe.ItemPipeProperties;

@SimplyRegister
public interface BlocksZT_Transport
{
	@RegistryName("copper_item_pipe")
	BlockItemPipe COPPER_ITEM_PIPE = new BlockItemPipe(new ItemPipeProperties());
}