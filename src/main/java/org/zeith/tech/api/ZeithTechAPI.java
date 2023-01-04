package org.zeith.tech.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.tech.api.audio.IAudioSystem;
import org.zeith.tech.api.misc.farm.FarmAlgorithm;
import org.zeith.tech.api.modules.IZeithTechModules;
import org.zeith.tech.api.recipes.IRecipeRegistries;
import org.zeith.tech.modules.shared.init.TagsZT;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Abstract class for the ZeithTechAPI. Provides methods for accessing various features and capabilities of the mod.
 */
public abstract class ZeithTechAPI
{
	/**
	 * Logger for the ZeithTechAPI.
	 */
	public static final Logger LOG = LogManager.getLogger("ZeithTechAPI");
	
	/**
	 * TagKey for blocks that require a tungsten tool to be mined.
	 */
	public static final TagKey<Block> NEEDS_TUNGSTEN_TOOL = BlockTags.create(new ResourceLocation("forge", "needs_tungsten_tool"));
	
	/**
	 * Tier for tungsten tools.
	 */
	public static final Tier TUNGSTEN_TIER = new ForgeTier(4, 2031, 9.0F, 4.0F, 15, NEEDS_TUNGSTEN_TOOL, () -> Ingredient.of(TagsZT.Items.INGOTS_TUNGSTEN));
	
	/**
	 * Mod ID for ZeithTech.
	 */
	public static final String MOD_ID = "zeithtech";
	
	/**
	 * Static instance of the ZeithTechAPI.
	 */
	private static ZeithTechAPI INSTANCE;
	
	/**
	 * Returns true if the ZeithTechAPI is present, false otherwise.
	 *
	 * @return boolean value indicating if the ZeithTechAPI is present
	 */
	public static boolean isPresent()
	{
		return INSTANCE != null;
	}
	
	/**
	 * Returns the instance of the ZeithTechAPI.
	 *
	 * @return instance of the ZeithTechAPI
	 */
	public static ZeithTechAPI get()
	{
		return INSTANCE;
	}
	
	/**
	 * Executes the provided Consumer with the instance of the ZeithTechAPI if it is present.
	 *
	 * @param handler
	 * 		Consumer to be executed with the ZeithTechAPI instance
	 */
	public static void ifPresent(Consumer<ZeithTechAPI> handler)
	{
		if(INSTANCE != null)
			handler.accept(INSTANCE);
	}
	
	/**
	 * Applies the provided instance to the static ZeithTechAPI instance.
	 *
	 * @param INSTANCE
	 * 		instance to be applied to the static ZeithTechAPI instance
	 */
	public static void apply(ZeithTechAPI INSTANCE)
	{
		if(ZeithTechAPI.INSTANCE == null)
			ZeithTechAPI.INSTANCE = INSTANCE;
	}
	
	/**
	 * Returns a ResourceLocation with the provided path and ZeithTech as mod ID.
	 *
	 * @param path
	 * 		path to be appended to the mod ID
	 *
	 * @return ResourceLocation with the mod ID and path
	 */
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
	
	/**
	 * Returns the IZeithTechModules instance.
	 *
	 * @return IZeithTechModules instance
	 */
	public abstract IZeithTechModules getModules();
	
	/**
	 * Returns the IRecipeRegistries instance.
	 *
	 * @return IRecipeRegistries instance
	 */
	public abstract IRecipeRegistries getRecipeRegistries();
	
	/**
	 * Returns the IAudioSystem instance.
	 *
	 * @return IAudioSystem instance
	 */
	public abstract IAudioSystem getAudioSystem();
	
	/**
	 * Returns the ZeithTech CreativeModeTab.
	 *
	 * @return CreativeModeTab instance
	 */
	public abstract CreativeModeTab getCreativeTab();
	
	/**
	 * Returns the IForgeRegistry for FarmAlgorithms.
	 *
	 * @return IForgeRegistry for FarmAlgorithms
	 */
	public abstract IForgeRegistry<FarmAlgorithm> getFarmAlgorithms();
	
	/**
	 * Registers an item sprite with the provided ResourceLocation path.
	 *
	 * @param path
	 * 		ResourceLocation path for the item sprite to be registered
	 */
	public abstract void registerItemSprite(ResourceLocation path);
	
	/**
	 * Returns the partial tick value during a given frame.
	 *
	 * @return float value for the partial tick
	 */
	public float getPartialTick()
	{
		return 0F;
	}
	
	/**
	 * Returns an Optional containing the BlockState of a facade item, if it exists.
	 *
	 * @param stack
	 * 		ItemStack to be checked for a facade
	 *
	 * @return Optional containing the BlockState of the facade, if it exists
	 */
	public Optional<BlockState> getFacadeFromItem(ItemStack stack)
	{
		return Optional.empty();
	}
}