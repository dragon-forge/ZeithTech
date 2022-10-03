package org.zeith.tech.modules.processing.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.items.*;
import org.zeith.tech.modules.shared.BaseZT;

import java.util.Optional;

@SimplyRegister
public interface ItemsZT_Processing
{
	@RegistryName("processing/iron_hammer")
	ItemHammer IRON_HAMMER = new ItemHammer(BaseZT.itemProps(), Optional.of(Tags.Items.INGOTS_IRON));
	
	@RegistryName("processing/wire_cutter")
	ItemWireCutter WIRE_CUTTER = new ItemWireCutter(BaseZT.itemProps(), Tiers.IRON);
	
	@RegistryName("processing/mining_heads/iron")
	ItemMiningHead IRON_MINING_HEAD = new ItemMiningHead(Tiers.IRON, BaseZT.itemProps());
	
	@RegistryName("processing/mining_heads/diamond")
	ItemMiningHead DIAMOND_MINING_HEAD = new ItemMiningHead(Tiers.DIAMOND, BaseZT.itemProps());
	
	@RegistryName("processing/mining_heads/netherite")
	ItemMiningHead NETHERITE_MINING_HEAD = new ItemMiningHead(Tiers.NETHERITE, BaseZT.itemProps());
	
	@RegistryName("processing/mining_head")
	Item MINING_HEAD = BaseZT.newItem();
}