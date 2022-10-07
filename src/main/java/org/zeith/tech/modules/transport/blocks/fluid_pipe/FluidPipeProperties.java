package org.zeith.tech.modules.transport.blocks.fluid_pipe;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public record FluidPipeProperties(BlockBehaviour.Properties properties, int storageVolume, int transferVolume, int maxTemperature)
{
	public FluidPipeProperties()
	{
		this(BlockBehaviour.Properties.of(Material.METAL).strength(2.5F).sound(SoundType.METAL), 100, 50, Integer.MAX_VALUE);
	}
	
	public static FluidPipePropertiesBuilder builder()
	{
		return FluidPipePropertiesBuilder.builder();
	}
	
	public static class FluidPipePropertiesBuilder
	{
		private BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.METAL).strength(2.5F).sound(SoundType.METAL);
		private int storageVolume = 100;
		private int transferVolume = 50;
		private int maxTemperature = Integer.MAX_VALUE;
		
		private FluidPipePropertiesBuilder()
		{
		}
		
		public static FluidPipePropertiesBuilder builder()
		{
			return new FluidPipePropertiesBuilder();
		}
		
		public FluidPipeProperties build()
		{
			return new FluidPipeProperties(this.properties, this.storageVolume, this.transferVolume, this.maxTemperature);
		}
		
		
		public FluidPipePropertiesBuilder setProperties(BlockBehaviour.Properties properties)
		{
			this.properties = properties;
			return this;
		}
		
		public FluidPipePropertiesBuilder setStorageVolume(int storageVolume)
		{
			this.storageVolume = storageVolume;
			return this;
		}
		
		public FluidPipePropertiesBuilder setTransferVolume(int transferVolume)
		{
			this.transferVolume = transferVolume;
			return this;
		}
		
		public FluidPipePropertiesBuilder setMaxTemperature(int maxTemperature)
		{
			this.maxTemperature = maxTemperature;
			return this;
		}
		
		public BlockBehaviour.Properties getProperties()
		{
			return this.properties;
		}
		
		public int getStorageVolume()
		{
			return this.storageVolume;
		}
		
		public int getTransferVolume()
		{
			return this.transferVolume;
		}
		
		public int getMaxTemperature()
		{
			return this.maxTemperature;
		}
	}
}