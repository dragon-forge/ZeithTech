package org.zeith.tech.init.items;

import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.common.items.ItemHammer;

import java.util.Optional;

import static org.zeith.tech.init.BaseZT.itemProps;

@SimplyRegister
public interface ToolsZT
{
	@RegistryName("iron_hammer")
	ItemHammer IRON_HAMMER = new ItemHammer(itemProps().durability(256), Optional.of(Tags.Items.INGOTS_IRON));
}