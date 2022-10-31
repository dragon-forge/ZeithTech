package org.zeith.tech.modules.processing.blocks.blast_furnace;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.properties.PropertyFloat;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.core.slot.FluidSlotBase;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.shared.ui.SlotInput;

import java.util.List;

public class ContainerBlastFurnaceB
		extends ContainerBaseMachine<TileBlastFurnaceB>
{
	protected ContainerBlastFurnaceB(TileBlastFurnaceB tile, Player player, int windowId)
	{
		super(tile, player, windowId, List.of(
				new PropertyInt(DirectStorage.create(i -> tile._maxProgress = i, () -> tile._maxProgress)),
				new PropertyInt(DirectStorage.create(i -> tile.maxBurnTime = i, () -> tile.maxBurnTime)),
				new PropertyInt(DirectStorage.create(i -> tile._progress = i, () -> tile._progress)),
				new PropertyFloat(DirectStorage.create(i -> tile.burnTime = i, () -> tile.burnTime)),
				new PropertyFloat(DirectStorage.create(i -> tile.temperature = i, () -> tile.temperature))
		));
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(player.getInventory(), y + x * 9 + 9, 8 + y * 18, 92 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(player.getInventory(), x, 8 + x * 18, 150));
		
		
		this.addSlot(new SlotInput(tile.inventory, 0, 62, 40));
		this.addSlot(new SlotInput(tile.inventory, 1, 62, 17));
		this.addSlot(new FurnaceResultSlot(player, tile.inventory, 2, 120, 40));
		
		
		addSlot(new FluidSlotBase.FluidTankSlot(tile.fuel, 8, 15));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiBlastFurnaceB(this, inv, label);
	}
}