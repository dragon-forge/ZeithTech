package org.zeith.tech.modules.world.worldgen;

import net.minecraft.world.level.levelgen.feature.Feature;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.world.worldgen.oil.OilLakeFeature;

@SimplyRegister
public class FeaturesZT
{
	@RegistryName("oil_lake")
	public static final Feature<OilLakeFeature.OilLakeConfiguration> OIL_LAKE = new OilLakeFeature(OilLakeFeature.OilLakeConfiguration.CODEC);
}
