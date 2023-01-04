package org.zeith.tech.modules.transport.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.transport.items.ItemFacade;
import org.zeith.tech.modules.transport.items.ItemPipeCutter;
import org.zeith.tech.modules.transport.items.multimeter.ItemMultimeter;

@SimplyRegister(prefix = "transport/")
public interface ItemsZT_Transport
{
	@RegistryName("multimeter")
	ItemMultimeter MULTIMETER = new ItemMultimeter(BaseZT.itemProps().stacksTo(1));
	
	@RegistryName("facade")
	ItemFacade FACADE = new ItemFacade(BaseZT.itemProps());
	
	@RegistryName("pipe_cutter")
	ItemPipeCutter PIPE_CUTTER = new ItemPipeCutter();
}