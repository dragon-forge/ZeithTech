package org.zeith.tech.modules.processing.blocks.sawmill.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.ContainerUnaryRecipeMachineB;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.GuiUnaryRecipeMachineB;

public class GuiSawmillB
		extends GuiUnaryRecipeMachineB<ContainerUnaryRecipeMachineB<TileSawmillB>>
{
	public GuiSawmillB(ContainerUnaryRecipeMachineB<TileSawmillB> container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
	}
	
	@Override
	protected void drawProgress(PoseStack pose, float progress)
	{
		RenderUtils.drawTexturedModalRect(pose, leftPos + 61, topPos + 35, 176, 0, 22 * progress, 16);
	}
}