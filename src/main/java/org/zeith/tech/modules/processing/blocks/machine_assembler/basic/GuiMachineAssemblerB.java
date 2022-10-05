package org.zeith.tech.modules.processing.blocks.machine_assembler.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

public class GuiMachineAssemblerB
		extends GuiBaseMachine<ContainerMachineAssemblerB>
{
	public GuiMachineAssemblerB(ContainerMachineAssemblerB container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		
		setSize(176, 184);
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		return true;
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/gui/machine_assembler_t1.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		var tile = menu.tile;
		
		float craftProgress = Math.min(1F, tile.craftingProgress.getInt() / (float) Math.max(1, tile.craftTime.getInt()));
		if(craftProgress > 0) RenderUtils.drawTexturedModalRect(pose, leftPos + 107, topPos + 45, 176, 0, 22 * craftProgress, 16);
		
		var resultSlotIdx = menu.findSlot(tile.resultInventory, 0).orElse(-1);
		if(resultSlotIdx >= 0)
		{
			pose.pushPose();
			
			var slot = menu.getSlot(resultSlotIdx);
			var output = tile.craftResult.get();
			
			if(!output.isEmpty())
			{
				int x = leftPos + slot.x;
				int y = topPos + slot.y;
				
				WidgetAPI.drawGhostItem(pose, x, y, output);
			}
			
			pose.popPose();
		}
	}
}
