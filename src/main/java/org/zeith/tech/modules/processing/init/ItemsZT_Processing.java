package org.zeith.tech.modules.processing.init;

import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.items.ItemHammer;
import org.zeith.tech.modules.processing.items.ItemWireCutter;
import org.zeith.tech.modules.shared.BaseZT;

import java.util.Optional;

@SimplyRegister
public interface ItemsZT_Processing
{
	@RegistryName("processing/iron_hammer")
	ItemHammer IRON_HAMMER = new ItemHammer(BaseZT.itemProps(), Optional.of(Tags.Items.INGOTS_IRON));
	
	@RegistryName("processing/wire_cutter")
	ItemWireCutter WIRE_CUTTER = new ItemWireCutter(BaseZT.itemProps(), Tiers.IRON);
}