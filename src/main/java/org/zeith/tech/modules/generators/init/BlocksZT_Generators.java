package org.zeith.tech.modules.generators.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.generators.blocks.fuel_generator.liquid.basic.BlockLiquidFuelGeneratorB;
import org.zeith.tech.modules.generators.blocks.fuel_generator.solid.basic.BlockSolidFuelGeneratorB;

@SimplyRegister(prefix = "generators/")
public interface BlocksZT_Generators
{
	@RegistryName("fuel_generator/basic")
	BlockSolidFuelGeneratorB BASIC_FUEL_GENERATOR = new BlockSolidFuelGeneratorB();
	
	@RegistryName("lfuel_generator/basic")
	BlockLiquidFuelGeneratorB BASIC_LIQUID_FUEL_GENERATOR = new BlockLiquidFuelGeneratorB();
}