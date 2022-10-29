package org.zeith.tech.modules.processing.blocks.waste_processor;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.core.slot.FluidSlotBase;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.shared.ui.SlotInput;
import org.zeith.tech.modules.shared.ui.SlotOutput;

import java.util.List;

public class ContainerWasteProcessor
		extends ContainerBaseMachine<TileWasteProcessor>
{
	protected ContainerWasteProcessor(TileWasteProcessor tile, Player player, int windowId)
	{
		super(tile, player, windowId, List.of(
				new PropertyInt(DirectStorage.create(i -> tile._maxProgress = i, () -> tile._maxProgress)),
				new PropertyInt(DirectStorage.create(i -> tile._progress = i, () -> tile._progress)),
				new PropertyInt(DirectStorage.create(tile.energy.fe::setEnergyStored, tile.energy.fe::getEnergyStored))
		));
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(player.getInventory(), y + x * 9 + 9, 8 + y * 18, 90 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(player.getInventory(), x, 8 + x * 18, 148));
		
		
		this.addSlot(new SlotInput(tile.inventory, 0, 82, 38));
		
		this.addSlot(new SlotOutput(tile.inventory, 1, 176, 14));
		this.addSlot(new SlotOutput(tile.inventory, 2, 176, 38));
		this.addSlot(new SlotOutput(tile.inventory, 3, 176, 62));
		
		this.addSlot(new FluidSlotBase.FluidTankSlot(tile.input_a, 38, 14));
		this.addSlot(new FluidSlotBase.FluidTankSlot(tile.input_b, 60, 14));
		this.addSlot(new FluidSlotBase.FluidTankSlot(tile.output_a, 130, 14));
		this.addSlot(new FluidSlotBase.FluidTankSlot(tile.output_b, 152, 14));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiWasteProcessor(this, inv, label);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotIdx)
	{
		ItemStack duped = ItemStack.EMPTY;
		
		var slot = slots.get(slotIdx);
		if(slot.hasItem())
		{
			ItemStack origin = slot.getItem();
			duped = origin.copy();
			
			if(slot.container == tile.inventory)
			{
				if(!this.moveItemStackTo(origin, 0, 36, true))
					return ItemStack.EMPTY;
			}
			
			if(slot.container == player.getInventory())
			{
				if(!this.moveItemStackTo(origin, 36, slots.size(), true))
					return ItemStack.EMPTY;
			}
			
			if(origin.getCount() == duped.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, origin);
		}
		
		return duped;
	}
}