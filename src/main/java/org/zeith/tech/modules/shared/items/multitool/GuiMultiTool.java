package org.zeith.tech.modules.shared.items.multitool;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;

public class GuiMultiTool
		extends ScreenWTFMojang<ContainerMultiTool>
{
	public GuiMultiTool(ContainerMultiTool container, Inventory playerInv, Component name)
	{
		super(container, playerInv, name);
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/gui/multi_tool.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		for(int i = 36; i < menu.slots.size(); ++i)
		{
			var item = menu.getSlot(i);
			if(!item.hasItem())
			{
				var j = i - 36;
				RenderUtils.drawTexturedModalRect(pose, leftPos + item.x, topPos + item.y, 176, 16 * j, 16, 16);
			}
		}
	}
}