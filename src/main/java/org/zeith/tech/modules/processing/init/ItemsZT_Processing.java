package org.zeith.tech.modules.processing.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.modules.processing.items.*;
import org.zeith.tech.modules.processing.items.redstone_control_tool.ItemRedstoneControlTool;
import org.zeith.tech.modules.shared.BaseZT;

import java.util.Optional;

@SimplyRegister(prefix = "processing/")
public interface ItemsZT_Processing
{
	@RegistryName("iron_hammer")
	ItemHammer IRON_HAMMER = new ItemHammer(BaseZT.itemProps(), Optional.of(Tags.Items.INGOTS_IRON));
	
	@RegistryName("wire_cutter")
	ItemWireCutter WIRE_CUTTER = new ItemWireCutter(BaseZT.itemProps(), Tiers.IRON);
	
	@RegistryName("mining_heads/iron")
	ItemMiningHead IRON_MINING_HEAD = new ItemMiningHead(Tiers.IRON, ZeithTechAPI.id("multi_tool/mining_heads/iron"), BaseZT.itemProps());
	
	@RegistryName("mining_heads/diamond")
	ItemMiningHead DIAMOND_MINING_HEAD = new ItemMiningHead(Tiers.DIAMOND, ZeithTechAPI.id("multi_tool/mining_heads/diamond"), BaseZT.itemProps());
	
	@RegistryName("mining_heads/tungsten")
	ItemMiningHead TUNGSTEN_MINING_HEAD = new ItemMiningHead(ZeithTechAPI.TUNGSTEN_TIER, ZeithTechAPI.id("multi_tool/mining_heads/tungsten"), BaseZT.itemProps());
	
	@RegistryName("mining_heads/netherite")
	ItemMiningHead NETHERITE_MINING_HEAD = new ItemMiningHead(Tiers.NETHERITE, ZeithTechAPI.id("multi_tool/mining_heads/netherite"), BaseZT.itemProps());
	
	@RegistryName("mining_head")
	Item MINING_HEAD = BaseZT.newItem();
	
	@RegistryName("redstone_control_tool")
	ItemRedstoneControlTool REDSTONE_CONTROL_TOOL = new ItemRedstoneControlTool(BaseZT.itemProps().stacksTo(1));
	
	@RegistryName("recipe_pattern")
	ItemRecipePattern RECIPE_PATTERN = new ItemRecipePattern(BaseZT.itemProps().stacksTo(1));
	
	@RegistryName("composite_brick")
	Item COMPOSITE_BRICK = BaseZT.newItem();
}