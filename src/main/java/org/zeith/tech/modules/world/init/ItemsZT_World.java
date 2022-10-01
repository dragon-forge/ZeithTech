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
	
	static @Setup void registerExtra()
	{
		TagAdapter.bindStaticTag(ItemTags.BOATS, HEVEA_BOAT);
		TagAdapter.bindStaticTag(ItemTags.CHEST_BOATS, HEVEA_CHEST_BOAT);
		TagAdapter.bindStaticTag(ItemTags.SIGNS, HEVEA_SIGN);
	}
}