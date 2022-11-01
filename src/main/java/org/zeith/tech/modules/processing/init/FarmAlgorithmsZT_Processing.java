package org.zeith.tech.modules.processing.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.farm_algorithms.FarmAlgorithmCactus;

@SimplyRegister
public interface FarmAlgorithmsZT_Processing
{
	@RegistryName("cactus")
	FarmAlgorithmCactus CACTUS_ALGORITHM = new FarmAlgorithmCactus();
}