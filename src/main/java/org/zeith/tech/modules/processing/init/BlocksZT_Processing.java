package org.zeith.tech.modules.processing.init;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.blocks.BlockMiningPipe;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.BlockElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.fuelgen.basic.BlockFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.BlockMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.mining_quarry.basic.BlockMiningQuarryB;

@SimplyRegister
public interface BlocksZT_Processing
{
	@RegistryName("processing/machine_assembler/basic")
	BlockMachineAssemblerB BASIC_MACHINE_ASSEMBLER = new BlockMachineAssemblerB();
	
	@RegistryName("processing/fuel_generator/basic")
	BlockFuelGeneratorB BASIC_FUEL_GENERATOR = new BlockFuelGeneratorB();
	
	@RegistryName("processing/electric_furnace/basic")
	BlockElectricFurnaceB BASIC_ELECTRIC_FURNACE = new BlockElectricFurnaceB();
	
	@RegistryName("processing/mining_quarry/basic")
	BlockMiningQuarryB BASIC_QUARRY = new BlockMiningQuarryB();
	
	@RegistryName("processing/mining_pipe")
	BlockMiningPipe MINING_PIPE = new BlockMiningPipe(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
}
