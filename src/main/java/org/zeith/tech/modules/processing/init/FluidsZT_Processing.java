package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.fluid.FluidFactory;
import org.zeith.tech.modules.processing.fluids.FluidTypeDieselFuel;
import org.zeith.tech.modules.processing.fluids.FluidTypeRefinedOil;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.utils.LegacyEventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public interface FluidsZT_Processing
{
	FluidFactory REFINED_OIL = FluidFactory.createWithBucket(new ResourceLocation(ZeithTech.MOD_ID, "processing/refined_oil"), FluidTypeRefinedOil::create, p -> p.tickRate(15)).addFluidTag(TagsZT.Fluids.OIL);
	
	FluidFactory DIESEL_FUEL = FluidFactory.createWithBucket(new ResourceLocation(ZeithTech.MOD_ID, "processing/diesel_fuel"), FluidTypeDieselFuel::create, p -> p.tickRate(10)).addFluidTag(TagsZT.Fluids.DIESEL);
	
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
		
		modBus.addListener(FMLLoadCompleteEvent.class, evt ->
		{
			ZeithTechAPI.get()
					.getModules()
					.processing()
					.setLiquidFuelBurnTime(
							FluidIngredient.ofFluids(List.of(new FluidStack(DIESEL_FUEL.getSource(), 1))),
							400);
		});
	}
}