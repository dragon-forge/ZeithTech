package org.zeith.tech.modules.processing.items.redstone_control_tool;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.api.ZeithTechAPI;

import java.awt.*;

public class GuiRedstoneControl
		extends ScreenWTFMojang<ContainerRedstoneControl>
{
	public GuiRedstoneControl(ContainerRedstoneControl container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
	}
	
	protected Button changeMode;
	protected float sliderValue;
	protected Rectangle sliderRect;
	protected boolean draggingSlider;
	
	@Override
	protected void init()
	{
		super.init();
		
		sliderRect = new Rectangle(leftPos + 25, topPos + 53, 126, 10);
		draggingSlider = false;
		
		changeMode = addRenderableWidget(new Button(leftPos + 25, topPos + 25, 20, 20, Component.literal(""), btn ->
		{
			// OnPress
			clickMenuButton(hasShiftDown() ? -1 : 0);
		})
		{
			@Override
			protected void renderBg(PoseStack pose, Minecraft mc, int x, int y)
			{
				FXUtils.bindTexture(ZeithTechAPI.MOD_ID, "textures/gui/redstone_control_tool.png");
				RenderUtils.drawTexturedModalRect(pose, this.x, this.y, 176, menu.source.getMode().ordinal() * 20, 20, 20);
			}
		});
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int btn)
	{
		if(btn == 0 && sliderRect.contains(x, y))
		{
			draggingSlider = true;
			return false;
		}
		
		return super.mouseClicked(x, y, btn);
	}
	
	@Override
	public boolean mouseReleased(double x, double y, int btn)
	{
		if(draggingSlider && btn == 0)
		{
			int value = 1 + (int) Math.ceil(Mth.clamp((float) (x - 2 - sliderRect.x) / sliderRect.width, 0, 1) * 14F);
			if(clickMenuButton(value))
				minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
			draggingSlider = false;
		}
		
		return super.mouseReleased(x, y, btn);
	}
	
	protected boolean hasGottenData;
	
	@Override
	protected void containerTick()
	{
		super.containerTick();
		
		if(!draggingSlider)
			sliderValue = (menu.source.getThreshold() - 1) / 14F;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		int value = menu.source.getThreshold();
		
		if(draggingSlider)
		{
			value = 1 + (int) Math.ceil(Mth.clamp((float) (mouseX - 2 - sliderRect.x) / sliderRect.width, 0, 1) * 14F);
			sliderValue = (value - 1) / 14F;
		}
		
		FXUtils.bindTexture(ZeithTechAPI.MOD_ID, "textures/gui/redstone_control_tool.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		RenderUtils.drawTexturedModalRect(pose, leftPos + 26 + 120 * sliderValue, topPos + 53, 196, 0, 4, 10);
		
		pose.pushPose();
		
		float scale = 0.75F;
		
		pose.translate(leftPos + 48, topPos + 25.25F, 0);
		pose.scale(scale, scale, scale);
		int width = 102;
		width /= scale;
		int y = 0;
		for(var comp : font.split(menu.source.getMode().translate(value), width))
		{
			font.drawShadow(pose, comp, 0, y, 0xFFFFFF);
			y += 9;
		}
		
		pose.popPose();
	}
}
