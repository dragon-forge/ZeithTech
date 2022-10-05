package org.zeith.tech.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.hammerlib.util.mcf.LogicalSidePredictor;

import java.util.Optional;
import java.util.function.Supplier;

public class RecipeManagerHelper
{
	public static Optional<RecipeManager> getRecipeManager(ReloadRecipeRegistryEvent.AddRecipes<?> e)
	{
		var side = LogicalSidePredictor.getCurrentLogicalSide();
		return e.getServer().map(MinecraftServer::getRecipeManager)
				.or(() -> getClientRecipeManager(side));
	}
	
	public static Optional<RecipeManager> getClientRecipeManager(LogicalSide side)
	{
		if(side == LogicalSide.CLIENT)
		{
			Supplier<Optional<RecipeManager>> getter = () -> Optional.ofNullable(Minecraft.getInstance().getConnection()).map(ClientPacketListener::getRecipeManager)
					.or(() -> Optional.ofNullable(Minecraft.getInstance().level).map(Level::getRecipeManager));
			return getter.get();
		}
		return Optional.empty();
	}
}