package org.zeith.tech.modules.transport.blocks.energy_cable;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.zeith.tech.api.energy.EnergyTier;

public record EnergyCableProperties(BlockBehaviour.Properties blockProps, EnergyTier tier, float energyLoss, boolean burns)
{
	public EnergyCableProperties(EnergyTier tier, float energyLoss, boolean burns)
	{
		this(BlockBehaviour.Properties.of(Material.METAL).strength(2.5F).sound(SoundType.METAL), tier, energyLoss, burns);
	}
	
	public static EnergyCablePropertiesBuilder builder()
	{
		return EnergyCablePropertiesBuilder.builder();
	}
	
	public static class EnergyCablePropertiesBuilder
	{
		private BlockBehaviour.Properties blockProps = BlockBehaviour.Properties.of(Material.METAL).strength(2.5F).sound(SoundType.METAL);
		private EnergyTier tier;
		private float energyLoss;
		private boolean burns;
		
		private EnergyCablePropertiesBuilder()
		{
		}
		
		public static EnergyCablePropertiesBuilder builder()
		{
			return new EnergyCablePropertiesBuilder();
		}
		
		public EnergyCableProperties build()
		{
			return new EnergyCableProperties(this.blockProps, this.tier, this.energyLoss, this.burns);
		}
		
		public EnergyCablePropertiesBuilder setBlockProps(BlockBehaviour.Properties blockProps)
		{
			this.blockProps = blockProps;
			return this;
		}
		
		public EnergyCablePropertiesBuilder setTier(EnergyTier tier)
		{
			this.tier = tier;
			return this;
		}
		
		public EnergyCablePropertiesBuilder setEnergyLoss(float energyLoss)
		{
			this.energyLoss = energyLoss;
			return this;
		}
		
		public EnergyCablePropertiesBuilder setBurns(boolean burns)
		{
			this.burns = burns;
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
	}
}