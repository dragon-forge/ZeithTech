package org.zeith.tech.modules.processing.blocks.base.unary_machine.basic;

import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;

public abstract class BlockUnaryRecipeMachineB<T extends TileUnaryRecipeMachineB<T, ?>>
		extends BlockBaseMachine<T>
{
	public BlockUnaryRecipeMachineB(Class<T> tileType)
	{
		super(tileType);
	}
}