package org.zeith.tech.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.hammerlib.util.mcf.LogicalSidePredictor;

import java.util.Optional;

public class RecipeManagerHelper
{
	private static final IEnv ENV = DistExecutor.unsafeRunForDist(() -> Client::new, () -> Server::new);
	
	public static Optional<RecipeManager> getRecipeManager(ReloadRecipeRegistryEvent.AddRecipes<?> e)
	{
		var side = LogicalSidePredictor.getCurrentLogicalSide();
		return e.getServer().map(MinecraftServer::getRecipeManager)
				.or(() -> getClientRecipeManager(side));
	}
	
	public static Optional<RecipeManager> getClientRecipeManager(LogicalSide side)
	{
		return ENV.getClientRecipeManager(side);
	}
	
	private interface IEnv
	{
		Optional<RecipeManager> getClientRecipeManager(LogicalSide side);
	}
	
	private static class Server
			implements IEnv
	{
		@Override
		public Optional<RecipeManager> getClientRecipeManager(LogicalSide side)
		{
			return Optional.empty();
		}
	}
	
	private static class Client
			implements IEnv
	{
		@Override
		public Optional<RecipeManager> getClientRecipeManager(LogicalSide side)
		{
			return Optional.of(Minecraft.getInstance())
					.map(Minecraft::getConnection)
					.map(ClientPacketListener::getRecipeManager);
		}
	}
}