package org.zeith.tech.modules.processing.blocks.base.machine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

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
	
	@Override
	public void renderSlot(PoseStack pose, Slot slot)
	{
		var mslot = menu.getMappedSlots().get(slot);
		
		if(mslot != null)
		{
			var color = mslot.getColor();
			
			RenderSystem.enableBlend();
			
			WidgetAPI.bind();
			RenderSystem.setShaderColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.25F);
			blit(pose, slot.x - 1, slot.y - 1, 39, 27, 18, 18);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
		
		super.renderSlot(pose, slot);
	}
}
