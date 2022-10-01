package org.zeith.tech.modules.processing.blocks.machine_assembler.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.net.Network;
import org.zeith.tech.ZeithTech;
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
		if(mouseX >= leftPos + 110 && mouseY >= topPos + 45 && mouseX < leftPos + 110 + 16 && mouseY < topPos + 45 + 16)
		{
			renderTooltip(pose, Component.translatable("gui." + ZeithTech.MOD_ID + ".start_crafting"), mouseX - leftPos, mouseY - topPos);
		}
		
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
			var recipe = tile.getActiveRecipe();
			
			if(recipe != null)
			{
				var output = recipe.getRecipeOutput(tile);
				
				int x = leftPos + slot.x;
				int y = topPos + slot.y;
				
				WidgetAPI.drawGhostItem(pose, x, y, output);
			}
			
			pose.popPose();
		}
		
		resultSlotIdx = menu.findSlot(tile.toolInventory, 0).orElse(-1);
		if(resultSlotIdx >= 0 && tile.craftResult.get().isEmpty() && craftProgress <= 0)
		{
			pose.pushPose();
			
			var slot = menu.getSlot(resultSlotIdx);
			
			if(slot.hasItem())
			{
				int x = leftPos + 110;
				int y = topPos + 45;
				
				WidgetAPI.drawGhostItem(pose, x, y, slot.getItem());
			}
			
			pose.popPose();
		}
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int btn)
	{
		if(x >= leftPos + 110 && y >= topPos + 45 && x < leftPos + 110 + 16 && y < topPos + 45 + 16)
		{
			Network.sendToServer(new PktMABStartCrafting(menu.tile.getBlockPos()));
			return true;
		}
		
		return super.mouseClicked(x, y, btn);
	}
}
