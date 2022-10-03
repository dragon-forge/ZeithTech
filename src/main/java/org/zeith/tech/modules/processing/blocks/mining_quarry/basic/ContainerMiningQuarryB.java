package org.zeith.tech.modules.processing.blocks.mining_quarry.basic;

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

public class ContainerMiningQuarryB
		extends ContainerBaseMachine<TileMiningQuarryB>
{
	protected ContainerMiningQuarryB(TileMiningQuarryB tile, Player player, int windowId)
	{
		super(tile, player, windowId, List.of(tile.energyStored));
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(player.getInventory(), y + x * 9 + 9, 8 + y * 18, 84 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(player.getInventory(), x, 8 + x * 18, 142));
		
		this.addSlot(new SlotInput(tile.inventory, 0, 51, 53));
		this.addSlot(new SlotInput(tile.inventory, 1, 51, 17));
		this.addSlot(new SlotInput(tile.inventory, 2, 51, 35));
		
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 2; ++y)
				this.addSlot(new SlotOutput(tile.inventory, 3 + x + y * 3, 73 + 18 * x, 26 + 18 * y));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiMiningQuarryB(this, inv, label);
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
			} else
			{
				if(!this.moveItemStackTo(origin, 36, 39, true))
					return ItemStack.EMPTY;
			}
			
			if(origin.getCount() == duped.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, origin);
		}
		
		return duped;
	}
}
