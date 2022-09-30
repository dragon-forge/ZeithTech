package org.zeith.tech.common.blocks.item_pipe;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class PipeProperties
{
	public final BlockBehaviour.Properties blockProps;
	
	// This is the value for 2 seconds per block speed.
	// Use this formula for convenient speed calculation:
	// pipeSpeed = 0.05 / X;
	// Where X is the amount of seconds it takes to pass one pipe.
	private float pipeSpeed = 0.01666F;
	
	public PipeProperties(BlockBehaviour.Properties blockProps)
	{
		this.blockProps = blockProps;
	}
	
	public PipeProperties setPipeSpeed(float pipeSpeed)
	{
		this.pipeSpeed = pipeSpeed;
		return this;
	}
	
	public float getPipeSpeed()
	{
		return pipeSpeed;
	}
}