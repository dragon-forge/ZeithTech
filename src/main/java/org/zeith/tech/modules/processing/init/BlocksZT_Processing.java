package org.zeith.tech.modules.processing.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.blocks.fuelgen.basic.BlockFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.BlockMachineAssemblerB;

@SimplyRegister
public interface BlocksZT_Processing
{
	@RegistryName("fuel_generator/basic")
	BlockFuelGeneratorB FUEL_GENERATOR_BASIC = new BlockFuelGeneratorB();
	
	@RegistryName("machine_assembler/basic")
	BlockMachineAssemblerB MACHINE_ASSEMBLER_BASIC = new BlockMachineAssemblerB();
}
