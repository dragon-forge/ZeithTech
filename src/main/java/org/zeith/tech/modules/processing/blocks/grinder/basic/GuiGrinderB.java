package org.zeith.tech.modules.processing.blocks.grinder.basic;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.ContainerUnaryRecipeMachineB;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.GuiUnaryRecipeMachineB;

public class GuiGrinderB
		extends GuiUnaryRecipeMachineB<ContainerUnaryRecipeMachineB<TileGrinderB>>
{
	public GuiGrinderB(ContainerUnaryRecipeMachineB<TileGrinderB> container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
	}
}