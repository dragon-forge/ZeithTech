package org.zeith.tech.modules.processing.blocks.base.machine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.zeith.hammerlib.client.screen.IAdvancedGui;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.tech.core.slot.FluidSlotBase;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

@IAdvancedGui.ApplyToJEI
public class GuiBaseMachine<C extends ContainerBaseMachine<?>>
		extends ScreenWTFMojang<C>
		implements IAdvancedGui<GuiBaseMachine<C>>
{
	public GuiBaseMachine(C container, Inventory playerInv, Component name)
	{
		super(container, playerInv, name);
	}
	
	@Override
	protected void containerTick()
	{
		menu.containerTick();
		super.containerTick();
	}
	
	@Override
	public Object getIngredientUnderMouse(double mouseX, double mouseY)
	{
		mouseX -= leftPos;
		mouseY -= topPos;
		for(FluidSlotBase slot : menu.fluidSlotBases)
			if(slot.isHovered(mouseX, mouseY))
				return slot.getFluid();
		return null;
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
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		for(var slot : menu.fluidSlotBases)
			if(slot.isHovered(mouseX - leftPos, mouseY - topPos))
				WidgetAPI.drawFluidBarOverlay(this, pose, slot.x + leftPos, slot.y + topPos, slot.getFluid(), slot.getCapacity(), true, mouseX, mouseY);
		pose.popPose();
		super.renderLabels(pose, mouseX, mouseY);
	}
	
	@Override
	protected void renderBg(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		super.renderBg(pose, partialTime, mouseX, mouseY);
		for(var slot : menu.fluidSlotBases)
			WidgetAPI.drawFluidBar(pose, leftPos + slot.x, topPos + slot.y, slot.getFluid(), slot.getCapacity());
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
	
	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int button)
	{
		return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, button)
				&& getExtraAreas().stream().noneMatch(rect2i -> rect2i.contains((int) mouseX, (int) mouseY));
	}
}
