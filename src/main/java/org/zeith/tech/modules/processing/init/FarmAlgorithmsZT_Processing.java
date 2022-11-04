package org.zeith.tech.modules.processing.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.farm_algorithms.*;

@SimplyRegister
public interface FarmAlgorithmsZT_Processing
{
	@RegistryName("cactus")
	FarmAlgorithmCactus CACTUS_ALGORITHM = new FarmAlgorithmCactus();
	
	@RegistryName("bamboo")
	FarmAlgorithmBamboo BAMBOO_ALGORITHM = new FarmAlgorithmBamboo();
	
	@RegistryName("nether_wart")
	FarmAlgorithmNetherWart NETHER_WART_ALGORITHM = new FarmAlgorithmNetherWart();
	
	@RegistryName("glow_berries")
	FarmAlgorithmGlowBerries GLOW_BERRY_ALGORITHM = new FarmAlgorithmGlowBerries();
	
	@RegistryName("crops")
	FarmAlgorithmCrops CROP_ALGORITHM = new FarmAlgorithmCrops();
	
	@RegistryName("stems")
	FarmAlgorithmStems STEM_ALGORITHM = new FarmAlgorithmStems();
	
	@RegistryName("cocoa")
	FarmAlgorithmCocoaBeans COCOA_ALGORITHM = new FarmAlgorithmCocoaBeans();
	
	@RegistryName("berries")
	FarmAlgorithmBerries BERRIES_ALGORITHM = new FarmAlgorithmBerries();
	
	@RegistryName("forester")
	FarmAlgorithmForester FORESTER_ALGORITHM = new FarmAlgorithmForester();
	
	@RegistryName("sugar_cane")
	FarmAlgorithmSugarCane CANE_ALGORITHM = new FarmAlgorithmSugarCane();
	
	@RegistryName("chorus")
	FarmAlgorithmChorus CHORUS_ALGORITHM = new FarmAlgorithmChorus();
}