package org.zeith.tech.modules.shared.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.ComposterBlock;
import org.zeith.hammerlib.annotations.*;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.modules.generators.init.ItemsZT_Generators;
import org.zeith.tech.modules.processing.init.ItemsZT_Processing;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.shared.items.*;
import org.zeith.tech.modules.shared.items.multitool.ItemMultiTool;
import org.zeith.tech.modules.shared.items.multitool.ItemMultiToolMotor;
import org.zeith.tech.modules.transport.init.ItemsZT_Transport;
import org.zeith.tech.modules.world.init.ItemsZT_World;

import static org.zeith.tech.modules.shared.BaseZT.newItem;

@SimplyRegister
public interface ItemsZT
		extends ItemsZT_World, ItemsZT_Generators, ItemsZT_Transport, ItemsZT_Processing, GearsZT, DustsZT
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
	
	@RegistryName("oil_sludge")
	Item OIL_SLUDGE = BaseZT.newItem();
	
	@RegistryName("accumulator")
	ItemAccumulator ACCUMULATOR = new ItemAccumulator(EnergyTier.EXTRA_LOW_VOLTAGE.capacity() * 10, BaseZT.itemProps());
	
	@RegistryName("basic_multi_tool")
	ItemMultiTool BASIC_MULTI_TOOL = new ItemMultiTool(BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/basic"));
	
	@RegistryName("multi_tool_iron_motor")
	ItemMultiToolMotor MULTI_TOOL_IRON_MOTOR = new ItemMultiToolMotor(Tiers.IRON, BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/motors/iron"));
	
	@RegistryName("multi_tool_diamond_motor")
	ItemMultiToolMotor MULTI_TOOL_DIAMOND_MOTOR = new ItemMultiToolMotor(Tiers.DIAMOND, BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/motors/diamond"));
	
	@RegistryName("multi_tool_tungsten_motor")
	ItemMultiToolMotor MULTI_TOOL_TUNGSTEN_MOTOR = new ItemMultiToolMotor(ZeithTechAPI.TUNGSTEN_TIER, BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/motors/tungsten"));
	
	@RegistryName("multi_tool_netherite_motor")
	ItemMultiToolMotor MULTI_TOOL_NETHERITE_MOTOR = new ItemMultiToolMotor(Tiers.NETHERITE, BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/motors/netherite"));
	
	static @Setup void setup()
	{
		ComposterBlock.COMPOSTABLES.put(SAWDUST, 0.3F);
	}
}