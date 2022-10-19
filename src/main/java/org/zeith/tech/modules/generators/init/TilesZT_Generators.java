package org.zeith.tech.modules.generators.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.tech.modules.generators.blocks.fuel_generator.liquid.basic.TileLiquidFuelGeneratorB;
import org.zeith.tech.modules.generators.blocks.fuel_generator.solid.basic.TileSolidFuelGeneratorB;
import org.zeith.tech.modules.generators.blocks.magmatic.TileMagmaticGenerator;
import org.zeith.tech.modules.generators.client.renderer.tile.TileRendererLiquidFuelGeneratorB;
import org.zeith.tech.modules.generators.client.renderer.tile.TileRendererMagmaticGenerator;

@SimplyRegister(prefix = "generators/")
public interface TilesZT_Generators
{
	@RegistryName("fuel_generator/basic")
	BlockEntityType<TileSolidFuelGeneratorB> BASIC_SOLID_FUEL_GENERATOR = BlockAPI.createBlockEntityType(TileSolidFuelGeneratorB::new, BlocksZT_Generators.BASIC_FUEL_GENERATOR);
	
	@RegistryName("lfuel_generator/basic")
	@TileRenderer(TileRendererLiquidFuelGeneratorB.class)
	BlockEntityType<TileLiquidFuelGeneratorB> BASIC_LIQUID_FUEL_GENERATOR = BlockAPI.createBlockEntityType(TileLiquidFuelGeneratorB::new, BlocksZT_Generators.BASIC_LIQUID_FUEL_GENERATOR);
	
	@RegistryName("magmatic_generator")
	@TileRenderer(TileRendererMagmaticGenerator.class)
	BlockEntityType<TileMagmaticGenerator> MAGMATIC_GENERATOR = BlockAPI.createBlockEntityType(TileMagmaticGenerator::new, BlocksZT_Generators.MAGMATIC_GENERATOR);
}