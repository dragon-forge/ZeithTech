package org.zeith.tech.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeTier;
import org.zeith.tech.api.audio.IAudioSystem;
import org.zeith.tech.api.modules.IZeithTechModules;
import org.zeith.tech.api.recipes.IRecipeRegistries;
import org.zeith.tech.modules.shared.init.TagsZT;

import java.util.function.Consumer;

public abstract class ZeithTechAPI
{
	public static final TagKey<Block> NEEDS_TUNGSTEN_TOOL = BlockTags.create(new ResourceLocation("forge", "needs_tungsten_tool"));
	public static final Tier TUNGSTEN_TIER = new ForgeTier(4, 2031, 9.0F, 4.0F, 15, NEEDS_TUNGSTEN_TOOL, () -> Ingredient.of(TagsZT.Items.INGOTS_TUNGSTEN));
	
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
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
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