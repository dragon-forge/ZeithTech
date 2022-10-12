package org.zeith.tech.api.tile.slots;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.misc.IColorProvider;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class ContainerItemSlotAccess
		implements ISlotAccess<ItemStack>
{
	protected final Container container;
	protected final int slot;
	protected final SlotRole role;
	
	public ContainerItemSlotAccess(Container container, int slot, SlotRole role)
	{
		this.container = container;
		this.slot = slot;
		this.role = role;
	}
	
	@Override
	public Optional<Color> getColorOverride()
	{
		return Cast.optionally(container, IColorProvider.class)
				.map(IColorProvider::getColor);
	}
	
	@Override
	public SlotType<ItemStack> getType()
	{
		return SlotType.ITEM;
	}
	
	@Override
	public ItemStack get()
	{
		return container.getItem(slot);
	}
	
	@Override
	public void set(ItemStack val)
	{
		container.setItem(slot, val);
	}
	
	@Override
	public int insert(ItemStack val, boolean simulate)
	{
		if(!container.canPlaceItem(slot, val)) return 0;
		
		val = val.copy();
		
		var ctn = get();
		if(ctn.isEmpty())
		{
			int amt = val.getCount();
			if(!simulate)
				set(val);
			return amt;
		}
		
		if(ItemHandlerHelper.canItemStacksStack(ctn, val))
		{
			int maxStackSize = Math.min(val.getMaxStackSize(), getMaxAmount());
			int canAccept = maxStackSize - ctn.getCount();
			int transfer = Math.min(canAccept, val.getCount());
			if(!simulate) ctn.grow(transfer);
			return transfer;
		}
		
		return 0;
	}
	
	@Override
	public ItemStack extract(int amount, boolean simulate)
	{
		var ctn = get().copy();
		if(ctn.isEmpty()) return ItemStack.EMPTY;
		amount = Math.min(ctn.getCount(), amount);
		var separated = ctn.split(amount);
		if(!simulate) set(ctn);
		return separated;
	}
	
	@Override
	public int getAmount()
	{
		return get().getCount();
	}
	
	@Override
	public int getMaxAmount()
	{
		var ctn = get();
		return Math.min(container.getMaxStackSize(), ctn.isEmpty() ? Integer.MAX_VALUE : ctn.getMaxStackSize());
	}
	
	@Override
	public boolean belongsTo(Object owner)
	{
		return owner == container;
	}
	
	private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> new IItemHandler()
	{
		@Override
		public int getSlots()
		{
			return 1;
		}
		
		@Override
		public @NotNull ItemStack getStackInSlot(int slot)
		{
			return get();
		}
		
		@Override
		public @NotNull ItemStack insertItem(int ignoredSlotInt, @NotNull ItemStack stack, boolean simulate)
		{
			if(!role.input()) return stack;
			
			if(stack.isEmpty())
				return ItemStack.EMPTY;
			
			ItemStack stackInSlot = container.getItem(slot);
			
			int m;
			if(!stackInSlot.isEmpty())
			{
				if(stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
					return stack;
				if(!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
					return stack;
				if(!container.canPlaceItem(slot, stack))
					return stack;
				m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();
				if(stack.getCount() <= m)
				{
					if(!simulate)
					{
						ItemStack copy = stack.copy();
						copy.grow(stackInSlot.getCount());
						container.setItem(slot, copy);
						container.setChanged();
					}
					return ItemStack.EMPTY;
				} else
				{
					// copy the stack to not modify the original one
					stack = stack.copy();
					if(!simulate)
					{
						ItemStack copy = stack.split(m);
						copy.grow(stackInSlot.getCount());
						container.setItem(slot, copy);
						container.setChanged();
					} else stack.shrink(m);
					return stack;
				}
			} else
			{
				if(!container.canPlaceItem(slot, stack))
					return stack;
				m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
				if(m < stack.getCount())
				{
					stack = stack.copy();
					if(!simulate)
					{
						container.setItem(slot, stack.split(m));
						container.setChanged();
					} else stack.shrink(m);
					return stack;
				} else
				{
					if(!simulate)
					{
						container.setItem(slot, stack);
						container.setChanged();
					}
					return ItemStack.EMPTY;
				}
			}
		}
		
		@Override
		public @NotNull ItemStack extractItem(int ignoredSlotInt, int amount, boolean simulate)
		{
			if(!role.output()) return ItemStack.EMPTY;
			if(amount == 0)
				return ItemStack.EMPTY;
			ItemStack stackInSlot = container.getItem(slot);
			if(stackInSlot.isEmpty())
				return ItemStack.EMPTY;
			if(simulate)
			{
				if(stackInSlot.getCount() < amount) return stackInSlot.copy();
				else
				{
					ItemStack copy = stackInSlot.copy();
					copy.setCount(amount);
					return copy;
				}
			} else
			{
				int m = Math.min(stackInSlot.getCount(), amount);
				ItemStack decrStackSize = container.removeItem(slot, m);
				container.setChanged();
				return decrStackSize;
			}
		}
		
		@Override
		public int getSlotLimit(int ignoredSlotInt)
		{
			return getMaxAmount();
		}
		
		@Override
		public boolean isItemValid(int ignoredSlotInt, @NotNull ItemStack stack)
		{
			return container.canPlaceItem(slot, stack);
		}
	});
	
	@Override
	public @NotNull <CAP> LazyOptional<CAP> getCapability(@NotNull Capability<CAP> cap)
	{
		if(cap == ForgeCapabilities.ITEM_HANDLER)
			return itemCapability.cast();
		return ISlotAccess.super.getCapability(cap);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ContainerItemSlotAccess) obj;
		return Objects.equals(this.container, that.container) &&
				this.slot == that.slot &&
				Objects.equals(this.role, that.role);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(container, slot, role);
	}
	
	@Override
	public String toString()
	{
		return "ContainerItemSlotAccess[" +
				"container=" + container + ", " +
				"slot=" + slot + ", " +
				"role=" + role + ']';
	}
	
}