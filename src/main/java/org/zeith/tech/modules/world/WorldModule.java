package org.zeith.tech.modules.world;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.zeith.hammerlib.HammerLib;
import org.zeith.tech.api.modules.IModuleWorld;
import org.zeith.tech.modules.IInternalCode;
import org.zeith.tech.modules.world.init.RecipesZT_World;
import org.zeith.tech.modules.world.proxy.ClientWorldProxyZT;
import org.zeith.tech.modules.world.proxy.CommonWorldProxyZT;

public class WorldModule
		implements IModuleWorld, IInternalCode
{
	public static final CommonWorldProxyZT PROXY = DistExecutor.unsafeRunForDist(() -> ClientWorldProxyZT::new, () -> CommonWorldProxyZT::new);
	
	public WorldModule()
	{
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		PROXY.subEvents(bus);
	}
	
	@Override
	public void enable()
	{
		HammerLib.EVENT_BUS.addListener(RecipesZT_World::provideRecipes);
	}
}