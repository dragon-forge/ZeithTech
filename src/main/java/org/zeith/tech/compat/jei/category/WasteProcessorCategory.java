package org.zeith.tech.compat.jei.category;

import com.google.common.cache.*;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.recipes.processing.RecipeWasteProcessor;
import org.zeith.tech.compat.jei.RecipeTypesZT;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.waste_processor.TileWasteProcessor;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.shared.client.gui.WidgetAPI;

import java.awt.*;
import java.util.List;

public class WasteProcessorCategory
		implements IRecipeCategory<RecipeWasteProcessor>
{
	public static final ResourceLocation WASTE_PROCESSOR_GUI = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/jei/waste_processor.png");
	public static final ResourceLocation WIDGETS = new ResourceLocation(ZeithTech.MOD_ID, "textures/gui/widgets.png");
	
	private final IDrawable icon, background, fluidGlass, energyBar, energyGlass;
	private final Component localizedName;
	private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
	
	public WasteProcessorCategory(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksZT_Processing.WASTE_PROCESSOR));
		this.background = guiHelper.createDrawable(WASTE_PROCESSOR_GUI, 0, 0, 157, 66);
		this.localizedName = BlocksZT_Processing.WASTE_PROCESSOR.getName();
		
		this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<>()
		{
			@Override
			public IDrawableAnimated load(Integer cookTime)
			{
				return guiHelper.drawableBuilder(TileWasteProcessor.WASTE_PROCESSOR_GUI_TEXTURE, 0, 172, 22, 16)
						.buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
			}
		});
		
		this.energyBar = guiHelper.createDrawable(WIDGETS, 27, 1, 11, 64);
		this.energyGlass = guiHelper.createDrawable(WIDGETS, 13, 0, 13, 66);
		this.fluidGlass = guiHelper.createDrawable(WIDGETS, 18, 66, 18, 66);
	}
	
	protected IDrawableAnimated getArrow(RecipeWasteProcessor recipe)
	{
		int cookTime = recipe.getTime();
		if(cookTime <= 0) cookTime = 200;
		return this.cachedArrows.getUnchecked(cookTime);
	}
	
	@Override
	public RecipeType<RecipeWasteProcessor> getRecipeType()
	{
		return RecipeTypesZT.WASTE_PROCESSING;
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
	public List<Component> getTooltipStrings(RecipeWasteProcessor recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
	{
		return energyBarBounds.contains(mouseX, mouseY)
				? List.of(Component.literal(I18n.get("info.zeithtech.fe", recipe.getTime() * 40)))
				: List.of();
	}
	
	@Override
	public void draw(RecipeWasteProcessor recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY)
	{
		getArrow(recipe).draw(stack, 75, 25);
		
		energyBar.draw(stack, 1, 1);
		energyGlass.draw(stack, 0, 0);
		
		if(energyBarBounds.contains(mouseX, mouseY))
			WidgetAPI.renderSlotHighlight(stack, 1, 1, 11, 64, 0);
		
		var bp = recipe.getByproduct();
		for(int i = 0; i < Math.min(bp.size(), 3); i++)
		{
			var extra = bp.get(i);
			
			var font = Minecraft.getInstance().font;
			float c = extra.chance();
			if(c < 1F)
			{
				var txt = Component.literal("%.0f%%".formatted(c * 100F));
				font.draw(stack, txt, 140 + 16 - font.width(txt), 1 + 24 * i, 0);
			}
		}
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder layout, RecipeWasteProcessor recipe, IFocusGroup focus)
	{
		var inA = recipe.getInputA();
		var inB = recipe.getInputB();
		var inItem = recipe.getInputItem();
		var outA = recipe.getOutputA();
		var outB = recipe.getOutputB();
		
		
		layout.addSlot(RecipeIngredientRole.INPUT, 16, 1)
				.setFluidRenderer(Math.max(inA.amount(), 1), false, 16, 64)
				.setOverlay(fluidGlass, -1, -1)
				.addIngredients(ForgeTypes.FLUID_STACK, List.of(inA.getValues()));
		
		layout.addSlot(RecipeIngredientRole.INPUT, 36, 1)
				.setFluidRenderer(Math.max(inB.amount(), 1), false, 16, 64)
				.setOverlay(fluidGlass, -1, -1)
				.addIngredients(ForgeTypes.FLUID_STACK, List.of(inB.getValues()));
		
		if(!inItem.isEmpty())
			layout.addSlot(RecipeIngredientRole.INPUT, 56, 25).addIngredients(inItem);
		
		var ing = layout.addSlot(RecipeIngredientRole.OUTPUT, 100, 1)
				.setFluidRenderer(Math.max(outA.getAmount(), 1), false, 16, 64)
				.setOverlay(fluidGlass, -1, -1);
		
		if(!outA.isEmpty()) ing.addIngredient(ForgeTypes.FLUID_STACK, outA);
		
		ing = layout.addSlot(RecipeIngredientRole.OUTPUT, 120, 1)
				.setFluidRenderer(Math.max(outB.getAmount(), 1), false, 16, 64)
				.setOverlay(fluidGlass, -1, -1);
		
		if(!outB.isEmpty()) ing.addIngredient(ForgeTypes.FLUID_STACK, outB);
		
		var bp = recipe.getByproduct();
		for(int i = 0; i < Math.min(bp.size(), 3); i++)
			layout.addSlot(RecipeIngredientRole.OUTPUT, 140, 1 + 24 * i)
					.addItemStacks(bp.get(i).getJeiItems());
	}
	
	@Override
	public @Nullable ResourceLocation getRegistryName(RecipeWasteProcessor recipe)
	{
		return recipe.id;
	}
}