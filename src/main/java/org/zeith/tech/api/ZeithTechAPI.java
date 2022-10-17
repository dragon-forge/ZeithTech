package org.zeith.tech.api;

import net.minecraft.world.item.CreativeModeTab;
import org.zeith.tech.api.audio.IAudioSystem;
import org.zeith.tech.api.modules.IZeithTechModules;
import org.zeith.tech.api.recipes.IRecipeRegistries;

import java.util.function.Consumer;

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
	
	public static void ifPresent(Consumer<ZeithTechAPI> handler)
	{
		if(INSTANCE != null)
			handler.accept(INSTANCE);
	}
	
	public static void apply(ZeithTechAPI INSTANCE)
	{
		if(ZeithTechAPI.INSTANCE == null)
			ZeithTechAPI.INSTANCE = INSTANCE;
	}
	
	public abstract IZeithTechModules getModules();
	
	public abstract IRecipeRegistries getRecipeRegistries();
	
	public abstract IAudioSystem getAudioSystem();
	
	public abstract CreativeModeTab getCreativeTab();
	
	public float getPartialTick()
	{
		return 0F;
	}
}