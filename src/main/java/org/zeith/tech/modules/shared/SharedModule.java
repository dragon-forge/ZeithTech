package org.zeith.tech.modules.shared;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.zeith.hammerlib.HammerLib;
import org.zeith.tech.api.modules.IModuleShared;
import org.zeith.tech.core.IInternalCode;
import org.zeith.tech.modules.shared.init.RecipesZT;
import org.zeith.tech.modules.shared.proxy.ClientSharedProxyZT;
import org.zeith.tech.modules.shared.proxy.CommonSharedProxyZT;

public class SharedModule
		implements IModuleShared, IInternalCode
{
	public static final CommonSharedProxyZT PROXY = DistExecutor.unsafeRunForDist(() -> ClientSharedProxyZT::new, () -> CommonSharedProxyZT::new);
	
	private boolean wasEnabled = false;
	
	public SharedModule()
	{
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		PROXY.subEvents(bus);
	}
	
	@Override
	public void enable()
	{
		wasEnabled = true;
		HammerLib.EVENT_BUS.addListener(RecipesZT::provideRecipes);
	}
	
	@Override
	public boolean isModuleActivated()
	{
		return wasEnabled;
	}
}