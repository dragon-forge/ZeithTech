package org.zeith.tech.compat;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.tech.api.recipes.IRecipeLifecycleListener;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;
import org.zeith.tech.utils.LegacyEventBus;

import java.util.Optional;

public class BaseCompat
		implements IRecipeLifecycleListener
{
	protected final Logger log = LogManager.getLogger("ZeithTech/" + getClass().getSimpleName());
	
	public void setup(LegacyEventBus bus)
	{
	}
	
	@Override
	public <T extends IZeithTechRecipe> void onRecipeDeRegistered(T recipe)
	{
		IRecipeLifecycleListener.super.onRecipeDeRegistered(recipe);
	}
	
	@Override
	public <T extends IZeithTechRecipe> void onRecipeRegistered(T recipe)
	{
		IRecipeLifecycleListener.super.onRecipeRegistered(recipe);
	}
	
	public Optional<BlockState> getFacadeFromItem(ItemStack stack)
	{
		return Optional.empty();
	}
}