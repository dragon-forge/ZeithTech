package org.zeith.tech.common.blocks.base.machine;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.api.inv.ComplexProgressHandler;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.client.screen.MenuWithProgressBars;
import org.zeith.hammerlib.net.properties.IProperty;

import java.util.List;

public abstract class ContainerBaseMachine<T extends TileBaseMachine<T>>
		extends MenuWithProgressBars
		implements IScreenContainer
{
	public final T tile;
	
	protected ContainerBaseMachine(T tile, Player player, int windowId, ComplexProgressHandler handler)
	{
		super(ContainerAPI.TILE_CONTAINER, windowId, handler);
		this.tile = tile;
	}
	
	protected ContainerBaseMachine(T tile, Player player, int windowId, List<IProperty<?>> props)
	{
		super(ContainerAPI.TILE_CONTAINER, windowId, ComplexProgressHandler.withProperties(props));
		this.tile = tile;
	}
	
	@Override
	public boolean stillValid(@NotNull Player player)
	{
		return tile != null && !tile.isRemoved() && tile.getBlockPos().closerToCenterThan(player.position(), 8);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public abstract Screen openScreen(Inventory inv, Component label);
}