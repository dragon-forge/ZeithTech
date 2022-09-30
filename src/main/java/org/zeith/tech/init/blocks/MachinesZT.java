package org.zeith.tech.init.blocks;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.common.blocks.fuelgen.basic.BlockFuelGeneratorB;
import org.zeith.tech.common.blocks.machine_assembler.basic.BlockMachineAssemblerB;

@SimplyRegister
public interface MachinesZT
{
	@RegistryName("fuel_generator/basic")
	BlockFuelGeneratorB FUEL_GENERATOR_BASIC = new BlockFuelGeneratorB();
	
	@RegistryName("machine_assembler/basic")
	BlockMachineAssemblerB MACHINE_ASSEMBLER_BASIC = new BlockMachineAssemblerB();
}
