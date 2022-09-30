package org.zeith.tech.init;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import org.zeith.hammerlib.annotations.*;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.common.entity.BoatZT;
import org.zeith.tech.common.items.ItemBoat;
import org.zeith.tech.init.items.CraftingMaterialsZT;
import org.zeith.tech.init.items.ToolsZT;

@SimplyRegister
public interface ItemsZT
		extends CraftingMaterialsZT, ToolsZT
{
	@RegistryName("hevea_sign")
	SignItem HEVEA_SIGN = new SignItem((new Item.Properties()).stacksTo(16).tab(ZeithTech.TAB), BlocksZT.HEVEA_SIGN, BlocksZT.HEVEA_WALL_SIGN);
	
	@RegistryName("hevea_boat")
	ItemBoat HEVEA_BOAT = new ItemBoat(false, BoatZT.Type.HEVEA, (new Item.Properties()).stacksTo(1).tab(ZeithTech.TAB));
	
	@RegistryName("hevea_chest_boat")
	ItemBoat HEVEA_CHEST_BOAT = new ItemBoat(true, BoatZT.Type.HEVEA, (new Item.Properties()).stacksTo(1).tab(ZeithTech.TAB));
	
	static @Setup void registerExtra()
	{
		TagAdapter.bindStaticTag(ItemTags.BOATS, HEVEA_BOAT);
		TagAdapter.bindStaticTag(ItemTags.CHEST_BOATS, HEVEA_CHEST_BOAT);
		TagAdapter.bindStaticTag(ItemTags.SIGNS, HEVEA_SIGN);
	}
}