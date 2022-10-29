package org.zeith.tech.compat.jei.category.machine_assembly;

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
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.shared.init.ItemsZT;

import java.awt.*;
import java.util.List;

public class MachineAssemblyCategoryA
		implements IRecipeCategory<RecipeMachineAssembler>
{
	public static final ResourceLocation MACHINE_ASSEMBLER = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/jei/machine_assembler_t2.png");
	public static final ResourceLocation WIDGETS = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/widgets.png");
	
	private final IDrawable icon, background, energyBar, energyGlass;
	private final Component localizedName;
	private final IDrawableAnimated arrow;
	
	public MachineAssemblyCategoryA(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksZT_Processing.ADVANVED_MACHINE_ASSEMBLER));
		this.background = guiHelper.createDrawable(MACHINE_ASSEMBLER, 0, 0, 169, 90);
		this.localizedName = BlocksZT_Processing.ADVANVED_MACHINE_ASSEMBLER.getName();
		
		this.arrow = guiHelper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
				.buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
		
		this.energyBar = guiHelper.createDrawable(WIDGETS, 27, 1, 11, 64);
		this.energyGlass = guiHelper.createDrawable(WIDGETS, 13, 0, 13, 66);
	}
	
	@Override
	public RecipeType<RecipeMachineAssembler> getRecipeType()
	{
		return RecipeTypesZT.MACHINE_ASSEMBLY_ADVANCED;
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
	public void draw(RecipeMachineAssembler recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY)
	{
		arrow.draw(stack, 113, 37);
		energyBar.draw(stack, 1, 13);
		energyGlass.draw(stack, 0, 12);
	}
	
	final Rectangle energyBarBounds = new Rectangle(1, 13, 11, 64);
	
	@Override
	public java.util.List<Component> getTooltipStrings(RecipeMachineAssembler recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
	{
		return energyBarBounds.contains(mouseX, mouseY)
				? java.util.List.of(Component.literal(I18n.get("info.zeithtech.fe", 64 * 100)))
				: List.of();
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeMachineAssembler recipe, IFocusGroup focus)
	{
		layout.addSlot(RecipeIngredientRole.OUTPUT, 148, 37).addItemStack(recipe.getRecipeOutput());
		
		layout.addSlot(RecipeIngredientRole.INPUT, 148, 66)
				.addItemStack(ItemsZT.RECIPE_PATTERN.createEncoded(RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY, recipe))
				.setSlotName("exclude_from_transfer");
		
		layout.addSlot(RecipeIngredientRole.OUTPUT, 148, 66).addItemStack(ItemsZT.RECIPE_PATTERN.createEncoded(RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY, recipe));
		
		for(int y = 0; y < 5; ++y)
		{
			int start = 0;
			int end = 5;
			
			if(y == 0 || y == 4)
			{
				start = 1;
				end = 4;
			}
			
			for(int x = start; x < end; ++x)
				layout.addSlot(RecipeIngredientRole.INPUT, 18 + x * 18, 1 + y * 18)
						.addIngredients(recipe.getRecipeItems().get(x + y * 5));
		}
	}
	
	@Override
	public @Nullable ResourceLocation getRegistryName(RecipeMachineAssembler recipe)
	{
		return recipe.id;
	}
}
