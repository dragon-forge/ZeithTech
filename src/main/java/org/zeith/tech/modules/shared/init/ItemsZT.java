package org.zeith.tech.modules.shared.init;

import net.minecraft.world.item.Item;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.init.ItemsZT_Processing;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.world.init.ItemsZT_World;

import static org.zeith.tech.modules.shared.BaseZT.newItem;

@SimplyRegister
public interface ItemsZT
		extends ItemsZT_World, ItemsZT_Processing
{
	@RegistryName("copper_coil")
	Item COPPER_COIL = newItem(TagsZT.Items.COILS_COPPER);
	
	@RegistryName("gold_coil")
	Item GOLD_COIL = newItem(TagsZT.Items.COILS_GOLD);
	
	@RegistryName("gold_plate")
	Item GOLD_PLATE = newItem(TagsZT.Items.PLATES_GOLD);
	
	@RegistryName("iron_plate")
	Item IRON_PLATE = newItem(TagsZT.Items.PLATES_IRON);
	
	@RegistryName("copper_plate")
	Item COPPER_PLATE = newItem(TagsZT.Items.PLATES_COPPER);
	
	@RegistryName("tin_plate")
	Item TIN_PLATE = newItem(TagsZT.Items.PLATES_TIN);
	
	@RegistryName("lead_plate")
	Item LEAD_PLATE = newItem(TagsZT.Items.PLATES_LEAD);
	
	@RegistryName("aluminum_plate")
	Item ALUMINUM_PLATE = newItem(TagsZT.Items.PLATES_ALUMINUM);
	
	@RegistryName("bowl_of_resin")
	Item BOWL_OF_RESIN = BaseZT.newItem(p -> p.stacksTo(4));
	
	@RegistryName("latex")
	Item LATEX = BaseZT.newItem();
	
	@RegistryName("tree_tap")
	Item TREE_TAP = BaseZT.newItem();
}