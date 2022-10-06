package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.fluid.FluidFactory;
import org.zeith.tech.modules.processing.fluids.FluidTypeRefinedOil;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.utils.LegacyEventBus;

public interface FluidsZT_Processing
{
	FluidFactory REFINED_OIL = FluidFactory.createWithBucket(new ResourceLocation(ZeithTech.MOD_ID, "processing/refined_oil"), FluidTypeRefinedOil::create, p -> p.tickRate(15)).addFluidTag(TagsZT.Fluids.OIL);
	
	static void register(LegacyEventBus modBus)
	{
		REFINED_OIL.register(modBus);
	}
}