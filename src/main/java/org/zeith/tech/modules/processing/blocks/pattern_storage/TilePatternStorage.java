package org.zeith.tech.modules.processing.blocks.pattern_storage;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.tiles.TileSyncable;
import org.zeith.hammerlib.util.SidedLocal;
import org.zeith.tech.api.item.IRecipePatternItem;
import org.zeith.tech.api.tile.ILoadableFromItem;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;

import java.util.Collections;
import java.util.stream.IntStream;

public class TilePatternStorage
		extends TileSyncable
		implements IContainerTile, ILoadableFromItem
{
	public final NonNullList<ItemStack> patterns = NonNullList.create();
	
	public final PropertyInt slotCount = new PropertyInt();
	
	public TilePatternStorage(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.PATTERN_STORAGE, pos, state);
		dispatcher.registerProperty("slots", slotCount);
	}
	
	@Override
	public AbstractContainerMenu openContainer(Player player, int windowId)
	{
		return new ContainerPatternStorage(this, player.getInventory(), windowId);
	}
	
	@Override
	public CompoundTag writeNBT(CompoundTag nbt)
	{
		nbt = super.writeNBT(nbt);
		
		ListTag itemsTag = new ListTag();
		for(var stack : patterns)
			if(!stack.isEmpty())
				itemsTag.add(stack.save(new CompoundTag()));
		nbt.put("Items", itemsTag);
		
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundTag nbt)
	{
		patterns.clear();
		ListTag itemsTag = nbt.getList("Items", 10);
		for(var i = 0; i < itemsTag.size(); ++i)
		{
			var item = ItemStack.of(itemsTag.getCompound(i));
			if(!item.isEmpty())
				patterns.add(item);
		}
		
		super.readNBT(nbt);
	}
	
	public final WorldlyContainer itemHandler = new ItemHandler();
	public final SidedLocal<Container> guiSlots = new SidedLocal<>(side -> side.isClient() ? new ClientContainer() : itemHandler);
	private final LazyOptional<IItemHandlerModifiable>[] itemStorage = SidedInvWrapper.create(itemHandler, Direction.values());
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ITEM_HANDLER)
			return itemStorage[side != null ? side.ordinal() : 0].cast();
		return super.getCapability(cap, side);
	}
	
	@Override
	public void loadFromItem(ItemStack stack)
	{
		patterns.clear();
		var tag = stack.getTag();
		if(tag != null && tag.contains("Patterns"))
		{
			var itemsTag = tag.getList("Patterns", Tag.TAG_COMPOUND);
			for(var i = 0; i < itemsTag.size(); ++i)
			{
				var item = ItemStack.of(itemsTag.getCompound(i));
				if(!item.isEmpty())
					patterns.add(item);
			}
		}
	}
	
	@Override
	public void saveToItem(ItemStack stack)
	{
		if(patterns.isEmpty())
			stack.removeTagKey("Patterns");
		else
		{
			ListTag itemsTag = new ListTag();
			for(var s0 : patterns) if(!s0.isEmpty()) itemsTag.add(s0.save(new CompoundTag()));
			stack.addTagElement("Patterns", itemsTag);
		}
	}
	
	public ItemStack generateItem(ItemLike item)
	{
		ItemStack drop = new ItemStack(item);
		saveToItem(drop);
		return drop;
	}
	
	protected class ClientContainer
			implements Container
	{
		public final NonNullList<ItemStack> items = NonNullList.create();
		
		public void ensureCapacity()
		{
			int size = slotCount.getInt() + 1;
			ensureCapacity(size);
			while(items.size() > size) items.remove(0);
		}
		
		public void ensureCapacity(int size)
		{
			while(items.size() < size) items.add(ItemStack.EMPTY);
		}
		
		@Override
		public int getContainerSize()
		{
			ensureCapacity();
			return slotCount.getInt();
		}
		
		@Override
		public boolean isEmpty()
		{
			return items.stream().allMatch(ItemStack::isEmpty);
		}
		
		@Override
		public ItemStack getItem(int slot)
		{
			ensureCapacity(slot + 1);
			return items.get(slot);
		}
		
		@Override
		public ItemStack removeItem(int slot, int amount)
		{
			ensureCapacity(slot + 1);
			return items.get(slot).split(amount);
		}
		
		@Override
		public ItemStack removeItemNoUpdate(int slot)
		{
			ensureCapacity();
			ItemStack was = items.get(slot);
			items.set(slot, ItemStack.EMPTY);
			return was;
		}
		
		@Override
		public void setItem(int slot, ItemStack stack)
		{
			ensureCapacity(slot + 1);
			items.set(slot, stack);
		}
		
		@Override
		public void setChanged()
		{
		}
		
		@Override
		public boolean stillValid(Player player)
		{
			return false;
		}
		
		@Override
		public void clearContent()
		{
			Collections.fill(items, ItemStack.EMPTY);
		}
		
		@Override
		public boolean canPlaceItem(int slot, ItemStack stack)
		{
			return itemHandler.canPlaceItem(slot, stack);
		}
	}
	
	protected class ItemHandler
			implements WorldlyContainer
	{
		@Override
		public int[] getSlotsForFace(Direction side)
		{
			return IntStream.rangeClosed(0, patterns.size()).toArray();
		}
		
		@Override
		public int getMaxStackSize()
		{
			return 1;
		}
		
		@Override
		public boolean canPlaceItem(int slot, ItemStack stack)
		{
			return stack.getItem() instanceof IRecipePatternItem pat && pat.getProvidedRecipe(stack) != null;
		}
		
		@Override
		public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction)
		{
			return canPlaceItem(slot, stack);
		}
		
		@Override
		public boolean canTakeItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction)
		{
			return true;
		}
		
		@Override
		public int getContainerSize()
		{
			return patterns.size() + 1;
		}
		
		@Override
		public boolean isEmpty()
		{
			return patterns.isEmpty();
		}
		
		@Override
		public ItemStack getItem(int slot)
		{
			return slot >= patterns.size() ? ItemStack.EMPTY : patterns.get(slot);
		}
		
		@Override
		public ItemStack removeItem(int slot, int amount)
		{
			if(slot < patterns.size())
			{
				var stack = patterns.get(slot);
				var split = stack.split(amount);
				if(stack.isEmpty()) patterns.remove(slot);
				return split;
			}
			
			return ItemStack.EMPTY;
		}
		
		@Override
		public ItemStack removeItemNoUpdate(int slot)
		{
			if(slot < patterns.size()) return patterns.remove(slot);
			return ItemStack.EMPTY;
		}
		
		@Override
		public void setItem(int slot, ItemStack stack)
		{
			if(slot >= patterns.size())
			{
				patterns.add(stack);
				return;
			}
			
			patterns.set(slot, stack);
		}
		
		@Override
		public void setChanged()
		{
			patterns.removeIf(ItemStack::isEmpty);
			sync();
		}
		
		@Override
		public void clearContent()
		{
			patterns.clear();
		}
		
		@Override
		public boolean stillValid(Player player)
		{
			return false;
		}
	}
}