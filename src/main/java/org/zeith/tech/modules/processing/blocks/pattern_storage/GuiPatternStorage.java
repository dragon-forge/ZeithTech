package org.zeith.tech.modules.processing.blocks.pattern_storage;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.IAdvancedGui;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.tech.core.ZeithTech;

import java.util.ArrayList;
import java.util.List;

@IAdvancedGui.ApplyToJEI
public class GuiPatternStorage
		extends ScreenWTFMojang<ContainerPatternStorage>
		implements IAdvancedGui<GuiPatternStorage>
{
	protected final List<Rect2i> areas = new ArrayList<>();
	protected float scrollOffs;
	protected boolean scrolling;
	
	public GuiPatternStorage(ContainerPatternStorage container, Inventory playerInv, Component name)
	{
		super(container, playerInv, name);
		setSize(176, 204);
		inventoryLabelY = 110;
	}
	
	@Override
	protected void init()
	{
		super.init();
		areas.clear();
		areas.add(new Rect2i(leftPos + imageWidth, topPos, 19, 136));
	}
	
	@Override
	public List<Rect2i> getExtraAreas()
	{
		return areas;
	}
	
	@Override
	public boolean mouseScrolled(double p_98527_, double p_98528_, double y)
	{
		if(!menu.canScroll())
		{
			return false;
		} else
		{
			int i = ((this.menu).tile.patterns.size() + 9) / 9 - 5;
			float f = (float) (y / (double) i);
			this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
			scrollTo(this.scrollOffs);
			return true;
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(button == 0)
		{
			if(this.insideScrollbar(mouseX, mouseY))
			{
				this.scrolling = menu.canScroll();
				return true;
			}
		}
		
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	protected boolean insideScrollbar(double x, double y)
	{
		int i = this.leftPos;
		int j = this.topPos;
		int k = i + 174;
		int l = j + 18;
		int i1 = k + 14;
		int j1 = l + 112;
		return x >= (double) k && y >= (double) l && x < (double) i1 && y < (double) j1;
	}
	
	@Override
	public boolean mouseDragged(double x, double y, int p_98537_, double p_98538_, double p_98539_)
	{
		if(this.scrolling)
		{
			int i = this.topPos + 18;
			int j = i + 112;
			this.scrollOffs = ((float) y - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
			this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
			this.menu.scrollTo(this.scrollOffs);
			return true;
		} else
		{
			return super.mouseDragged(x, y, p_98537_, p_98538_, p_98539_);
		}
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(ZeithTech.MOD_ID, "textures/processing/gui/pattern_storage.png");
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		RenderUtils.drawTexturedModalRect(pose, leftPos + imageWidth, topPos, imageWidth, 0, 19, 136);
		
		int i = this.leftPos + 174;
		int j = this.topPos + 18;
		int k = j + 112;
		this.blit(pose, i, j + (int) ((float) (k - j - 17) * this.scrollOffs), 232 + (menu.canScroll() ? 0 : 12), 0, 12, 15);
	}
	
	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int button)
	{
		return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, button)
				&& getExtraAreas().stream().noneMatch(rect2i -> rect2i.contains((int) mouseX, (int) mouseY));
	}
	
	public void scrollTo(float amount)
	{
		clickMenuButton(Math.round(amount * 255F));
	}
}