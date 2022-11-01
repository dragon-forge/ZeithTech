package org.zeith.tech.compat.patchouli.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.RecipeRegistriesZT;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipe;

public class PageMachineAssembly
		extends PageDoubleRecipe<RecipeMachineAssembler>
{
	private static final ResourceLocation TEXTURE = ZeithTechAPI.id("textures/gui/patchouli/machine_assembler.png");
	
	@Override
	protected void drawRecipe(PoseStack pose, RecipeMachineAssembler recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second)
	{
		recipeY += 4;
		
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.enableBlend();
		GuiComponent.blit(pose, recipeX - 2, recipeY - 2, 102.0F * recipe.getMinTier().ordinal(), 0.0F, 102, 139, 306, 139);
		
		this.parent.drawCenteredStringNoShadow(pose, this.getTitle(second).getVisualOrderText(), 58, recipeY - 12, this.book.headerColor);
		this.parent.renderItemStack(pose, recipeX + 41, recipeY + 115, mouseX, mouseY, recipe.getRecipeOutput());
		
		NonNullList<Ingredient> ingredients = recipe.getRecipeItems();
		int wrap = recipe.getWidth();
		
		for(int i = 0; i < ingredients.size(); ++i)
			this.parent.renderIngredient(pose, recipeX + i % wrap * 19 + 3, recipeY + i / wrap * 19 + 2, mouseX, mouseY, ingredients.get(i));
		
		ItemStack machine = switch(recipe.getMinTier())
				{
					case BASIC -> new ItemStack(BlocksZT.BASIC_MACHINE_ASSEMBLER);
					case ADVANCED -> new ItemStack(BlocksZT.ADVANCED_MACHINE_ASSEMBLER);
					default -> new ItemStack(Blocks.BARRIER);
				};
		
		this.parent.renderItemStack(pose, recipeX + 20, recipeY + 115, mouseX, mouseY, machine);
	}
	
	@Override
	protected RecipeMachineAssembler loadRecipe(BookContentsBuilder bookContentsBuilder, BookEntry bookEntry, ResourceLocation recipe)
	{
		return RecipeRegistriesZT.MACHINE_ASSEMBLY.getRecipes()
				.stream()
				.filter(f -> f.getRecipeName().equals(recipe))
				.findFirst()
				.orElse(null);
	}
	
	@Override
	protected ItemStack getRecipeOutput(RecipeMachineAssembler recipe)
	{
		return recipe.getRecipeOutput();
	}
	
	@Override
	protected int getRecipeHeight()
	{
		return 139;
	}
}