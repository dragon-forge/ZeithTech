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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.zeith.tech.api.recipes.processing.RecipeLiquidFuel;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.generators.init.BlocksZT_Generators;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

import java.awt.*;
import java.util.List;

public class LiquidFuelCategory
		implements IRecipeCategory<RecipeLiquidFuel>
{
	public static final ResourceLocation FLUID_CENTRIFUGE_GUI = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/jei/liquid_fuel.png");
	public static final ResourceLocation WIDGETS = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/widgets.png");
	
	private final IDrawable icon, background, fluidGlass, energyBar, energyGlass;
	private final Component localizedName;
	private final IDrawableAnimated arrow;
	
	public LiquidFuelCategory(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksZT_Generators.BASIC_LIQUID_FUEL_GENERATOR));
		this.background = guiHelper.createDrawable(FLUID_CENTRIFUGE_GUI, 0, 0, 67, 66);
		this.localizedName = Component.translatable("info.zeithtech.liquid_fuel");
		
		this.arrow = guiHelper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
				.buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
		
		this.energyBar = guiHelper.createDrawable(WIDGETS, 27, 1, 11, 64);
		this.energyGlass = guiHelper.createDrawable(WIDGETS, 13, 0, 13, 66);
		this.fluidGlass = guiHelper.createDrawable(WIDGETS, 18, 66, 18, 66);
	}
	
	@Override
	public RecipeType<RecipeLiquidFuel> getRecipeType()
	{
		return RecipeTypesZT.LIQUID_FUEL;
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
	
	final Rectangle energyBarBounds = new Rectangle(55, 1, 11, 64);
	
	@Override
	public java.util.List<Component> getTooltipStrings(RecipeLiquidFuel recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
	{
		return energyBarBounds.contains(mouseX, mouseY)
				? java.util.List.of(Component.literal(I18n.get("info.zeithtech.fe", recipe.burnTime() * 40)))
				: java.util.List.of();
	}
	
	@Override
	public void draw(RecipeLiquidFuel recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY)
	{
		arrow.draw(stack, 25, 25);
		
		energyBar.draw(stack, 55, 1);
		energyGlass.draw(stack, 54, 0);
		
		if(energyBarBounds.contains(mouseX, mouseY))
			WidgetAPI.renderSlotHighlight(stack, 55, 1, 11, 64, 0);
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeLiquidFuel recipe, IFocusGroup focus)
	{
		FluidStack[] matching = recipe.ingredient().getValues(100);
		
		layout.addSlot(RecipeIngredientRole.INPUT, 1, 1)
				.setFluidRenderer(100, false, 16, 64)
				.setOverlay(fluidGlass, -1, -1)
				.addIngredients(ForgeTypes.FLUID_STACK, List.of(matching));
	}
}