package org.zeith.tech.modules.processing.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.modules.processing.blocks.blast_furnace.basic.TileBlastFurnaceB;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.TileElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.facade_slicer.TileFacadeSlicer;
import org.zeith.tech.modules.processing.blocks.farm.TileFarm;
import org.zeith.tech.modules.processing.blocks.fluid_centrifuge.TileFluidCentrifuge;
import org.zeith.tech.modules.processing.blocks.fluid_pump.TileFluidPump;
import org.zeith.tech.modules.processing.blocks.grinder.basic.TileGrinderB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.advanced.TileMachineAssemblerA;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.TileMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.metal_press.TileMetalPress;
import org.zeith.tech.modules.processing.blocks.mining_quarry.basic.TileMiningQuarryB;
import org.zeith.tech.modules.processing.blocks.pattern_storage.TilePatternStorage;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.TileSawmillB;
import org.zeith.tech.modules.processing.blocks.waste_processor.TileWasteProcessor;
import org.zeith.tech.modules.processing.client.renderer.tile.*;
import org.zeith.tech.modules.processing.client.renderer.tile.multiblock.TileRenderFarm;
import org.zeith.tech.modules.processing.client.renderer.tile.multiblock.TileRendererBlastFurnaceB;

@SimplyRegister(prefix = "processing/")
public interface TilesZT_Processing
{
	@RegistryName("machine_assembler/basic")
	@TileRenderer(TileRendererMachineAssemblerB.class)
	BlockEntityType<TileMachineAssemblerB> BASIC_MACHINE_ASSEMBLER = BlockAPI.createBlockEntityType(TileMachineAssemblerB::new, BlocksZT_Processing.BASIC_MACHINE_ASSEMBLER);
	
	@RegistryName("machine_assembler/advanced")
	@TileRenderer(TileRendererMachineAssemblerA.class)
	BlockEntityType<TileMachineAssemblerA> ADVANCED_MACHINE_ASSEMBLER = BlockAPI.createBlockEntityType(TileMachineAssemblerA::new, BlocksZT_Processing.ADVANCED_MACHINE_ASSEMBLER);
	
	@RegistryName("electric_furnace/basic")
	@TileRenderer(TileRendererElectricFurnaceB.class)
	BlockEntityType<TileElectricFurnaceB> BASIC_ELECTRIC_FURNACE = BlockAPI.createBlockEntityType(TileElectricFurnaceB::new, BlocksZT_Processing.BASIC_ELECTRIC_FURNACE);
	
	@RegistryName("grinder/basic")
	BlockEntityType<TileGrinderB> BASIC_GRINDER = BlockAPI.createBlockEntityType(TileGrinderB::new, BlocksZT_Processing.BASIC_GRINDER);
	
	@RegistryName("sawmill/basic")
	BlockEntityType<TileSawmillB> BASIC_SAWMILL = BlockAPI.createBlockEntityType(TileSawmillB::new, BlocksZT_Processing.BASIC_SAWMILL);
	
	@RegistryName("mining_quarry/basic")
	BlockEntityType<TileMiningQuarryB> BASIC_QUARRY = BlockAPI.createBlockEntityType(TileMiningQuarryB::new, BlocksZT_Processing.BASIC_QUARRY);
	
	@RegistryName("fluid_pump")
	@TileRenderer(TileRendererFluidPump.class)
	BlockEntityType<TileFluidPump> FLUID_PUMP = BlockAPI.createBlockEntityType(TileFluidPump::new, BlocksZT_Processing.FLUID_PUMP);
	
	@RegistryName("fluid_centrifuge")
	@TileRenderer(TileRendererFluidCentrifuge.class)
	BlockEntityType<TileFluidCentrifuge> FLUID_CENTRIFUGE = BlockAPI.createBlockEntityType(TileFluidCentrifuge::new, BlocksZT_Processing.FLUID_CENTRIFUGE);
	
	@RegistryName("waste_processor")
	@TileRenderer(TileRendererWasteProcessor.class)
	BlockEntityType<TileWasteProcessor> WASTE_PROCESSOR = BlockAPI.createBlockEntityType(TileWasteProcessor::new, BlocksZT_Processing.WASTE_PROCESSOR);
	
	@RegistryName("metal_press")
	@TileRenderer(TileRendererMetalPress.class)
	BlockEntityType<TileMetalPress> METAL_PRESS = BlockAPI.createBlockEntityType(TileMetalPress::new, BlocksZT_Processing.METAL_PRESS);
	
	@RegistryName("facade_slicer")
	BlockEntityType<TileFacadeSlicer> FACADE_SLICER = BlockAPI.createBlockEntityType(TileFacadeSlicer::new, BlocksZT_Processing.FACADE_SLICER);
	
	@RegistryName("pattern_storage")
	@TileRenderer(TileRendererPatternStorage.class)
	BlockEntityType<TilePatternStorage> PATTERN_STORAGE = BlockAPI.createBlockEntityType(TilePatternStorage::new, BlocksZT_Processing.PATTERN_STORAGE);
	
	@RegistryName("blast_furnace/basic")
	@TileRenderer(TileRendererBlastFurnaceB.class)
	BlockEntityType<TileBlastFurnaceB> BASIC_BLAST_FURNACE = BlockAPI.createBlockEntityType(TileBlastFurnaceB::new, BlocksZT_Processing.BASIC_BLAST_FURNACE);
	
	@RegistryName("farm")
	@TileRenderer(TileRenderFarm.class)
	BlockEntityType<TileFarm> FARM = BlockAPI.createBlockEntityType(TileFarm::new, BlocksZT_Processing.FARM);
}