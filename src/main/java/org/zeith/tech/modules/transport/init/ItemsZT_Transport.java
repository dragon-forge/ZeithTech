package org.zeith.tech.modules.transport.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.transport.items.ItemMultimeter;

@SimplyRegister
public interface ItemsZT_Transport
{
	@RegistryName("transport/multimeter")
	ItemMultimeter MULTIMETER = new ItemMultimeter(BaseZT.itemProps().stacksTo(1));
}