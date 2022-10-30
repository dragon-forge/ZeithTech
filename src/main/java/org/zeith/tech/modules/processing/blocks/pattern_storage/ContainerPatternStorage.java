package org.zeith.tech.modules.processing.blocks.pattern_storage;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.tech.modules.shared.ui.SlotInput;

public class ContainerPatternStorage
		extends AbstractContainerMenu
		implements IScreenContainer
{
	public final TilePatternStorage tile;
	
	protected ContainerPatternStorage(TilePatternStorage tile, Inventory playerInv, int windowId)
	{
		super(ContainerAPI.TILE_CONTAINER, windowId);
		this.tile = tile;
		
		for(int i = 0; i < 5; ++i)
			for(int j = 0; j < 9; ++j)
				this.addSlot(new SlotInput(tile.guiSlots.get(), i * 9 + j, 8 + j * 18, 18 + i * 18));
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(playerInv, y + x * 9 + 9, 8 + y * 18, 122 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(playerInv, x, 8 + x * 18, 180));
	}
	
	@Override
	public void removed(Player player)
	{
		super.removed(player);
		tile.close(player);
	}
	
	@Override
	public boolean clickMenuButton(Player player, int id)
	{
		if(id >= 0 && id <= 255)
			scrollTo(id / 255F);
		
		return true;
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotIdx)
	{
		var slot = getSlot(slotIdx);
		
		if(slot != null && slot.hasItem())
		{
			if(slot.container == player.getInventory())
			{
				var ctr = tile.itemHandler;
				var lastSlot = ctr.getContainerSize() - 1;
				if(ctr.canPlaceItem(lastSlot, slot.getItem()))
				{
					ctr.setItem(lastSlot, slot.remove(slot.getMaxStackSize()));
				}
			} else if(slot.container == tile.guiSlots.get())
			{
				ItemStack origin = slot.getItem();
				var duped = origin.copy();
				if(!this.moveItemStackTo(origin, 45, 45 + 36, true))
					return ItemStack.EMPTY;
				if(origin.getCount() == duped.getCount())
					return ItemStack.EMPTY;
				slot.onTake(player, origin);
			}
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return !tile.isRemoved() && tile.getBlockPos().distToCenterSqr(player.position()) < 64;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiPatternStorage(this, inv, label);
	}
	
	public void scrollTo(float scrollProgress)
	{
		scrollProgress = Mth.clamp(scrollProgress, 0F, 1F);
		
		int maxScroll = (tile.patterns.size() + 9) / 9 - 5;
		int scrollOffset = (int) ((double) (scrollProgress * (float) maxScroll) + 0.5D);
		
		for(int row = 0; row < 5; ++row)
		{
			for(int col = 0; col < 9; ++col)
			{
				int slotIdx = col + row * 9;
				int invIdx = col + (row + scrollOffset) * 9;
				
				var slot = getSlot(slotIdx);
				ObfuscationReflectionHelper.setPrivateValue(Slot.class, slot, invIdx, "f_40217_");
			}
		}
	}
	
	public boolean canScroll()
	{
		return tile.patterns.size() >= 45;
	}
}