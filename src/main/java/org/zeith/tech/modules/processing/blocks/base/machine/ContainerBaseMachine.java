package org.zeith.tech.modules.processing.blocks.base.machine;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.api.inv.ComplexProgressHandler;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.client.screen.MenuWithProgressBars;
import org.zeith.hammerlib.net.properties.IProperty;
import org.zeith.tech.api.tile.slots.ISlot;
import org.zeith.tech.api.tile.slots.ITileSlotProvider;

import java.util.List;
import java.util.Map;

public abstract class ContainerBaseMachine<T extends TileBaseMachine<T>>
		extends MenuWithProgressBars
		implements IScreenContainer
{
	public final T tile;
	
	protected Map<Slot, ISlot<?>> mappedSlots;
	
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
	
	public Map<Slot, ISlot<?>> getMappedSlots()
	{
		if(mappedSlots == null)
			mappedSlots = ITileSlotProvider.getSlotsAt(tile.getLevel(), tile.getBlockPos(), null)
					.map(s -> s.mapGuiSlots(slots))
					.orElse(Map.of());
		
		return mappedSlots;
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