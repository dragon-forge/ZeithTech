package org.zeith.tech.modules.transport.init;

import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.modules.transport.blocks.energy_cell.basic.BlockEnergyCellB;
import org.zeith.tech.modules.transport.blocks.energy_wire.BlockEnergyWire;
import org.zeith.tech.modules.transport.blocks.energy_wire.EnergyWireProperties;
import org.zeith.tech.modules.transport.blocks.fluid_pipe.BlockFluidPipe;
import org.zeith.tech.modules.transport.blocks.fluid_pipe.FluidPipeProperties;
import org.zeith.tech.modules.transport.blocks.fluid_tank.basic.BlockFluidTankB;
import org.zeith.tech.modules.transport.blocks.item_pipe.BlockItemPipe;
import org.zeith.tech.modules.transport.blocks.item_pipe.ItemPipeProperties;

@SimplyRegister(prefix = "transport/")
public interface BlocksZT_Transport
{
	@RegistryName("fluid_tank/basic")
	BlockFluidTankB BASIC_FLUID_TANK = new BlockFluidTankB(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(2.5F).requiresCorrectToolForDrops(), BlockHarvestAdapter.MineableType.PICKAXE, Tiers.STONE);
	
	@RegistryName("energy_cell/basic")
	BlockEnergyCellB BASIC_ENERGY_CELL = new BlockEnergyCellB();
	
	@RegistryName("item_pipes/copper")
	BlockItemPipe COPPER_ITEM_PIPE = new BlockItemPipe(ItemPipeProperties.builder().build());
	
	@RegistryName("fluid_pipes/wooden")
	BlockFluidPipe WOODEN_FLUID_PIPE = new BlockFluidPipe(FluidPipeProperties.builder().setProperties(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.5F).dynamicShape()).setMaxTemperature(500).setTransferVolume(25).build(), BlockHarvestAdapter.MineableType.AXE);
	
	@RegistryName("fluid_pipes/iron")
	BlockFluidPipe IRON_FLUID_PIPE = new BlockFluidPipe(FluidPipeProperties.builder().setProperties(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(2.5F).dynamicShape().requiresCorrectToolForDrops()).setMaxTemperature(1536).setTransferVolume(50).build(), BlockHarvestAdapter.MineableType.PICKAXE);
	
	@RegistryName("wires/aluminum/uninsulated")
	BlockEnergyWire UNINSULATED_ALUMINUM_WIRE = new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.EXTRA_LOW_VOLTAGE).setEnergyLoss(1.5F).setInsulated(false).build());
	
	@RegistryName("wires/aluminum/insulated")
	BlockEnergyWire INSULATED_ALUMINUM_WIRE = new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.EXTRA_LOW_VOLTAGE).setEnergyLoss(0.35F).setInsulated(true).visitBlockProps(p -> p.sound(SoundType.WOOL)).build());
	
	@RegistryName("wires/copper/uninsulated")
	BlockEnergyWire UNINSULATED_COPPER_WIRE = new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.LOW_VOLTAGE).setEnergyLoss(1F).setInsulated(false).build());
	
	@RegistryName("wires/copper/insulated")
	BlockEnergyWire INSULATED_COPPER_WIRE = new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.LOW_VOLTAGE).setEnergyLoss(0.25F).setInsulated(true).visitBlockProps(p -> p.sound(SoundType.WOOL)).build());
	
	@RegistryName("wires/gold/uninsulated")
	BlockEnergyWire UNINSULATED_GOLD_WIRE = new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.MEDIUM_VOLTAGE).setEnergyLoss(1.25F).setInsulated(false).build());
	
	@RegistryName("wires/gold/insulated")
	BlockEnergyWire INSULATED_GOLD_WIRE = new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.MEDIUM_VOLTAGE).setEnergyLoss(0.25F).setInsulated(true).visitBlockProps(p -> p.sound(SoundType.WOOL)).build());
	
	@RegistryName("wires/silver/uninsulated")
	BlockEnergyWire UNINSULATED_SILVER_WIRE = new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.HIGH_VOLTAGE).setEnergyLoss(1F).setInsulated(false).build());
	
	@RegistryName("wires/silver/insulated")
	BlockEnergyWire INSULATED_SILVER_WIRE = new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.HIGH_VOLTAGE).setEnergyLoss(0.125F).setInsulated(true).visitBlockProps(p -> p.sound(SoundType.WOOL)).build());
}