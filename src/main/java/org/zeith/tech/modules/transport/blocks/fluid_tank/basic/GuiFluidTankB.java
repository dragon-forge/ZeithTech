package org.zeith.tech.modules.transport.blocks.fluid_tank.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.IAdvancedGui;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@IAdvancedGui.ApplyToJEI
public class GuiFluidTankB
		extends GuiBaseMachine<ContainerFluidTankB>
		implements IAdvancedGui<GuiFluidTankB>
{
	public GuiFluidTankB(ContainerFluidTankB container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		setSize(176, 166);
	}
	
	Rectangle switchRect = new Rectangle(65, 34, 13, 18),
			fluidRect = new Rectangle(96, 10, 18, 66);
	
	@Override
	public Object getIngredientUnderMouse(double mouseX, double mouseY)
	{
		mouseX -= leftPos;
		mouseY -= topPos;
		if(fluidRect.contains(mouseX, mouseY))
			return menu.tile.storage.getFluid();
		return null;
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int btn)
	{
		if(btn == 0 && switchRect.contains(x - leftPos, y - topPos) && clickMenuButton(0))
		{
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
			return true;
		}
		return super.mouseClicked(x, y, btn);
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		WidgetAPI.drawFluidBarOverlay(this, pose, 96 + leftPos, 10 + topPos, menu.tile.storage, true, mouseX, mouseY);
		if(switchRect.contains(mouseX - leftPos, mouseY - topPos))
			renderTooltip(pose, List.of(Component.translatable("info.zeithtech.fluid_tank." + (menu.tile.fillItem ? "fill" : "drain")), Component.translatable("info.zeithtech.fluid_tank.toggle_mode")), Optional.empty(), mouseX, mouseY);
		pose.popPose();
		
		
		return false;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		menu.tile.storage.setFluid(menu.tile.tankSmooth.getClientAverage(minecraft.getPartialTick()));
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/transport/gui/fluid_tank.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		var tile = menu.tile;
		WidgetAPI.drawFluidBar(pose, leftPos + 96, topPos + 10, tile.storage);
	}
}