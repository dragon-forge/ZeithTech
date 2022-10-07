package org.zeith.tech.modules.transport.blocks.energy_wire;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.zeith.tech.api.energy.EnergyTier;

import java.util.function.UnaryOperator;

public record EnergyWireProperties(BlockBehaviour.Properties blockProps, EnergyTier tier, float energyLoss, boolean burns, boolean insulated)
{
	public EnergyWireProperties(EnergyTier tier, float energyLoss, boolean burns, boolean insulated)
	{
		this(BlockBehaviour.Properties.of(Material.METAL).strength(2.5F).dynamicShape().sound(SoundType.METAL), tier, energyLoss, burns, insulated);
	}
	
	public static EnergyWirePropertiesBuilder builder()
	{
		return EnergyWirePropertiesBuilder.builder();
	}
	
	public static class EnergyWirePropertiesBuilder
	{
		private BlockBehaviour.Properties blockProps = BlockBehaviour.Properties.of(Material.METAL).dynamicShape().strength(0.5F).sound(SoundType.METAL);
		private EnergyTier tier;
		private float energyLoss;
		private boolean burns = true;
		private boolean insulated;
		
		private EnergyWirePropertiesBuilder()
		{
		}
		
		public static EnergyWirePropertiesBuilder builder()
		{
			return new EnergyWirePropertiesBuilder();
		}
		
		public EnergyWireProperties build()
		{
			return new EnergyWireProperties(this.blockProps, this.tier, this.energyLoss, this.burns, this.insulated);
		}
		
		
		public EnergyWirePropertiesBuilder setBlockProps(BlockBehaviour.Properties blockProps)
		{
			this.blockProps = blockProps;
			return this;
		}
		
		public EnergyWirePropertiesBuilder visitBlockProps(UnaryOperator<BlockBehaviour.Properties> blockProps)
		{
			this.blockProps = blockProps.apply(this.blockProps);
			return this;
		}
		
		public EnergyWirePropertiesBuilder setTier(EnergyTier tier)
		{
			this.tier = tier;
			return this;
		}
		
		public EnergyWirePropertiesBuilder setEnergyLoss(float energyLoss)
		{
			this.energyLoss = energyLoss;
			return this;
		}
		
		public EnergyWirePropertiesBuilder setBurns(boolean burns)
		{
			this.burns = burns;
			return this;
		}
		
		public EnergyWirePropertiesBuilder setInsulated(boolean insulated)
		{
			this.insulated = insulated;
			return this;
		}
		
		public BlockBehaviour.Properties getBlockProps()
		{
			return this.blockProps;
		}
		
		public EnergyTier getTier()
		{
			return this.tier;
		}
		
		public float getEnergyLoss()
		{
			return this.energyLoss;
		}
		
		public boolean getBurns()
		{
			return this.burns;
		}
		
		public boolean getInsulated()
		{
			return this.insulated;
		}
	}
	
}