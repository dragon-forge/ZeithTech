package org.zeith.tech.modules.transport.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.modules.transport.blocks.energy_wire.BlockEnergyWire;
import org.zeith.tech.modules.transport.blocks.energy_wire.EnergyWireProperties;
import org.zeith.tech.modules.transport.blocks.item_pipe.BlockItemPipe;
import org.zeith.tech.modules.transport.blocks.item_pipe.ItemPipeProperties;

import java.util.List;

@SimplyRegister
public interface BlocksZT_Transport
{
	@RegistryName("transport/item_pipes/copper")
	BlockItemPipe COPPER_ITEM_PIPE = (BlockItemPipe) new BlockItemPipe(new ItemPipeProperties()).addBlockTags(List.of(BlockTags.MINEABLE_WITH_PICKAXE));
	
	@RegistryName("transport/wires/copper/uninsulated")
	BlockEnergyWire UNINSULATED_COPPER_WIRE = (BlockEnergyWire) new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.LOW_VOLTAGE).setEnergyLoss(1F).setInsulated(false).build()).addBlockTags(List.of(BlockTags.MINEABLE_WITH_AXE)).dropsSelf();
	
	@RegistryName("transport/wires/copper/insulated")
	BlockEnergyWire INSULATED_COPPER_WIRE = (BlockEnergyWire) new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.LOW_VOLTAGE).setEnergyLoss(0.25F).setInsulated(true).visitBlockProps(p -> p.sound(SoundType.WOOL)).build()).dropsSelf();
	
	@RegistryName("transport/wires/aluminum/uninsulated")
	BlockEnergyWire UNINSULATED_ALUMINUM_WIRE = (BlockEnergyWire) new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.SEMI_MID_VOLTAGE).setEnergyLoss(1.6F).setInsulated(false).build()).dropsSelf();
	
	@RegistryName("transport/wires/aluminum/insulated")
	BlockEnergyWire INSULATED_ALUMINUM_WIRE = (BlockEnergyWire) new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.SEMI_MID_VOLTAGE).setEnergyLoss(0.45F).setInsulated(true).visitBlockProps(p -> p.sound(SoundType.WOOL)).build()).dropsSelf();
	
	@RegistryName("transport/wires/gold/uninsulated")
	BlockEnergyWire UNINSULATED_GOLD_WIRE = (BlockEnergyWire) new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.MID_VOLTAGE).setEnergyLoss(1.5F).setInsulated(false).build()).dropsSelf();
	
	@RegistryName("transport/wires/gold/insulated")
	BlockEnergyWire INSULATED_GOLD_WIRE = (BlockEnergyWire) new BlockEnergyWire(EnergyWireProperties.builder().setTier(EnergyTier.MID_VOLTAGE).setEnergyLoss(0.4F).setInsulated(true).visitBlockProps(p -> p.sound(SoundType.WOOL)).build()).dropsSelf();
}