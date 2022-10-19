package org.zeith.tech.core.datagen;

import com.google.common.collect.Sets;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.modules.generators.init.AdvancementTriggersZT;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.ItemsZT;
import org.zeith.tech.modules.transport.blocks.energy_wire.BlockEnergyWire;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AdvancementGeneratorZT
		implements DataProvider
{
	private final DataGenerator generator;
	
	public AdvancementGeneratorZT(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(CachedOutput cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		Set<ResourceLocation> set = Sets.newHashSet();
		Consumer<Advancement> consumer = (advancement) ->
		{
			if(!set.add(advancement.getId()))
			{
				throw new IllegalStateException("Duplicate advancement " + advancement.getId());
			} else
			{
				Path path1 = createPath(path, advancement);
				
				try
				{
					DataProvider.saveStable(cache, advancement.deconstruct().serializeToJson(), path1);
				} catch(IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		};
		
		generateAdvancements(consumer);
	}
	
	private void generateAdvancements(Consumer<Advancement> consumer)
	{
		var root = Advancement.Builder.advancement()
				.display(
						ItemsZT.IRON_HAMMER,
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".root"),
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".desc"),
						ZeithTechAPI.id("textures/block/lead_block.png"),
						FrameType.TASK,
						false /* showToast */,
						true /* announceChat */,
						false /* hidden */
				)
				.addCriterion("trigger", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsZT.IRON_HAMMER))
				.save(consumer, ZeithTechAPI.MOD_ID + ":main/root");
		
		var firstPlate = Advancement.Builder.advancement()
				.display(
						ItemsZT.COPPER_PLATE,
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".first_plate"),
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".first_plate.desc"),
						null /* background */,
						FrameType.TASK,
						true /* showToast */,
						true /* announceChat */,
						false /* hidden */
				)
				.parent(root)
				.addCriterion("trigger", AdvancementTriggersZT.MAKE_PLATE.instance())
				.save(consumer, ZeithTechAPI.MOD_ID + ":main/first_plate");
		
		var findHevea = Advancement.Builder.advancement()
				.display(
						BlocksZT.HEVEA_SAPLING,
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".find_hevea"),
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".find_hevea.desc"),
						null /* background */,
						FrameType.TASK,
						true /* showToast */,
						true /* announceChat */,
						false /* hidden */
				)
				.parent(root)
				.addCriterion("trigger", AdvancementTriggersZT.FIND_HEVEA.instance())
				.save(consumer, ZeithTechAPI.MOD_ID + ":main/find_hevea");
		
		var collectResin = Advancement.Builder.advancement()
				.display(
						ItemsZT.BOWL_OF_RESIN,
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".collect_resin"),
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".collect_resin.desc"),
						null /* background */,
						FrameType.TASK,
						true /* showToast */,
						true /* announceChat */,
						false /* hidden */
				)
				.parent(findHevea)
				.addCriterion("trigger", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsZT.BOWL_OF_RESIN))
				.save(consumer, ZeithTechAPI.MOD_ID + ":main/collect_resin");
		
		var smeltLatex = Advancement.Builder.advancement()
				.display(
						ItemsZT.LATEX,
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".smelt_latex"),
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".smelt_latex.desc"),
						null /* background */,
						FrameType.TASK,
						true /* showToast */,
						true /* announceChat */,
						false /* hidden */
				)
				.parent(collectResin)
				.addCriterion("trigger", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsZT.LATEX))
				.save(consumer, ZeithTechAPI.MOD_ID + ":main/smelt_latex");
		
		var machineAssembler = Advancement.Builder.advancement()
				.display(
						BlocksZT.BASIC_MACHINE_ASSEMBLER,
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".machine_assembler"),
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".machine_assembler.desc"),
						null /* background */,
						FrameType.TASK,
						true /* showToast */,
						true /* announceChat */,
						false /* hidden */
				)
				.parent(firstPlate)
				.addCriterion("trigger", InventoryChangeTrigger.TriggerInstance.hasItems(BlocksZT.BASIC_MACHINE_ASSEMBLER))
				.save(consumer, ZeithTechAPI.MOD_ID + ":main/machine_assembler");
		
		var basicSolidFuelGenerator = Advancement.Builder.advancement()
				.display(
						BlocksZT.BASIC_FUEL_GENERATOR,
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".basic_fuel_generator"),
						Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".basic_fuel_generator.desc"),
						null /* background */,
						FrameType.TASK,
						true /* showToast */,
						true /* announceChat */,
						false /* hidden */
				)
				.parent(machineAssembler)
				.addCriterion("trigger", InventoryChangeTrigger.TriggerInstance.hasItems(BlocksZT.BASIC_FUEL_GENERATOR))
				.save(consumer, ZeithTechAPI.MOD_ID + ":main/basic_fuel_generator");
		
		var makeWires = visit(Advancement.Builder.advancement()
						.display(
								BlocksZT.UNINSULATED_COPPER_WIRE,
								Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".craft_wire"),
								Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".craft_wire.desc"),
								null /* background */,
								FrameType.TASK,
								true /* showToast */,
								true /* announceChat */,
								false /* hidden */
						)
						.parent(firstPlate)
						.requirements(RequirementsStrategy.OR),
				builder -> findBlocks(b -> b instanceof BlockEnergyWire w && !w.properties.insulated()).forEach(b -> builder.addCriterion(ForgeRegistries.BLOCKS.getKey(b).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(b))))
				.save(consumer, ZeithTechAPI.MOD_ID + ":main/craft_wire");
		
		var insulateWires = visit(Advancement.Builder.advancement()
						.display(
								BlocksZT.INSULATED_COPPER_WIRE,
								Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".insulate_wire"),
								Component.translatable("advancement." + ZeithTechAPI.MOD_ID + ".insulate_wire.desc"),
								null /* background */,
								FrameType.TASK,
								true /* showToast */,
								true /* announceChat */,
								false /* hidden */
						)
						.parent(makeWires)
						.requirements(RequirementsStrategy.OR),
				builder -> findBlocks(b -> b instanceof BlockEnergyWire w && w.properties.insulated()).forEach(b -> builder.addCriterion(ForgeRegistries.BLOCKS.getKey(b).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(b))))
				.save(consumer, ZeithTechAPI.MOD_ID + ":main/insulate_wire");
	}
	
	public static Advancement.Builder visit(Advancement.Builder builder, Consumer<Advancement.Builder> op)
	{
		op.accept(builder);
		return builder;
	}
	
	public static Stream<Block> findBlocks(Predicate<Block> filter)
	{
		return ForgeRegistries.BLOCKS.getValues()
				.stream()
				.filter(filter);
	}
	
	private static Path createPath(Path basePath, Advancement advancement)
	{
		return basePath.resolve("data/" + advancement.getId().getNamespace()
				+ "/advancements/" + advancement.getId().getPath() + ".json");
	}
	
	@Override
	public String getName()
	{
		return "Advancements";
	}
}