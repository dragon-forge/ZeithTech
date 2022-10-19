package org.zeith.tech.modules.generators.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.modules.generators.blocks.fuel_generator.liquid.basic.TileLiquidFuelGeneratorB;
import org.zeith.tech.modules.generators.blocks.fuel_generator.solid.basic.TileSolidFuelGeneratorB;
import org.zeith.tech.modules.processing.client.renderer.tile.TileRendererLiquidFuelGeneratorB;

@SimplyRegister(prefix = "generators/")
public interface TilesZT_Generators
{
	@RegistryName("fuel_generator/basic")
	BlockEntityType<TileSolidFuelGeneratorB> BASIC_SOLID_FUEL_GENERATOR = BlockAPI.createBlockEntityType(TileSolidFuelGeneratorB::new, BlocksZT_Generators.BASIC_FUEL_GENERATOR);
	
	@RegistryName("lfuel_generator/basic")
	@TileRenderer(TileRendererLiquidFuelGeneratorB.class)
	BlockEntityType<TileLiquidFuelGeneratorB> BASIC_LIQUID_FUEL_GENERATOR = BlockAPI.createBlockEntityType(TileLiquidFuelGeneratorB::new, BlocksZT_Generators.BASIC_LIQUID_FUEL_GENERATOR);
}