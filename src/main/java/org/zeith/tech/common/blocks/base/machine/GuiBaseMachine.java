package org.zeith.tech.common.blocks.base.machine;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;

public class GuiBaseMachine<C extends ContainerBaseMachine<?>>
		extends ScreenWTFMojang<C>
{
	public GuiBaseMachine(C container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
	}
	
	@Override
	protected void containerTick()
	{
		menu.containerTick();
		super.containerTick();
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		return false;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
	
	}
}
