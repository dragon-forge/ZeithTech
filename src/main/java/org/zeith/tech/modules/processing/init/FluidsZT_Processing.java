package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.fluid.FluidFactory;
import org.zeith.tech.modules.processing.fluids.*;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.utils.LegacyEventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public interface FluidsZT_Processing
{
	FluidFactory REFINED_OIL = FluidFactory.createWithBucket(new ResourceLocation(ZeithTech.MOD_ID, "processing/refined_oil"), FluidTypeRefinedOil::create, p -> p.tickRate(15)).addFluidTag(TagsZT.Fluids.OIL);
	
	FluidFactory DIESEL_FUEL = FluidFactory.createWithBucket(new ResourceLocation(ZeithTech.MOD_ID, "processing/diesel_fuel"), FluidTypeDieselFuel::create, p -> p.tickRate(10)).addFluidTag(TagsZT.Fluids.DIESEL);
	
	FluidFactory GAS = FluidFactory.createNoBucket(new ResourceLocation(ZeithTech.MOD_ID, "processing/gas"), FluidTypeGas::create, p -> p.tickRate(10), false).addFluidTag(TagsZT.Fluids.GAS);
	
	FluidFactory SULFURIC_ACID = FluidFactory.createWithBucket(new ResourceLocation(ZeithTech.MOD_ID, "processing/sulfuric_acid"), FluidTypeSulfuricAcid::create, p -> p.tickRate(7)).addFluidTag(TagsZT.Fluids.SULFURIC_ACID);
	
	static void register(LegacyEventBus modBus)
	{
		for(Field field : FluidsZT_Processing.class.getDeclaredFields())
		{
			if(FluidFactory.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers()))
			{
				try
				{
					FluidFactory.class.cast(field.get(null)).register(modBus);
				} catch(IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}