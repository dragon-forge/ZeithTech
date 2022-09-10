package org.zeith.tech;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.tech.api.recipes.RecipeRegistriesZT;
import org.zeith.tech.init.TagsZT;
import org.zeith.tech.init.blocks.MachinesZT;
import org.zeith.tech.proxy.ClientProxy;
import org.zeith.tech.proxy.CommonProxy;

@Mod(ZeithTech.MOD_ID)
public class ZeithTech
{
	public static final String MOD_ID = "zeithtech";
	
	public static final Logger LOG = LogManager.getLogger("ZeithTech");
	
	public static final CommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	
	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID)
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(MachinesZT.FUEL_GENERATOR_BASIC);
		}
	};
	
	public ZeithTech()
	{
		TagsZT.init();
		LanguageAdapter.registerMod(MOD_ID);
		
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(RecipeRegistriesZT::setup);
	}
}