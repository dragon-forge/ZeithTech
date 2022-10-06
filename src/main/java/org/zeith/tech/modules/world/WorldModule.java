package org.zeith.tech.modules.world;

import net.minecraftforge.fml.DistExecutor;
import org.zeith.hammerlib.HammerLib;
import org.zeith.tech.api.modules.IModuleWorld;
import org.zeith.tech.core.IInternalCode;
import org.zeith.tech.modules.world.init.FluidsZT_World;
import org.zeith.tech.modules.world.init.RecipesZT_World;
import org.zeith.tech.modules.world.proxy.ClientWorldProxyZT;
import org.zeith.tech.modules.world.proxy.CommonWorldProxyZT;
import org.zeith.tech.utils.LegacyEventBus;

public class WorldModule
		implements IModuleWorld, IInternalCode
{
	public static final CommonWorldProxyZT PROXY = DistExecutor.unsafeRunForDist(() -> ClientWorldProxyZT::new, () -> CommonWorldProxyZT::new);
	
	private boolean wasEnabled = false;
	
	@Override
	public void construct(LegacyEventBus bus)
	{
		PROXY.subEvents(bus.modBus());
		FluidsZT_World.register(bus);
	}
	
	@Override
	public void enable()
	{
		wasEnabled = true;
		HammerLib.EVENT_BUS.addListener(RecipesZT_World::provideRecipes);
	}
	
	@Override
	public boolean isModuleActivated()
	{
		return wasEnabled;
	}
}