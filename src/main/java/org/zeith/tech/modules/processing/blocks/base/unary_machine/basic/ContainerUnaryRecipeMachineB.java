package org.zeith.tech.modules.processing.blocks.base.unary_machine.basic;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.shared.ui.SlotInput;
import org.zeith.tech.modules.shared.ui.SlotOutput;

import java.util.List;

public class ContainerUnaryRecipeMachineB<T extends TileUnaryRecipeMachineB<T, ?>>
		extends ContainerBaseMachine<T>
{
	public ContainerUnaryRecipeMachineB(T tile, Player player, int windowId)
	{
		super(tile, player, windowId, List.of(
				new PropertyInt(DirectStorage.create(i -> tile._maxProgress = i, () -> tile._maxProgress)),
				new PropertyInt(DirectStorage.create(i -> tile._progress = i, () -> tile._progress)),
				new PropertyInt(DirectStorage.create(tile.energy.fe::setEnergyStored, tile.energy.fe::getEnergyStored))
		));
		addInventorySlots(player);
		addMachineSlots(tile);
	}
	
	protected void addMachineSlots(T tile)
	{
		this.addSlot(new SlotInput(tile.inventory, 0, 48, 35));
		this.addSlot(new SlotOutput(tile.inventory, 1, 108, 35));
	}
	
	protected void addInventorySlots(Player player)
	{
		addInventorySlotsAt(player, 8, 84);
	}
	
	protected void addInventorySlotsAt(Player player, int xPos, int yPos)
	{
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(player.getInventory(), y + x * 9 + 9, xPos + y * 18, yPos + x * 18));
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(player.getInventory(), x, xPos + x * 18, yPos + 58));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiUnaryRecipeMachineB openScreen(Inventory inv, Component label)
	{
		return new GuiUnaryRecipeMachineB(this, inv, label);
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
				if(!this.moveItemStackTo(origin, 36, 37, true))
					return ItemStack.EMPTY;
			}
			
			if(origin.getCount() == duped.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, origin);
		}
		
		return duped;
	}
}