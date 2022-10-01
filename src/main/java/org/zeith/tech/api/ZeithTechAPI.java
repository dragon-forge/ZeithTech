package org.zeith.tech.api;

import org.zeith.tech.api.modules.IZeithTechModules;

public abstract class ZeithTechAPI
{
	public static final String MOD_ID = "zeithtech";
	
	private static ZeithTechAPI INSTANCE;
	
	public static boolean isPresent()
	{
		return INSTANCE != null;
	}
	
	public static ZeithTechAPI get()
	{
		return INSTANCE;
	}
	
	public static void apply(ZeithTechAPI INSTANCE)
	{
		if(ZeithTechAPI.INSTANCE == null)
			ZeithTechAPI.INSTANCE = INSTANCE;
	}
	
	public abstract IZeithTechModules getModules();
}