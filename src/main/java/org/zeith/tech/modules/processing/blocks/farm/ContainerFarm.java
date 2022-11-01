package org.zeith.tech.modules.processing.blocks.farm;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.core.slot.FluidSlotBase;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.shared.ui.SlotInput;

import java.util.List;

public class ContainerFarm
		extends ContainerBaseMachine<TileFarm>
{
	public static final ResourceLocation BONE_MEAL = ZeithTechAPI.id("slots/bone_meal");
	public static final ResourceLocation FARM_SOC = ZeithTechAPI.id("slots/farm_soc");
	public static final ResourceLocation FARMLAND = ZeithTechAPI.id("slots/farmland");
	public static final ResourceLocation WHEAT = ZeithTechAPI.id("slots/wheat");
	public static final ResourceLocation WHEAT_SEEDS = ZeithTechAPI.id("slots/wheat_seeds");
	
	protected ContainerFarm(TileFarm tile, Player player, int windowId)
	{
		super(tile, player, windowId, List.of(
				new PropertyInt(DirectStorage.create(tile.energy.fe::setEnergyStored, tile.energy.fe::getEnergyStored)),
				new PropertyInt(DirectStorage.create(v -> tile.cooldown = v, () -> tile.cooldown))
		));
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(player.getInventory(), y + x * 9 + 9, 8 + y * 18, 92 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(player.getInventory(), x, 8 + x * 18, 150));
		
		addSlot(new SlotInput(tile.algorithmInventory, 0, 47, 15).withIcon(FARM_SOC));
		addSlot(new SlotInput(tile.fertilizerInventory, 0, 47, 63).withIcon(BONE_MEAL));
		
		for(int i = 1; i >= 0; --i)
			addSlot(new SlotInput(tile.soilInventory, i, 90, 11 + i * 18).withIcon(FARMLAND));
		
		for(int i = 3; i >= 0; --i)
			addSlot(new SlotInput(tile.plantInventory, i, 112 + (i % 2) * 18, 11 + i / 2 * 18).withIcon(WHEAT_SEEDS));
		
		for(int i = 5; i >= 0; --i)
			addSlot(new SlotInput(tile.resultInventory, i, 92 + (i % 3) * 18, 49 + i / 3 * 18).withIcon(WHEAT));
		
		addSlot(new FluidSlotBase.FluidTankSlot(tile.water, 25, 15));
	}
	
	public static void registerSprites(ZeithTechAPI api)
	{
		api.registerItemSprite(BONE_MEAL);
		api.registerItemSprite(FARM_SOC);
		api.registerItemSprite(FARMLAND);
		api.registerItemSprite(WHEAT);
		api.registerItemSprite(WHEAT_SEEDS);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiFarm(this, inv, label);
	}
}