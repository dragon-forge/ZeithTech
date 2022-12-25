package org.zeith.tech.compat.farmersdelight;

import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.compat.BaseCompat;
import org.zeith.tech.utils.LegacyEventBus;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.List;

public class FarmersDelightCompat
		extends BaseCompat
{
	@Override
	public void setup(LegacyEventBus bus)
	{
		bus.addListener(FMLLoadCompleteEvent.class, this::loadComplete);
	}
	
	public void loadComplete(FMLLoadCompleteEvent e)
	{
		var farmData = ZeithTechAPI.get()
				.getModules()
				.processing()
				.farmData();
		
		log.info("Added rich soil to list of dirts for farming.");
		farmData.addFarmlandPlaceable(ModItems.RICH_SOIL.get());
		
		log.info("Added tomato crop support.");
		farmData.registerCropSubAlgorithm(new TomatoCropSubAlgorithm(), List.of(ModItems.TOMATO_SEEDS.get()), List.of(ModBlocks.BUDDING_TOMATO_CROP.get(), ModBlocks.TOMATO_CROP.get()));
	}
}