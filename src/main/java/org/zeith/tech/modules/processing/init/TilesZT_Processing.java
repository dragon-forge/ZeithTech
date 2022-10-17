package org.zeith.tech.modules.processing.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.TileElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.fluid_centrifuge.TileFluidCentrifuge;
import org.zeith.tech.modules.processing.blocks.fluid_pump.TileFluidPump;
import org.zeith.tech.modules.processing.blocks.fuelgen.liquid.basic.TileLiquidFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.fuelgen.solid.basic.TileSolidFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.grinder.basic.TileGrinderB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.TileMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.metal_press.TileMetalPress;
import org.zeith.tech.modules.processing.blocks.mining_quarry.basic.TileMiningQuarryB;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.TileSawmillB;
import org.zeith.tech.modules.processing.blocks.waste_processor.TileWasteProcessor;
import org.zeith.tech.modules.processing.client.renderer.tile.*;

@SimplyRegister
public interface TilesZT_Processing
{
	@RegistryName("processing/machine_assembler/basic")
	@TileRenderer(TileRendererMachineAssemblerB.class)
	BlockEntityType<TileMachineAssemblerB> BASIC_MACHINE_ASSEMBLER = BlockAPI.createBlockEntityType(TileMachineAssemblerB::new, BlocksZT_Processing.BASIC_MACHINE_ASSEMBLER);
	
	@RegistryName("processing/fuel_generator/basic")
	BlockEntityType<TileSolidFuelGeneratorB> BASIC_SOLID_FUEL_GENERATOR = BlockAPI.createBlockEntityType(TileSolidFuelGeneratorB::new, BlocksZT_Processing.BASIC_FUEL_GENERATOR);
	
	@RegistryName("processing/lfuel_generator/basic")
	@TileRenderer(TileRendererLiquidFuelGeneratorB.class)
	BlockEntityType<TileLiquidFuelGeneratorB> BASIC_LIQUID_FUEL_GENERATOR = BlockAPI.createBlockEntityType(TileLiquidFuelGeneratorB::new, BlocksZT_Processing.BASIC_LIQUID_FUEL_GENERATOR);
	
	@RegistryName("processing/electric_furnace/basic")
	@TileRenderer(TileRendererElectricFurnaceB.class)
	BlockEntityType<TileElectricFurnaceB> BASIC_ELECTRIC_FURNACE = BlockAPI.createBlockEntityType(TileElectricFurnaceB::new, BlocksZT_Processing.BASIC_ELECTRIC_FURNACE);
	
	@RegistryName("processing/grinder/basic")
	BlockEntityType<TileGrinderB> BASIC_GRINDER = BlockAPI.createBlockEntityType(TileGrinderB::new, BlocksZT_Processing.BASIC_GRINDER);
	
	@RegistryName("processing/sawmill/basic")
	BlockEntityType<TileSawmillB> BASIC_SAWMILL = BlockAPI.createBlockEntityType(TileSawmillB::new, BlocksZT_Processing.BASIC_SAWMILL);
	
	@RegistryName("processing/mining_quarry/basic")
	BlockEntityType<TileMiningQuarryB> BASIC_QUARRY = BlockAPI.createBlockEntityType(TileMiningQuarryB::new, BlocksZT_Processing.BASIC_QUARRY);
	
	@RegistryName("processing/fluid_pump")
	@TileRenderer(TileRendererFluidPump.class)
	BlockEntityType<TileFluidPump> FLUID_PUMP = BlockAPI.createBlockEntityType(TileFluidPump::new, BlocksZT_Processing.FLUID_PUMP);
	
	@RegistryName("processing/fluid_centrifuge")
	@TileRenderer(TileRendererFluidCentrifuge.class)
	BlockEntityType<TileFluidCentrifuge> FLUID_CENTRIFUGE = BlockAPI.createBlockEntityType(TileFluidCentrifuge::new, BlocksZT_Processing.FLUID_CENTRIFUGE);
	
	@RegistryName("processing/waste_processor")
	@TileRenderer(TileRendererWasteProcessor.class)
	BlockEntityType<TileWasteProcessor> WASTE_PROCESSOR = BlockAPI.createBlockEntityType(TileWasteProcessor::new, BlocksZT_Processing.WASTE_PROCESSOR);
	
	@RegistryName("processing/metal_press")
	@TileRenderer(TileRendererMetalPress.class)
	BlockEntityType<TileMetalPress> METAL_PRESS = BlockAPI.createBlockEntityType(TileMetalPress::new, BlocksZT_Processing.METAL_PRESS);
}