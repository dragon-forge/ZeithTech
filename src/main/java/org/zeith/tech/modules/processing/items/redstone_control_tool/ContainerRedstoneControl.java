package org.zeith.tech.modules.processing.items.redstone_control_tool;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.inv.ComplexProgressHandler;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.client.screen.MenuWithProgressBars;
import org.zeith.hammerlib.net.properties.*;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.tile.RedstoneControl;

import java.util.List;

@SimplyRegister(prefix = "processing/")
public class ContainerRedstoneControl
		extends MenuWithProgressBars
		implements IScreenContainer
{
	@RegistryName("redstone_control")
	public static final MenuType<ContainerRedstoneControl> REDSTONE_CONTROL = IForgeMenuType.create((windowId, playerInv, extraData) ->
	{
		var lvl = playerInv.player.getLevel();
		var pos = extraData.readBlockPos();
		var be = lvl.getBlockEntity(pos);
		if(be != null)
			return be.getCapability(ZeithTechCapabilities.REDSTONE_CONTROL)
					.resolve()
					.map(control -> new ContainerRedstoneControl(windowId, playerInv, new RedstoneModeData(control, pos)))
					.orElse(null);
		return null;
	});
	
	public final RedstoneModeData source;
	
	public ContainerRedstoneControl(int windowId, Inventory playerInv, RedstoneModeData data)
	{
		super(REDSTONE_CONTROL, windowId, ComplexProgressHandler.withProperties(data.getProperties()));
		this.source = data;
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(playerInv, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(playerInv, x, 8 + x * 18, 142));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiRedstoneControl(this, inv, label);
	}
	
	@Override
	public boolean clickMenuButton(Player player, int button)
	{
		if(button == -1)
		{
			source.cycleMode(true);
			return true;
		} else if(button == 0)
		{
			source.cycleMode(false);
			return true;
		} else if(button >= 1 && button <= 15)
		{
			source.setThreshold((byte) button);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return source.isUsable(player);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotId)
	{
		return ItemStack.EMPTY;
	}
	
	public static class RedstoneModeData
	{
		public final RedstoneControl control;
		public final BlockPos pos;
		
		RedstoneControl.RedstoneMode modeEnum;
		
		public final PropertyByte threshold, mode;
		public final PropertyBool hasData;
		
		public RedstoneModeData(RedstoneControl control, BlockPos pos)
		{
			this.modeEnum = control.getMode();
			
			this.threshold = new PropertyByte();
			this.mode = new PropertyByte(DirectStorage.create(v -> modeEnum = RedstoneControl.RedstoneMode.values()[v], () -> (byte) modeEnum.ordinal()));
			this.hasData = new PropertyBool();
			
			this.threshold.setByte(control.getThreshold());
			
			this.control = control;
			this.pos = pos;
		}
		
		public List<IProperty<?>> getProperties()
		{
			return List.of(mode, threshold, hasData);
		}
		
		public byte getThreshold()
		{
			return threshold.getByte();
		}
		
		public RedstoneControl.RedstoneMode getMode()
		{
			return modeEnum;
		}
		
		public boolean isUsable(Player player)
		{
			return control != null
					&& pos != null
					&& player.distanceToSqr(Vec3.atCenterOf(pos)) < 64;
		}
		
		public void cycleMode(boolean reverse)
		{
			var vals = RedstoneControl.RedstoneMode.values();
			
			int newOrdinal = getMode().ordinal();
			if(reverse) --newOrdinal;
			else ++newOrdinal;
			
			if(newOrdinal < 0) newOrdinal += vals.length;
			else newOrdinal = newOrdinal % vals.length;
			
			var newMode = vals[newOrdinal];
			
			this.mode.setByte((byte) newOrdinal);
			this.control.setMode(newMode);
		}
		
		public void setThreshold(byte threshold)
		{
			this.threshold.setByte(threshold);
			this.control.setThreshold(threshold);
		}
	}
}
