package org.zeith.tech.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.recipes.processing.RecipeFluidCentrifuge;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

import java.awt.*;
import java.util.List;

public class FluidCentrifugeCategory
		implements IRecipeCategory<RecipeFluidCentrifuge>
{
	public static final ResourceLocation FLUID_CENTRIFUGE_GUI = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/jei/fluid_centrifuge.png");
	public static final ResourceLocation WIDGETS = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/widgets.png");
	
	private final IDrawable icon, background, fluidGlass, energyBar, energyGlass;
	private final Component localizedName;
	private final IDrawableAnimated arrow;
	
	public FluidCentrifugeCategory(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksZT_Processing.FLUID_CENTRIFUGE));
		this.background = guiHelper.createDrawable(FLUID_CENTRIFUGE_GUI, 0, 0, 111, 66);
		this.localizedName = BlocksZT_Processing.FLUID_CENTRIFUGE.getName();
		
		this.arrow = guiHelper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
				.buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
		
		this.energyBar = guiHelper.createDrawable(WIDGETS, 27, 1, 11, 64);
		this.energyGlass = guiHelper.createDrawable(WIDGETS, 13, 0, 13, 66);
		this.fluidGlass = guiHelper.createDrawable(WIDGETS, 18, 66, 18, 66);
	}
	
	@Override
	public RecipeType<RecipeFluidCentrifuge> getRecipeType()
	{
		return RecipeTypesZT.FLUID_CENTRIFUGE;
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
	
	final Rectangle energyBarBounds = new Rectangle(1, 1, 11, 64);
	
	@Override
	public List<Component> getTooltipStrings(RecipeFluidCentrifuge recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
	{
		return energyBarBounds.contains(mouseX, mouseY)
				? List.of(Component.literal(I18n.get("info.zeithtech.fe", recipe.getEnergy())))
				: List.of();
	}
	
	@Override
	public void draw(RecipeFluidCentrifuge recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY)
	{
		arrow.draw(stack, 41, 25);
		
		energyBar.draw(stack, 1, 1);
		energyGlass.draw(stack, 0, 0);
		
		if(energyBarBounds.contains(mouseX, mouseY))
			WidgetAPI.renderSlotHighlight(stack, 1, 1, 11, 64, 0);
		
		recipe.getExtra().ifPresent(extra ->
		{
			var font = Minecraft.getInstance().font;
			float c = extra.chance();
			if(c < 1F)
				font.draw(stack, Component.literal("%.0f%%".formatted(c * 100F)), 93, 39, 0);
		});
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeFluidCentrifuge recipe, IFocusGroup focus)
	{
		FluidStack[] matching = recipe.getInput().getValues();
		
		layout.addSlot(RecipeIngredientRole.INPUT, 18, 1)
				.setFluidRenderer(recipe.getInput().amount(), false, 16, 64)
				.setOverlay(fluidGlass, -1, -1)
				.addIngredients(ForgeTypes.FLUID_STACK, List.of(matching));
		
		layout.addSlot(RecipeIngredientRole.OUTPUT, 72, 1)
				.setFluidRenderer(recipe.getOutputAmount(), false, 16, 64)
				.setOverlay(fluidGlass, -1, -1)
				.addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutput());
		
		recipe.getExtra().ifPresent(extra ->
				layout.addSlot(RecipeIngredientRole.OUTPUT, 94, 20)
						.addItemStacks(extra.getJeiItems())
		);
	}
	
	@Override
	public @Nullable ResourceLocation getRegistryName(RecipeFluidCentrifuge recipe)
	{
		return recipe.id;
	}
}