package org.zeith.tech.modules.transport;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.zeith.hammerlib.HammerLib;
import org.zeith.tech.api.modules.IModuleTransport;
import org.zeith.tech.core.IInternalCode;
import org.zeith.tech.modules.transport.init.RecipesZT_Transport;
import org.zeith.tech.modules.transport.proxy.ClientTransportProxyZT;
import org.zeith.tech.modules.transport.proxy.CommonTransportProxyZT;

public class TransportModule
		implements IModuleTransport, IInternalCode
{
	public static final CommonTransportProxyZT PROXY = DistExecutor.unsafeRunForDist(() -> ClientTransportProxyZT::new, () -> CommonTransportProxyZT::new);
	
	
	private boolean wasEnabled = false;
	
	public TransportModule()
	{
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		PROXY.subEvents(bus);
	}
	
	@Override
	public void enable()
	{
		wasEnabled = true;
		HammerLib.EVENT_BUS.addListener(RecipesZT_Transport::provideRecipes);
	}
	
	@Override
	public boolean isModuleActivated()
	{
		return wasEnabled;
	}
}