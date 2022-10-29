package org.zeith.tech.modules.processing.blocks.machine_assembler.advanced;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.base.machine.GuiBaseMachine;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.processing.items.redstone_control_tool.ContainerRedstoneControl;
import org.zeith.tech.modules.processing.items.redstone_control_tool.GuiRedstoneControl;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

import java.util.ArrayList;
import java.util.List;

public class GuiMachineAssemblerA
		extends GuiBaseMachine<ContainerMachineAssemblerA>
{
	public GuiMachineAssemblerA(ContainerMachineAssemblerA container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
		
		setSize(176, 184);
	}
	
	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();
		pose.translate(-leftPos, -topPos, 0);
		WidgetAPI.drawPowerBarOverlay(this, pose, leftPos - 18, topPos + 14, menu.tile.energy, mouseX, mouseY);
		pose.popPose();
		
		return true;
	}
	
	public final List<Rect2i> extraAreas = new ArrayList<>();
	
	@Override
	public List<Rect2i> getExtraAreas()
	{
		return extraAreas;
	}
	
	@Override
	protected void init()
	{
		super.init();
		extraAreas.clear();
		extraAreas.add(new Rect2i(leftPos + 176, topPos, 26, 94));
		extraAreas.add(new Rect2i(leftPos - 26, topPos, 26, 95));
		
		addRenderableWidget(GuiRedstoneControl.createButtonWithRedstoneState(new ContainerRedstoneControl.RedstoneModeData(menu.tile.redstone, menu.tile.getBlockPos()),
				leftPos + 175, topPos + 67,
				20, 20,
				Component.literal(""), btn -> clickMenuButton(0)));
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/gui/machine_assembler_t2.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		RenderUtils.drawTexturedModalRect(pose, leftPos + imageWidth, topPos, imageWidth, 0, 26, 94);
		RenderUtils.drawTexturedModalRect(pose, leftPos - 26, topPos, 202, 0, 26, 95);
		
		var tile = menu.tile;
		WidgetAPI.drawPowerBar(pose, leftPos - 18, topPos + 14, tile.energy.getFillRate());
		
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/gui/machine_assembler_t1.png");
		float craftProgress = Math.min(1F, tile.craftingProgress.getInt() / (float) Math.max(1, tile.craftTime.getInt()));
		if(craftProgress > 0) RenderUtils.drawTexturedModalRect(pose, leftPos + 109, topPos + 45, 176, 0, 22 * craftProgress, 16);
		
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
		
		if(tile._forcedRecipeId != null)
		{
			var recipe = RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY.getRecipe(tile._forcedRecipeId);
			if(recipe != null)
			{
				for(int i = 0; i < 25; ++i)
				{
					Ingredient ingredient = Ingredient.EMPTY;
					{
						int x = i % 5, y = i / 5;
						if(x < recipe.getWidth() && y < recipe.getHeight())
							ingredient = recipe.getRecipeItems().get(x + y * recipe.getWidth());
					}
					if(ingredient.isEmpty())
						continue;
					
					resultSlotIdx = menu.findSlot(tile.craftingInventory, i).orElse(-1);
					if(resultSlotIdx >= 0)
					{
						pose.pushPose();
						
						var slot = menu.getSlot(resultSlotIdx);
						var items = ingredient.getItems();
						var item = items[(tile.ticksExisted / 20) % items.length];
						
						if(!item.isEmpty() && !slot.hasItem())
						{
							int x = leftPos + slot.x;
							int y = topPos + slot.y;
							
							WidgetAPI.drawGhostItem(pose, x, y, item);
						}
						
						pose.popPose();
					}
				}
			}
		}
	}
}
