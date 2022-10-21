package org.zeith.tech.modules.processing.init;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.processing.blocks.BlockMiningPipe;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.BlockElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.facade_slicer.BlockFacadeSlicer;
import org.zeith.tech.modules.processing.blocks.fluid_centrifuge.BlockFluidCentrifuge;
import org.zeith.tech.modules.processing.blocks.fluid_pump.BlockFluidPump;
import org.zeith.tech.modules.processing.blocks.grinder.basic.BlockGrinderB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.BlockMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.metal_press.BlockMetalPress;
import org.zeith.tech.modules.processing.blocks.mining_quarry.basic.BlockMiningQuarryB;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.BlockSawmillB;
import org.zeith.tech.modules.processing.blocks.waste_processor.BlockWasteProcessor;

@SimplyRegister(prefix = "processing/")
public interface BlocksZT_Processing
{
	@RegistryName("machine_assembler/basic")
	BlockMachineAssemblerB BASIC_MACHINE_ASSEMBLER = new BlockMachineAssemblerB();
	
	@RegistryName("electric_furnace/basic")
	BlockElectricFurnaceB BASIC_ELECTRIC_FURNACE = new BlockElectricFurnaceB();
	
	@RegistryName("grinder/basic")
	BlockGrinderB BASIC_GRINDER = new BlockGrinderB();
	
	@RegistryName("sawmill/basic")
	BlockSawmillB BASIC_SAWMILL = new BlockSawmillB();
	
	@RegistryName("mining_quarry/basic")
	BlockMiningQuarryB BASIC_QUARRY = new BlockMiningQuarryB();
	
	@RegistryName("mining_pipe")
	BlockMiningPipe MINING_PIPE = new BlockMiningPipe(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
	
	@RegistryName("fluid_pump")
	BlockFluidPump FLUID_PUMP = new BlockFluidPump();
	
	@RegistryName("fluid_centrifuge")
	BlockFluidCentrifuge FLUID_CENTRIFUGE = new BlockFluidCentrifuge();
	
	@RegistryName("waste_processor")
	BlockWasteProcessor WASTE_PROCESSOR = new BlockWasteProcessor();
	
	@RegistryName("metal_press")
	BlockMetalPress METAL_PRESS = new BlockMetalPress();
	
	@RegistryName("facade_slicer")
	BlockFacadeSlicer FACADE_SLICER = new BlockFacadeSlicer();
}