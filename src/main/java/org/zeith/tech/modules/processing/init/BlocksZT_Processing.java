package org.zeith.tech.modules.processing.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.BlockElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.fuelgen.basic.BlockFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.BlockMachineAssemblerB;

@SimplyRegister
public interface BlocksZT_Processing
{
	@RegistryName("processing/machine_assembler/basic")
	BlockMachineAssemblerB BASIC_MACHINE_ASSEMBLER = new BlockMachineAssemblerB();
	
	@RegistryName("processing/fuel_generator/basic")
	BlockFuelGeneratorB BASIC_FUEL_GENERATOR = new BlockFuelGeneratorB();
	
	@RegistryName("processing/electric_furnace/basic")
	BlockElectricFurnaceB BASIC_ELECTRIC_FURNACE = new BlockElectricFurnaceB();
}
