package org.zeith.tech.modules.world.init;

import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.core.fluid.FluidFactory;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.world.fluids.FluidTypeCrudeOil;
import org.zeith.tech.utils.LegacyEventBus;

import java.util.List;

public interface FluidsZT_World
{
	FluidFactory CRUDE_OIL = FluidFactory.createWithBucket(ZeithTechAPI.id("crude_oil"), FluidTypeCrudeOil::create, p -> p.tickRate(20)).addFluidTags(List.of(TagsZT.Fluids.OIL, TagsZT.Fluids.CRUDE_OIL));
	
	static void register(LegacyEventBus modBus)
	{
		CRUDE_OIL.register(modBus);
	}
}