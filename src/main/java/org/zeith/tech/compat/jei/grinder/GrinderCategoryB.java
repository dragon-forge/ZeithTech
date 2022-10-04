package org.zeith.tech.compat.jei.grinder;

import com.google.common.cache.*;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.recipes.processing.RecipeGrinding;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.modules.processing.blocks.grinder.basic.TileGrinderB;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;

import java.util.stream.Stream;

public class GrinderCategoryB
		implements IRecipeCategory<RecipeGrinding>
{
	public static final ResourceLocation GRINDER_GUI = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/jei/grinder/basic.png");
	
	private final IDrawable icon, background;
	private final Component localizedName;
	private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
	
	public GrinderCategoryB(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksZT_Processing.BASIC_GRINDER));
		this.background = guiHelper.createDrawable(GRINDER_GUI, 0, 0, 82, 26);
		this.localizedName = BlocksZT_Processing.BASIC_GRINDER.getName();
		
		this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<>()
		{
			@Override
			public IDrawableAnimated load(Integer cookTime)
			{
				return guiHelper.drawableBuilder(TileGrinderB.GRINDER_GUI_TEXTURE, 176, 0, 22, 16)
						.buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
			}
		});
	}
	
	protected IDrawableAnimated getArrow(RecipeGrinding recipe)
	{
		int cookTime = recipe.getCraftTime();
		if(cookTime <= 0)
			cookTime = 200;
		return this.cachedArrows.getUnchecked(cookTime);
	}
	
	@Override
	public RecipeType<RecipeGrinding> getRecipeType()
	{
		return RecipeTypesZT.GRINDER_BASIC;
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
	public void draw(RecipeGrinding recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY)
	{
		getArrow(recipe).draw(stack, 25, 5);
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeGrinding recipe, IFocusGroup focus)
	{
		var items = Stream.of(recipe.getInput().getItems())
				.map(s ->
				{
					s = s.copy();
					s.setCount(s.getCount() * recipe.getInputCount());
					return s;
				}).toList();
		
		layout.addSlot(RecipeIngredientRole.INPUT, 1, 5)
				.addItemStacks(items);
		
		layout.addSlot(RecipeIngredientRole.OUTPUT, 61, 5)
				.addItemStack(recipe.getRecipeOutput());
	}
}
