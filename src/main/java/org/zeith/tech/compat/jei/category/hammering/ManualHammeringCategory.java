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
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.processing.RecipeHammering;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.init.ItemsZT_Processing;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ManualHammeringCategory
		implements IRecipeCategory<RecipeHammering>
{
	public static final ResourceLocation HAMMERING_GUI = ZeithTechAPI.id("textures/gui/jei/manual_hammering.png");
	
	private final IDrawable icon, background;
	private final Component localizedName;
	private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
	
	public ManualHammeringCategory(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(ItemsZT_Processing.IRON_HAMMER));
		this.background = guiHelper.createDrawable(HAMMERING_GUI, 0, 2, 85, 62);
		this.localizedName = Component.translatable("jei." + ZeithTech.MOD_ID + ".manual_hammering");
		
		this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<>()
		{
			@Override
			public IDrawableAnimated load(Integer cookTime)
			{
				return guiHelper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
						.buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
			}
		});
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
		return RecipeTypesZT.MANUAL_HAMMERING;
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
		getArrow(recipe).draw(stack, 24, 23);
		var font = Minecraft.getInstance().font;
		
		font.draw(stack, Component.translatable("info." + ZeithTech.MOD_ID + "_processing.hammer_hit_count", recipe.getHitCount()),
				25, (18 - font.lineHeight) / 2F, 0);
	}
	
	final Rectangle infoRect = new Rectangle(28, 45, 16, 16);
	
	@Override
	public List<Component> getTooltipStrings(RecipeHammering recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
	{
		if(infoRect.contains(mouseX, mouseY))
			return List.of(Component.translatable("jei." + ZeithTech.MOD_ID + ".manual_hammering.info", recipe.getHitCount()));
		
		return List.of();
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeHammering recipe, IFocusGroup focus)
	{
		layout.addSlot(RecipeIngredientRole.CATALYST, 1, 1).addItemStack(new ItemStack(ItemsZT_Processing.IRON_HAMMER));
		layout.addSlot(RecipeIngredientRole.INPUT, 1, 23).addIngredients(recipe.getInput());
		
		List<ItemStack> blocks = recipe.getHammeringTags().stream().<ItemStack> mapMulti((tag, $) ->
		{
			for(var holder : Registry.BLOCK.getTagOrEmpty(tag))
			{
				var item = holder.get().asItem();
				if(item != Items.AIR)
					$.accept(new ItemStack(item));
			}
		}).collect(Collectors.toList());
		
		layout.addSlot(RecipeIngredientRole.CATALYST, 1, 45).addItemStacks(blocks);
		
		layout.addSlot(RecipeIngredientRole.OUTPUT, 59, 23).addItemStack(recipe.getRecipeOutput());
	}
	
	@Override
	public @Nullable ResourceLocation getRegistryName(RecipeHammering recipe)
	{
		return recipe.id;
	}
}
