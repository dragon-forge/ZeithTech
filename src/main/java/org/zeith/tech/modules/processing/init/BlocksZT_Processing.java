package org.zeith.tech.modules.processing.init;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.blocks.BlockMiningPipe;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.BlockElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.fluid_centrifuge.BlockFluidCentrifuge;
import org.zeith.tech.modules.processing.blocks.fluid_pump.BlockFluidPump;
import org.zeith.tech.modules.processing.blocks.fuelgen.liquid.basic.BlockLiquidFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.fuelgen.solid.basic.BlockSolidFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.grinder.basic.BlockGrinderB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.BlockMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.metal_press.BlockMetalPress;
import org.zeith.tech.modules.processing.blocks.mining_quarry.basic.BlockMiningQuarryB;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.BlockSawmillB;
import org.zeith.tech.modules.processing.blocks.waste_processor.BlockWasteProcessor;

@SimplyRegister
public interface BlocksZT_Processing
{
	@RegistryName("processing/machine_assembler/basic")
	BlockMachineAssemblerB BASIC_MACHINE_ASSEMBLER = new BlockMachineAssemblerB();
	
	@RegistryName("processing/fuel_generator/basic")
	BlockSolidFuelGeneratorB BASIC_FUEL_GENERATOR = new BlockSolidFuelGeneratorB();
	
	@RegistryName("processing/lfuel_generator/basic")
	BlockLiquidFuelGeneratorB BASIC_LIQUID_FUEL_GENERATOR = new BlockLiquidFuelGeneratorB();
	
	@RegistryName("processing/electric_furnace/basic")
	BlockElectricFurnaceB BASIC_ELECTRIC_FURNACE = new BlockElectricFurnaceB();
	
	@RegistryName("processing/grinder/basic")
	BlockGrinderB BASIC_GRINDER = new BlockGrinderB();
	
	@RegistryName("processing/sawmill/basic")
	BlockSawmillB BASIC_SAWMILL = new BlockSawmillB();
	
	@RegistryName("processing/mining_quarry/basic")
	BlockMiningQuarryB BASIC_QUARRY = new BlockMiningQuarryB();
	
	@RegistryName("processing/mining_pipe")
	BlockMiningPipe MINING_PIPE = new BlockMiningPipe(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
	
	@RegistryName("processing/fluid_pump")
	BlockFluidPump FLUID_PUMP = new BlockFluidPump();
	
	@RegistryName("processing/fluid_centrifuge")
	BlockFluidCentrifuge FLUID_CENTRIFUGE = new BlockFluidCentrifuge();
	
	@RegistryName("processing/waste_processor")
	BlockWasteProcessor WASTE_PROCESSOR = new BlockWasteProcessor();
	
	@RegistryName("processing/metal_press")
	BlockMetalPress METAL_PRESS = new BlockMetalPress();
}