package org.zeith.tech.modules.processing.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.api.block.multiblock.blast_furnace.IBlastFurnaceCasingBlock;
import org.zeith.tech.modules.processing.blocks.BlockCompositeBricks;
import org.zeith.tech.modules.processing.blocks.BlockMiningPipe;
import org.zeith.tech.modules.processing.blocks.blast_furnace.basic.BlockBlastFurnaceB;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.BlockElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.facade_slicer.BlockFacadeSlicer;
import org.zeith.tech.modules.processing.blocks.farm.BlockFarm;
import org.zeith.tech.modules.processing.blocks.farm.BlockFarmController;
import org.zeith.tech.modules.processing.blocks.fluid_centrifuge.BlockFluidCentrifuge;
import org.zeith.tech.modules.processing.blocks.fluid_pump.BlockFluidPump;
import org.zeith.tech.modules.processing.blocks.grinder.basic.BlockGrinderB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.advanced.BlockMachineAssemblerA;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.BlockMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.metal_press.BlockMetalPress;
import org.zeith.tech.modules.processing.blocks.mining_quarry.basic.BlockMiningQuarryB;
import org.zeith.tech.modules.processing.blocks.pattern_storage.BlockPatternStorage;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.BlockSawmillB;
import org.zeith.tech.modules.processing.blocks.waste_processor.BlockWasteProcessor;
import org.zeith.tech.modules.shared.blocks.SimpleBlockZT;

import java.util.List;

@SimplyRegister(prefix = "processing/")
public interface BlocksZT_Processing
{
	@RegistryName("machine_assembler/basic")
	BlockMachineAssemblerB BASIC_MACHINE_ASSEMBLER = new BlockMachineAssemblerB();
	
	@RegistryName("machine_assembler/advanced")
	BlockMachineAssemblerA ADVANCED_MACHINE_ASSEMBLER = new BlockMachineAssemblerA();
	
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
	
	@RegistryName("pattern_storage")
	BlockPatternStorage PATTERN_STORAGE = new BlockPatternStorage();
	
	@RegistryName("blast_furnace_burner")
	SimpleBlockZT BLAST_FURNACE_BURNER = new SimpleBlockZT(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).requiresCorrectToolForDrops()).dropsSelf();
	
	@RegistryName("composite_bricks/broken")
	BlockCompositeBricks BROKEN_COMPOSITE_BRICKS = new BlockCompositeBricks(null, IBlastFurnaceCasingBlock.BlastFurnaceTier.BASIC, 1F, 0.25F);
	
	@RegistryName("composite_bricks/damaged")
	BlockCompositeBricks DAMAGED_COMPOSITE_BRICKS = new BlockCompositeBricks(BROKEN_COMPOSITE_BRICKS, IBlastFurnaceCasingBlock.BlastFurnaceTier.BASIC, 0.25F, 0.75F);
	
	@RegistryName("composite_bricks/cracked")
	BlockCompositeBricks CRACKED_COMPOSITE_BRICKS = new BlockCompositeBricks(DAMAGED_COMPOSITE_BRICKS, IBlastFurnaceCasingBlock.BlastFurnaceTier.BASIC, 0.1F, 0.9F);
	
	@RegistryName("composite_bricks/new")
	BlockCompositeBricks COMPOSITE_BRICKS = new BlockCompositeBricks(CRACKED_COMPOSITE_BRICKS, IBlastFurnaceCasingBlock.BlastFurnaceTier.BASIC, 0F, 0.98F);
	
	@RegistryName("blast_furnace/basic")
	BlockBlastFurnaceB BASIC_BLAST_FURNACE = new BlockBlastFurnaceB();
	
	@RegistryName("farm")
	BlockFarm FARM = new BlockFarm();
	
	@RegistryName("farm/controller")
	BlockFarmController FARM_CONTROLLER = new BlockFarmController();
	
	@RegistryName("farm/item_port")
	SimpleBlockZT FARM_ITEM_PORT = new SimpleBlockZT(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(3.0F, 10.0F).sound(SoundType.WOOD)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE)).dropsSelf();
	
	@RegistryName("farm/fluid_port")
	SimpleBlockZT FARM_FLUID_PORT = new SimpleBlockZT(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(3.0F, 10.0F).sound(SoundType.WOOD)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE)).dropsSelf();
	
	@RegistryName("farm/energy_port")
	SimpleBlockZT FARM_ENERGY_PORT = new SimpleBlockZT(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(3.0F, 10.0F).sound(SoundType.WOOD)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE)).dropsSelf();
}