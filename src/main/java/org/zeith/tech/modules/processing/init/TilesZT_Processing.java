package org.zeith.tech.modules.processing.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.modules.processing.blocks.fuelgen.basic.TileFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.TileMachineAssemblerB;
import org.zeith.tech.modules.processing.client.renderer.tile.TileRendererMachineAssemblerB;

@SimplyRegister
public interface TilesZT_Processing
{
	@RegistryName("fuel_generator/basic")
	BlockEntityType<TileFuelGeneratorB> FUEL_GENERATOR_BASIC = BlockAPI.createBlockEntityType(TileFuelGeneratorB::new, BlocksZT_Processing.FUEL_GENERATOR_BASIC);
	
	@RegistryName("machine_assembler/basic")
	@TileRenderer(TileRendererMachineAssemblerB.class)
	BlockEntityType<TileMachineAssemblerB> MACHINE_ASSEMBLER_BASIC = BlockAPI.createBlockEntityType(TileMachineAssemblerB::new, BlocksZT_Processing.MACHINE_ASSEMBLER_BASIC);
}