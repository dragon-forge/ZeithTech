package org.zeith.tech.compat.jei.category.blast_furnace;

import com.google.common.cache.*;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.util.java.Chars;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.processing.RecipeBlastFurnace;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

public class BlastFurnaceCategoryB
		implements IRecipeCategory<RecipeBlastFurnace>
{
	public static final ResourceLocation FURNACE_GUI = ZeithTechAPI.id("textures/gui/jei/blast_furnace.png");
	
	private final IDrawable icon, background;
	private final Component localizedName;
	private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
	
	public BlastFurnaceCategoryB(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksZT_Processing.BLAST_FURNACE_BURNER));
		this.background = guiHelper.createDrawable(FURNACE_GUI, 0, 0, 84, 62);
		this.localizedName = BlocksZT_Processing.BASIC_BLAST_FURNACE.getName();
		
		this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<>()
		{
			@Override
			public IDrawableAnimated load(Integer cookTime)
			{
				return guiHelper.drawableBuilder(ZeithTechAPI.id("textures/processing/gui/blast_furnace/basic.png"), 176, 0, 22, 16)
						.buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
			}
		});
	}
	
	protected IDrawableAnimated getArrow(RecipeBlastFurnace recipe)
	{
		int cookTime = recipe.getCraftTime();
		if(cookTime <= 0)
			cookTime = 200;
		return this.cachedArrows.getUnchecked(cookTime);
	}
	
	@Override
	public RecipeType<RecipeBlastFurnace> getRecipeType()
	{
		return RecipeTypesZT.BASIC_BLASTING;
	}
	
	@Override
	public Component getTitle()
	{
		return localizedName;
	}
	
	@Override
	public IDrawable getBackground()
	{
		return background;
	}
	
	@Override
	public IDrawable getIcon()
	{
		return icon;
	}
	
	@Override
	public void draw(RecipeBlastFurnace recipe, IRecipeSlotsView recipeSlotsView, PoseStack pose, double mouseX, double mouseY)
	{
		getArrow(recipe).draw(pose, 31, 24);
		WidgetAPI.drawFuelBar(pose, 6, 48, 0.5F);
		
		final int cookTime = recipe.getCraftTime();
		if(cookTime > 0)
		{
			final int cookTimeSeconds = cookTime / 20;
			final Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
			final Minecraft minecraft = Minecraft.getInstance();
			final Font fontRenderer = minecraft.font;
			final int stringWidth = fontRenderer.width(timeString);
			fontRenderer.draw(pose, timeString, (float) (this.background.getWidth() - stringWidth), 50, -8355712);
		}
		
		final float temp = recipe.getNeededTemperature();
		if(temp > 0)
		{
			final Component timeString = Component.literal("%.01f%sC".formatted(temp, Chars.DEGREE_SIGN));
			final Minecraft minecraft = Minecraft.getInstance();
			final Font fontRenderer = minecraft.font;
			final int stringWidth = fontRenderer.width(timeString);
			fontRenderer.draw(pose, timeString, (float) (this.background.getWidth() - stringWidth), 5, -8355712);
		}
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeBlastFurnace recipe, IFocusGroup focus)
	{
		layout.addSlot(RecipeIngredientRole.INPUT, 5, 24)
				.addIngredients(recipe.getInputA());
		
		layout.addSlot(RecipeIngredientRole.INPUT, 5, 1)
				.addIngredients(recipe.getInputB());
		
		layout.addSlot(RecipeIngredientRole.OUTPUT, 63, 24)
				.addItemStack(recipe.assemble());
	}
	
	@Override
	public @Nullable ResourceLocation getRegistryName(RecipeBlastFurnace recipe)
	{
		return recipe.id;
	}
}
