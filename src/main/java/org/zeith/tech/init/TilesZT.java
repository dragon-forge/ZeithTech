package org.zeith.tech.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.common.blocks.fuelgen.basic.TileFuelGeneratorB;
import org.zeith.tech.common.blocks.machine_assembler.basic.TileMachineAssemblerB;
import org.zeith.tech.init.blocks.MachinesZT;

@SimplyRegister
public class TilesZT
{
	@RegistryName("fuel_generator/basic")
	public static final BlockEntityType<TileFuelGeneratorB> FUEL_GENERATOR_BASIC = BlockAPI.createBlockEntityType(TileFuelGeneratorB::new, MachinesZT.FUEL_GENERATOR_BASIC);
	
	@RegistryName("machine_assembler/basic")
	public static final BlockEntityType<TileMachineAssemblerB> MACHINE_ASSEMBLER_BASIC = BlockAPI.createBlockEntityType(TileMachineAssemblerB::new, MachinesZT.MACHINE_ASSEMBLER_BASIC);
}