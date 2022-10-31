package org.zeith.tech.compat.jei.category.hammering;

import com.google.common.cache.*;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.Constants;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.processing.RecipeHammering;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;

import java.awt.*;
import java.util.List;

public class AdvancedHammeringCategory
		implements IRecipeCategory<RecipeHammering>
{
	public static final ResourceLocation PRESS_GUI = ZeithTechAPI.id("textures/gui/jei/metal_press.png");
	public static final ResourceLocation WIDGETS = ZeithTechAPI.id("textures/gui/widgets.png");
	
	private final IDrawable icon, background, energyBar, energyGlass;
	private final Component localizedName;
	private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
	
	public AdvancedHammeringCategory(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksZT_Processing.METAL_PRESS));
		this.background = guiHelper.createDrawable(PRESS_GUI, 0, 0, 97, 66);
		this.localizedName = BlocksZT_Processing.METAL_PRESS.getName();
		
		this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<>()
		{
			@Override
			public IDrawableAnimated load(Integer cookTime)
			{
				return guiHelper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
						.buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
			}
		});
		
		this.energyBar = guiHelper.createDrawable(WIDGETS, 27, 1, 11, 64);
		this.energyGlass = guiHelper.createDrawable(WIDGETS, 13, 0, 13, 66);
	}
	
	protected IDrawableAnimated getArrow(RecipeHammering recipe)
	{
		int cookTime = recipe.getCraftTime();
		if(cookTime <= 0)
			cookTime = 200;
		return this.cachedArrows.getUnchecked(cookTime);
	}
	
	@Override
	public RecipeType<RecipeHammering> getRecipeType()
	{
		return RecipeTypesZT.ADVANCED_HAMMERING;
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
	public void draw(RecipeHammering recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY)
	{
		getArrow(recipe).draw(stack, 41, 25);
		energyBar.draw(stack, 1, 1);
		energyGlass.draw(stack, 0, 0);
	}
	
	final Rectangle energyBarBounds = new Rectangle(1, 1, 11, 64);
	
	@Override
	public List<Component> getTooltipStrings(RecipeHammering recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
	{
		return energyBarBounds.contains(mouseX, mouseY)
				? List.of(Component.literal(I18n.get("info.zeithtech.fe", recipe.getCraftTime() * 40)))
				: List.of();
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeHammering recipe, IFocusGroup focus)
	{
		layout.addSlot(RecipeIngredientRole.INPUT, 18, 25).addIngredients(recipe.getInput());
		layout.addSlot(RecipeIngredientRole.OUTPUT, 76, 25).addItemStack(recipe.getRecipeOutput());
	}
	
	@Override
	public @Nullable ResourceLocation getRegistryName(RecipeHammering recipe)
	{
		return recipe.id;
	}
}
