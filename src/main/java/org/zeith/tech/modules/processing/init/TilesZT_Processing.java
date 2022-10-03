package org.zeith.tech.modules.processing.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.TileElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.fuelgen.basic.TileFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.TileMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.mining_quarry.basic.TileMiningQuarryB;
import org.zeith.tech.modules.processing.client.renderer.tile.TileRendererElectricFurnaceB;
import org.zeith.tech.modules.processing.client.renderer.tile.TileRendererMachineAssemblerB;

@SimplyRegister
public interface TilesZT_Processing
{
	@RegistryName("processing/machine_assembler/basic")
	@TileRenderer(TileRendererMachineAssemblerB.class)
	BlockEntityType<TileMachineAssemblerB> BASIC_MACHINE_ASSEMBLER = BlockAPI.createBlockEntityType(TileMachineAssemblerB::new, BlocksZT_Processing.BASIC_MACHINE_ASSEMBLER);
	
	@RegistryName("processing/fuel_generator/basic")
	BlockEntityType<TileFuelGeneratorB> BASIC_FUEL_GENERATOR = BlockAPI.createBlockEntityType(TileFuelGeneratorB::new, BlocksZT_Processing.BASIC_FUEL_GENERATOR);
	
	@RegistryName("processing/electric_furnace/basic")
	@TileRenderer(TileRendererElectricFurnaceB.class)
	BlockEntityType<TileElectricFurnaceB> BASIC_ELECTRIC_FURNACE = BlockAPI.createBlockEntityType(TileElectricFurnaceB::new, BlocksZT_Processing.BASIC_ELECTRIC_FURNACE);
	
	@RegistryName("processing/mining_quarry/basic")
	BlockEntityType<TileMiningQuarryB> BASIC_QUARRY = BlockAPI.createBlockEntityType(TileMiningQuarryB::new, BlocksZT_Processing.BASIC_QUARRY);
}