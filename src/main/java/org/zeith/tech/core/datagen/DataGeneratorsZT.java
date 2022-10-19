package org.zeith.tech.core.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.tech.core.datagen.loot.LootTableGeneratorZT;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGeneratorsZT
{
	@SubscribeEvent
	public static void onGatherData(GatherDataEvent dataEvent)
	{
		onGatherData(dataEvent.getGenerator(), dataEvent.getExistingFileHelper());
	}
	
	public static void onGatherData(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		generator.addProvider(true, new AdvancementGeneratorZT(generator));
		generator.addProvider(true, new LootTableGeneratorZT(generator));
	}
}