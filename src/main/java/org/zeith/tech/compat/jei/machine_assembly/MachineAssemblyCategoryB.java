package org.zeith.tech.compat.jei.machine_assembly;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.recipes.RecipeMachineAssembler;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.processing.init.ItemsZT_Processing;

import java.awt.*;

public class MachineAssemblyCategoryB
		implements IRecipeCategory<RecipeMachineAssembler>
{
	public static final ResourceLocation MACHINE_ASSEMBLER = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/jei/machine_assembler_t1.png");
	
	private final IDrawable icon, background;
	private final Component localizedName;
	private final IDrawableAnimated arrow;
	
	public MachineAssemblyCategoryB(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksZT_Processing.MACHINE_ASSEMBLER_BASIC));
		this.background = guiHelper.createDrawable(MACHINE_ASSEMBLER, 0, 0, 152, 90);
		this.localizedName = BlocksZT_Processing.MACHINE_ASSEMBLER_BASIC.getName();
		
		this.arrow = guiHelper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
				.buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
	}
	
	@Override
	public RecipeType<RecipeMachineAssembler> getRecipeType()
	{
		return RecipeTypesZT.MACHINE_ASSEMBLY_BASIC;
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
		arrow.draw(stack, 97, 37);
	}
	
	final Rectangle infoRect = new Rectangle(97, 37, 22, 15);
	
	@Override
	public java.util.List<Component> getTooltipStrings(RecipeMachineAssembler recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
	{
		if(infoRect.contains(mouseX, mouseY))
			return java.util.List.of(Component.translatable("jei." + ZeithTech.MOD_ID + ".machine_assembler_t1.info"));
		
		return java.util.List.of();
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeMachineAssembler recipe, IFocusGroup focus)
	{
		layout.addSlot(RecipeIngredientRole.CATALYST, 100, 16).addItemStack(new ItemStack(ItemsZT_Processing.IRON_HAMMER));
		layout.addSlot(RecipeIngredientRole.OUTPUT, 131, 37).addItemStack(recipe.getRecipeOutput());
		
		for(int y = 0; y < 5; ++y)
		{
			int start = 0;
			int end = 5;
			
			if(y == 0 || y == 4)
			{
				start = 2;
				end = 3;
			} else if(y == 1 || y == 3)
			{
				start = 1;
				end = 4;
			}
			
			for(int x = start; x < end; ++x)
			{
				layout.addSlot(RecipeIngredientRole.INPUT, 1 + x * 18, 1 + y * 18)
						.addIngredients(recipe.getRecipeItems().get(x + y * 5));
			}
		}
	}
}
