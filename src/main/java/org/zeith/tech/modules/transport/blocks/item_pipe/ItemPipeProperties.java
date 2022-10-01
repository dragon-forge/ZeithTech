package org.zeith.tech.modules.transport.blocks.item_pipe;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

// --- About pipe speed:
// This is the value for 2 seconds per block speed.
// Use this formula for convenient speed calculation:
// pipeSpeed = 0.05 / X;
// Where X is the amount of seconds it takes to pass one pipe.

public record ItemPipeProperties(BlockBehaviour.Properties properties, float pipeSpeed)
{
	public ItemPipeProperties()
	{
		this(BlockBehaviour.Properties.of(Material.METAL).strength(2.5F).sound(SoundType.METAL), 0.01666F);
	}
	
	public static ItemPipePropertiesBuilder builder()
	{
		return ItemPipePropertiesBuilder.builder();
	}
	
	public static class ItemPipePropertiesBuilder
	{
		private BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.METAL).strength(2.5F).sound(SoundType.METAL);
		private float pipeSpeed = 0.01666F;
		
		private ItemPipePropertiesBuilder()
		{
		}
		
		public static ItemPipePropertiesBuilder builder()
		{
			return new ItemPipePropertiesBuilder();
		}
		
		public ItemPipeProperties build()
		{
			return new ItemPipeProperties(this.properties, this.pipeSpeed);
		}
		
		public ItemPipePropertiesBuilder setProperties(BlockBehaviour.Properties properties)
		{
			this.properties = properties;
			return this;
		}
		
		public ItemPipePropertiesBuilder setPipeSpeed(float pipeSpeed)
		{
			this.pipeSpeed = pipeSpeed;
			return this;
		}
		
		public BlockBehaviour.Properties getProperties()
		{
			return this.properties;
		}
		
		public float getPipeSpeed()
		{
			return this.pipeSpeed;
		}
	}
}