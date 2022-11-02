package org.zeith.tech.modules.processing.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.farm_algorithms.*;

@SimplyRegister
public interface FarmAlgorithmsZT_Processing
{
	@RegistryName("cactus")
	FarmAlgorithmCactus CACTUS_ALGORITHM = new FarmAlgorithmCactus();
	
	@RegistryName("nether_wart")
	FarmAlgorithmNetherWart NETHER_WART_ALGORITHM = new FarmAlgorithmNetherWart();
	
	@RegistryName("glow_berries")
	FarmAlgorithmGlowBerries GLOW_BERRY_ALGORITHM = new FarmAlgorithmGlowBerries();
}