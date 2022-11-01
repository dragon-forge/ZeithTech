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
import org.zeith.tech.modules.shared.items.multitool.*;
import org.zeith.tech.modules.transport.init.ItemsZT_Transport;
import org.zeith.tech.modules.world.init.ItemsZT_World;

import java.util.List;

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
	Item LATEX = BaseZT.newItem(List.of(TagsZT.Items.RUBBER, TagsZT.Items.LATEX));
	
	@RegistryName("tree_tap")
	Item TREE_TAP = BaseZT.newItem();
	
	@RegistryName("plastic")
	Item PLASTIC = BaseZT.newItem(TagsZT.Items.PLASTIC);
	
	@RegistryName("motor")
	Item MOTOR = BaseZT.newItem();
	
	@RegistryName("circular_saw")
	Item CIRCULAR_SAW = BaseZT.newItem();
	
	@RegistryName("sawdust")
	Item SAWDUST = BaseZT.newItem();
	
	@RegistryName("oil_sludge")
	Item OIL_SLUDGE = BaseZT.newItem();
	
	@RegistryName("silicon")
	Item SILICON = BaseZT.newItem(TagsZT.Items.SILICON);
	
	@RegistryName("circuits/basic")
	Item BASIC_CIRCUIT = BaseZT.newItem();
	
	@RegistryName("accumulators/basic")
	ItemAccumulator ACCUMULATOR_BASIC = new ItemAccumulator(EnergyTier.EXTRA_LOW_VOLTAGE.capacity() * 10, BaseZT.itemProps());
	
	@RegistryName("accumulators/normal")
	ItemAccumulator ACCUMULATOR_NORMAL = new ItemAccumulator(EnergyTier.MEDIUM_VOLTAGE.capacity() * 10, BaseZT.itemProps());
	
	@RegistryName("accumulators/advanced")
	ItemAccumulator ACCUMULATOR_ADVANCED = new ItemAccumulator(EnergyTier.HIGH_VOLTAGE.capacity() * 10, BaseZT.itemProps());
	
	@RegistryName("multi_tool/basic")
	ItemMultiTool BASIC_MULTI_TOOL = new ItemMultiTool(BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/basic"), ZeithTechAPI.id("item/multi_tool/basic_empty"));
	
	@RegistryName("multi_tool/electric_saw/iron")
	ItemElectricSaw IRON_ELECTRIC_SAW = new ItemElectricSaw(Tiers.IRON, ZeithTechAPI.id("multi_tool/electric_saw/iron"), BaseZT.itemProps());

//	@RegistryName("multi_tool/wrench")
//	ItemElectricWrench ELECTRIC_WRENCH = new ItemElectricWrench(BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/electric_wrench"));
	
	@RegistryName("multi_tool/electric_saw/diamond")
	ItemElectricSaw DIAMOND_ELECTRIC_SAW = new ItemElectricSaw(Tiers.DIAMOND, ZeithTechAPI.id("multi_tool/electric_saw/diamond"), BaseZT.itemProps());
	
	@RegistryName("multi_tool/electric_saw/tungsten")
	ItemElectricSaw TUNGSTEN_ELECTRIC_SAW = new ItemElectricSaw(ZeithTechAPI.TUNGSTEN_TIER, ZeithTechAPI.id("multi_tool/electric_saw/tungsten"), BaseZT.itemProps());
	
	@RegistryName("multi_tool/electric_saw/netherite")
	ItemElectricSaw NETHERITE_ELECTRIC_SAW = new ItemElectricSaw(Tiers.NETHERITE, ZeithTechAPI.id("multi_tool/electric_saw/netherite"), BaseZT.itemProps());
	
	@RegistryName("multi_tool/motors/iron")
	ItemMultiToolMotor MULTI_TOOL_IRON_MOTOR = new ItemMultiToolMotor(Tiers.IRON, 1.15F, 0.8F, BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/motors/iron"));
	
	@RegistryName("multi_tool/motors/diamond")
	ItemMultiToolMotor MULTI_TOOL_DIAMOND_MOTOR = new ItemMultiToolMotor(Tiers.DIAMOND, 1.05F, 0.9F, BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/motors/diamond"));
	
	@RegistryName("multi_tool/motors/tungsten")
	ItemMultiToolMotor MULTI_TOOL_TUNGSTEN_MOTOR = new ItemMultiToolMotor(ZeithTechAPI.TUNGSTEN_TIER, 1F, 1F, BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/motors/tungsten"));
	
	@RegistryName("multi_tool/motors/netherite")
	ItemMultiToolMotor MULTI_TOOL_NETHERITE_MOTOR = new ItemMultiToolMotor(Tiers.NETHERITE, 0.95F, 1.1F, BaseZT.itemProps(), ZeithTechAPI.id("multi_tool/motors/netherite"));
	
	static @Setup void setup()
	{
		ComposterBlock.COMPOSTABLES.put(SAWDUST, 0.3F);
	}
}