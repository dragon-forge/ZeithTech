package org.zeith.tech.init.items;

import net.minecraft.world.item.Item;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.init.TagsZT;

import static org.zeith.tech.init.BaseZT.newItem;

@SimplyRegister
public interface CraftingMaterialsZT
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
	Item BOWL_OF_RESIN = newItem(p -> p.stacksTo(4));
	
	@RegistryName("latex")
	Item LATEX = newItem();
	
	@RegistryName("tree_tap")
	Item TREE_TAP = newItem();
}