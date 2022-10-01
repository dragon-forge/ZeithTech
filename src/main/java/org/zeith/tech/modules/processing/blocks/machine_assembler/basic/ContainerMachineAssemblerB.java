package org.zeith.tech.modules.processing.blocks.machine_assembler.basic;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.shared.ui.SlotInput;
import org.zeith.tech.modules.shared.ui.SlotOutput;

import java.util.List;

public class ContainerMachineAssemblerB
		extends ContainerBaseMachine<TileMachineAssemblerB>
{
	protected ContainerMachineAssemblerB(TileMachineAssemblerB tile, Player player, int windowId)
	{
		super(tile, player, windowId, List.of(
				tile.craftingProgress,
				tile.craftTime
		));
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(player.getInventory(), y + x * 9 + 9, 8 + y * 18, 104 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(player.getInventory(), x, 8 + x * 18, 162));
		
		for(int y = 0; y < 5; ++y)
		{
			int start = 0;
			int end = 5;
			
			if(y == 0 || y == 4)
			{
				start = 2;
				end = 3;
			} else if(y == 1 || y == 3)
			{
				start = 1;
				end = 4;
			}
			
			for(x = start; x < end; ++x)
			{
				this.addSlot(new SlotInput(tile.craftingInventory, x + y * 5, 8 + x * 18, 9 + y * 18));
			}
		}
		
		this.addSlot(new SlotOutput(tile.resultInventory, 0, 148, 45));
		
		this.addSlot(new SlotInput(tile.toolInventory, 0, 110, 9));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiMachineAssemblerB(this, inv, label);
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
			
			if(tile.getAllInventories().contains(slot.container))
			{
				if(!this.moveItemStackTo(origin, 0, 36, true))
					return ItemStack.EMPTY;
			}
			
			if(origin.getCount() == duped.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, origin);
		}
		
		return duped;
	}
}