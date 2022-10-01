package org.zeith.tech.modules.world.init;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import org.zeith.hammerlib.annotations.*;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.world.entity.BoatZT;
import org.zeith.tech.modules.world.items.ItemBoat;

import static org.zeith.tech.modules.shared.BaseZT.newItem;

@SimplyRegister
public interface ItemsZT_World
{
	// Hevea tree stuff
	
	@RegistryName("hevea_sign")
	SignItem HEVEA_SIGN = new SignItem((new Item.Properties()).stacksTo(16).tab(ZeithTech.TAB), BlocksZT_World.HEVEA_SIGN, BlocksZT_World.HEVEA_WALL_SIGN);
	
	@RegistryName("hevea_boat")
	ItemBoat HEVEA_BOAT = new ItemBoat(false, BoatZT.Type.HEVEA, (new Item.Properties()).stacksTo(1).tab(ZeithTech.TAB));
	
	@RegistryName("hevea_chest_boat")
	ItemBoat HEVEA_CHEST_BOAT = new ItemBoat(true, BoatZT.Type.HEVEA, (new Item.Properties()).stacksTo(1).tab(ZeithTech.TAB));
	
	// Raw ores
	
	@RegistryName("raw_tin")
	Item RAW_TIN = newItem(TagsZT.Items.RAW_MATERIALS_TIN);
	@RegistryName("raw_lead")
	Item RAW_LEAD = newItem(TagsZT.Items.RAW_MATERIALS_LEAD);
	@RegistryName("raw_aluminum")
	Item RAW_ALUMINUM = newItem(TagsZT.Items.RAW_MATERIALS_ALUMINUM);
	
	// Ingots
	
	@RegistryName("tin_ingot")
	Item TIN_INGOT = newItem(TagsZT.Items.INGOTS_TIN);
	@RegistryName("lead_ingot")
	Item LEAD_INGOT = newItem(TagsZT.Items.INGOTS_LEAD);
	@RegistryName("aluminum_ingot")
	Item ALUMINUM_INGOT = newItem(TagsZT.Items.INGOTS_ALUMINUM);
	
	@RegistryName("zinc_ingot")
	Item ZINC_INGOT = newItem(TagsZT.Items.INGOTS_ZINC);
	
	@RegistryName("tungsten_ingot")
	Item TUNGSTEN_INGOT = newItem(TagsZT.Items.INGOTS_TUNGSTEN);
	
	// Plates
	
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
	
	@RegistryName("zinc_plate")
	Item ZINC_PLATE = newItem(TagsZT.Items.PLATES_ZINC);
	
	@RegistryName("tungsten_plate")
	Item TUNGSTEN_PLATE = newItem(TagsZT.Items.PLATES_TUNGSTEN);
	
	static @Setup void registerExtra()
	{
		TagAdapter.bindStaticTag(ItemTags.BOATS, HEVEA_BOAT);
		TagAdapter.bindStaticTag(ItemTags.CHEST_BOATS, HEVEA_CHEST_BOAT);
		TagAdapter.bindStaticTag(ItemTags.SIGNS, HEVEA_SIGN);
	}
}