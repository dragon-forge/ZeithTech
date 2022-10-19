package org.zeith.tech.modules.shared.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.*;
import org.zeith.tech.modules.generators.init.ItemsZT_Generators;
import org.zeith.tech.modules.processing.init.ItemsZT_Processing;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.transport.init.ItemsZT_Transport;
import org.zeith.tech.modules.world.init.ItemsZT_World;

import static org.zeith.tech.modules.shared.BaseZT.newItem;

@SimplyRegister
public interface ItemsZT
		extends ItemsZT_World, ItemsZT_Generators, ItemsZT_Transport, ItemsZT_Processing
{
	@RegistryName("copper_coil")
	Item COPPER_COIL = newItem(TagsZT.Items.COILS_COPPER);
	
	@RegistryName("gold_coil")
	Item GOLD_COIL = newItem(TagsZT.Items.COILS_GOLD);
	
	@RegistryName("bowl_of_resin")
	Item BOWL_OF_RESIN = BaseZT.newItem(p -> p.stacksTo(4));
	
	@RegistryName("latex")
	Item LATEX = BaseZT.newItem();
	
	@RegistryName("tree_tap")
	Item TREE_TAP = BaseZT.newItem();
	
	@RegistryName("plastic")
	Item PLASTIC = BaseZT.newItem();
	
	@RegistryName("motor")
	Item MOTOR = BaseZT.newItem();
	
	@RegistryName("circular_saw")
	Item CIRCULAR_SAW = BaseZT.newItem();
	
	@RegistryName("sawdust")
	Item SAWDUST = BaseZT.newItem();
	
	@RegistryName("bioluminescent_dust")
	Item BIOLUMINESCENT_DUST = BaseZT.newItem(Tags.Items.DUSTS_GLOWSTONE);
	
	@RegistryName("oil_sludge")
	Item OIL_SLUDGE = BaseZT.newItem();
	
	static @Setup void setup()
	{
		ComposterBlock.COMPOSTABLES.put(SAWDUST, 0.3F);
	}
}