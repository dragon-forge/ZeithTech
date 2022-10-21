package org.zeith.tech.modules.shared.items.multitool;

import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.tech.api.item.IAccumulatorItem;
import org.zeith.tech.api.item.multitool.*;
import org.zeith.tech.modules.shared.ui.SlotInput;
import org.zeith.tech.modules.shared.ui.SlotNonInteractable;

@SimplyRegister
public class ContainerMultiTool
		extends AbstractContainerMenu
{
	@RegistryName("multi_tool")
	public static final MenuType<ContainerMultiTool> MULTI_TOOL = IForgeMenuType.create((windowId, playerInv, extraData) -> new ContainerMultiTool(windowId, playerInv));
	
	private final MultiToolInventory inventory = new MultiToolInventory();
	
	public Inventory playerInv;
	public ItemStack multiToolStack;
	public int multiToolSlot;
	
	private boolean isLoading = false;
	
	public ContainerMultiTool(int windowId, Inventory playerInv)
	{
		super(MULTI_TOOL, windowId);
		
		this.playerInv = playerInv;
		multiToolStack = playerInv.getSelected();
		multiToolSlot = playerInv.selected;
		
		for(int y = 0; y < 3; ++y)
			for(int x = 0; x < 9; ++x)
				this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
		for(int x = 0; x < 9; ++x)
			this.addSlot(x == multiToolSlot ? new SlotNonInteractable(playerInv, x, 8 + x * 18, 142) : new Slot(playerInv, x, 8 + x * 18, 142));
		
		syncMultiTool(multiToolStack);
		
		addSlot(new SlotInput(inventory, 0, 52, 35));
		addSlot(new SlotInput(inventory, 1, 80, 35));
		addSlot(new SlotInput(inventory, 2, 108, 35));
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
			
			if(slot.container == player.getInventory())
			{
				if(!this.moveItemStackTo(origin, 36, slots.size(), true))
					return ItemStack.EMPTY;
			} else if(!this.moveItemStackTo(origin, 0, 36, true))
				return ItemStack.EMPTY;
			
			if(origin.getCount() == duped.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, origin);
		}
		
		return duped;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return player.getInventory().selected == multiToolSlot
				&& player.getInventory().getSelected() == multiToolStack;
	}
	
	@Override
	public void slotsChanged(Container ctr)
	{
		super.slotsChanged(ctr);
		
		if(ctr == inventory && !isLoading)
		{
			isLoading = true;
			
			if(multiToolStack.isEmpty() || !(multiToolStack.getItem() instanceof IMultiToolItem tool))
			{
				playerInv.player.closeContainer();
				return;
			}
			
			var accum = inventory.getItem(1);
			if(!tool.setAccumulator(multiToolStack, accum))
			{
				if(!playerInv.add(accum)) playerInv.player.drop(accum, false, false);
				inventory.setItem(1, ItemStack.EMPTY);
				tool.setAccumulator(multiToolStack, ItemStack.EMPTY);
			}
			
			var motor = inventory.getItem(2);
			if(!tool.setMotor(multiToolStack, motor))
			{
				if(!playerInv.add(motor)) playerInv.player.drop(motor, false, false);
				inventory.setItem(2, ItemStack.EMPTY);
				tool.setMotor(multiToolStack, ItemStack.EMPTY);
			}
			
			var head = inventory.getItem(0);
			if(inventory.getItem(2).isEmpty()
					|| !(inventory.getItem(2).getItem() instanceof IMultiToolMotor m)
					|| !m.supportsHead(inventory.getItem(2), head, multiToolStack)
					|| !tool.setHead(multiToolStack, head))
			{
				if(!playerInv.add(head)) playerInv.player.drop(head, false, false);
				inventory.setItem(0, ItemStack.EMPTY);
				tool.setHead(multiToolStack, ItemStack.EMPTY);
			}
			
			syncMultiTool(multiToolStack);
			isLoading = false;
		}
	}
	
	public void syncMultiTool(ItemStack newStack)
	{
		if(newStack.isEmpty() || !(newStack.getItem() instanceof IMultiToolItem tool))
		{
			playerInv.player.closeContainer();
			return;
		}
		
		isLoading = true;
		
		playerInv.setItem(multiToolSlot, newStack);
		multiToolStack = playerInv.getItem(multiToolSlot);
		
		inventory.setItem(0, tool.getHead(newStack).map(Tuple::getB).orElse(ItemStack.EMPTY));
		inventory.setItem(1, tool.getAccumulator(newStack).map(Tuple::getB).orElse(ItemStack.EMPTY));
		inventory.setItem(2, tool.getMotor(newStack).map(Tuple::getB).orElse(ItemStack.EMPTY));
		
		isLoading = false;
	}
	
	private class MultiToolInventory
			extends SimpleInventory
	{
		public MultiToolInventory()
		{
			super(3);
			stackSizeLimit = 1;
			isStackValid = this::isItemValid;
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack)
		{
			if(stack.isEmpty()) return false;
			
			if(slot == 0) return stack.getItem() instanceof IMultiToolHead
					&& (getItem(2).getItem() instanceof IMultiToolMotor motor && motor.supportsHead(getItem(2), stack, multiToolStack));
			
			if(slot == 1) return stack.getItem() instanceof IAccumulatorItem;
			if(slot == 2) return stack.getItem() instanceof IMultiToolMotor;
			return false;
		}
		
		@Override
		public void setItem(int slot, ItemStack item)
		{
			super.setItem(slot, item);
			slotsChanged(this);
		}
		
		@Override
		public void setChanged()
		{
			super.setChanged();
			slotsChanged(this);
		}
	}
}