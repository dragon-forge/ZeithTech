package org.zeith.tech;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.tech.proxy.ClientProxy;
import org.zeith.tech.proxy.CommonProxy;

@Mod(ZeithTech.MOD_ID)
public class ZeithTech
{
	public static final String MOD_ID = "zeithtech";
	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static final CommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	
	public ZeithTech()
	{
		LanguageAdapter.registerMod(MOD_ID);
	}
}