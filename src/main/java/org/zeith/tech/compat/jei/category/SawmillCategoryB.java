package org.zeith.tech.compat.jei.category;

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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.processing.RecipeSawmill;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.TileSawmillB;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;

import java.util.stream.Stream;

public class SawmillCategoryB
		implements IRecipeCategory<RecipeSawmill>
{
	public static final ResourceLocation SAWMILL_GUI = ZeithTechAPI.id("textures/gui/jei/sawmill/basic.png");
	
	private final IDrawable icon, background;
	private final Component localizedName;
	private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
	
	public SawmillCategoryB(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksZT_Processing.BASIC_SAWMILL));
		this.background = guiHelper.createDrawable(SAWMILL_GUI, 0, 0, 104, 28);
		this.localizedName = BlocksZT_Processing.BASIC_SAWMILL.getName();
		
		this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<>()
		{
			@Override
			public IDrawableAnimated load(Integer cookTime)
			{
				return guiHelper.drawableBuilder(TileSawmillB.SAWMILL_GUI_TEXTURE, 176, 0, 22, 16)
						.buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
			}
		});
	}
	
	protected IDrawableAnimated getArrow(RecipeSawmill recipe)
	{
		int cookTime = recipe.getCraftTime();
		if(cookTime <= 0)
			cookTime = 200;
		return this.cachedArrows.getUnchecked(cookTime);
	}
	
	@Override
	public RecipeType<RecipeSawmill> getRecipeType()
	{
		return RecipeTypesZT.SAWMILL;
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
	public void draw(RecipeSawmill recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY)
	{
		getArrow(recipe).draw(stack, 25, 6);
		
		recipe.getExtra().ifPresent(extra ->
		{
			var font = Minecraft.getInstance().font;
			float c = extra.chance();
			if(c < 1F)
				font.draw(stack, Component.literal("%.0f%%".formatted(c * 100F)), 86, 20, 0);
		});
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeSawmill recipe, IFocusGroup focus)
	{
		var items = Stream.of(recipe.getInput().getItems())
				.map(s ->
				{
					s = s.copy();
					s.setCount(s.getCount() * recipe.getInputCount());
					return s;
				}).toList();
		
		layout.addSlot(RecipeIngredientRole.INPUT, 1, 6)
				.addItemStacks(items);
		
		layout.addSlot(RecipeIngredientRole.OUTPUT, 61, 6)
				.addItemStack(recipe.getRecipeOutput());
		
		recipe.getExtra().ifPresent(extra ->
				layout.addSlot(RecipeIngredientRole.OUTPUT, 87, 1)
						.addItemStacks(extra.getJeiItems())
		);
	}
	
	@Override
	public @Nullable ResourceLocation getRegistryName(RecipeSawmill recipe)
	{
		return recipe.id;
	}
}
