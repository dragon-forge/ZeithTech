package org.zeith.tech.init.items;

import net.minecraft.world.item.Item;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.init.TagsZT;

import static org.zeith.tech.init.BaseZT.newItem;

@SimplyRegister
public class CraftingMaterialsZT
{
	@RegistryName("copper_coil")
	public static final Item COPPER_COIL = newItem(TagsZT.Items.COILS_COPPER);
	
	@RegistryName("gold_coil")
	public static final Item GOLD_COIL = newItem(TagsZT.Items.COILS_GOLD);
	
	@RegistryName("gold_plate")
	public static final Item GOLD_PLATE = newItem(TagsZT.Items.PLATES_GOLD);
	
	@RegistryName("iron_plate")
	public static final Item IRON_PLATE = newItem(TagsZT.Items.PLATES_IRON);
	
	@RegistryName("copper_plate")
	public static final Item COPPER_PLATE = newItem(TagsZT.Items.PLATES_COPPER);
	
	@RegistryName("tin_plate")
	public static final Item TIN_PLATE = newItem(TagsZT.Items.PLATES_TIN);
	
	@RegistryName("lead_plate")
	public static final Item LEAD_PLATE = newItem(TagsZT.Items.PLATES_LEAD);
	
	@RegistryName("aluminum_plate")
	public static final Item ALUMINUM_PLATE = newItem(TagsZT.Items.PLATES_ALUMINUM);
}