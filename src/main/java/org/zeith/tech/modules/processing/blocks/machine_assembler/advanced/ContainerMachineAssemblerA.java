package org.zeith.tech.modules.processing.blocks.machine_assembler.advanced;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.items.redstone_control_tool.ItemRedstoneControlTool;
import org.zeith.tech.modules.shared.ui.SlotInput;
import org.zeith.tech.modules.shared.ui.SlotOutput;

import java.util.List;

public class ContainerMachineAssemblerA
		extends ContainerBaseMachine<TileMachineAssemblerA>
{
	public final Slot patternInput;
	
	protected ContainerMachineAssemblerA(TileMachineAssemblerA tile, Player player, int windowId)
	{
		super(tile, player, windowId, List.of(
				tile.craftingProgress,
				tile.craftTime,
				new PropertyInt(DirectStorage.create(tile.energy.fe::setEnergyStored, tile.energy.fe::getEnergyStored))
		));
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(player.getInventory(), y + x * 9 + 9, 8 + y * 18, 102 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(player.getInventory(), x, 8 + x * 18, 160));
		
		for(int y = 0; y < 5; ++y)
		{
			int start = 0;
			int end = 5;
			
			if(y == 0 || y == 4)
			{
				start = 1;
				end = 4;
			}
			
			for(x = start; x < end; ++x)
			{
				this.addSlot(new SlotInput(tile.craftingInventory, x + y * 5, 13 + x * 18, 9 + y * 18));
			}
		}
		
		this.addSlot(patternInput = new SlotInput(tile.patternInventory, 0, 177, 9));
		this.addSlot(new SlotOutput(tile.patternInventory, 1, 177, 45));
		
		this.addSlot(new SlotOutput(tile.resultInventory, 0, 143, 45));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiMachineAssemblerA(this, inv, label);
	}
	
	@Override
	public boolean isTileInventory(Container container, Player player)
	{
		return tile.getAllInventories().contains(container);
	}
	
	@Override
	public boolean clickMenuButton(Player player, int btn)
	{
		if(btn == 0)
		{
			ItemRedstoneControlTool.openRedstoneControl(player, tile);
			return true;
		}
		
		return super.clickMenuButton(player, btn);
	}
}