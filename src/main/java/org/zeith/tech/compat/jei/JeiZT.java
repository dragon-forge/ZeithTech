package org.zeith.tech.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.compat.jei.hammering.ManualHammeringCategory;
import org.zeith.tech.compat.jei.machine_assembly.MachineAssemblyCategoryB;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.GuiElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.GuiMachineAssemblerB;
import org.zeith.tech.modules.processing.init.*;
import org.zeith.tech.modules.shared.init.ItemsZT;

import java.util.List;
import java.util.stream.Stream;

@JeiPlugin
public class JeiZT
		implements IModPlugin
{
	public static final ResourceLocation UID = new ResourceLocation(ZeithTech.MOD_ID, "jei");
	
	@Override
	public ResourceLocation getPluginUid()
	{
		return UID;
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		var gui$ = registration.getJeiHelpers().getGuiHelper();
		
		registration.addRecipeCategories(
				new ManualHammeringCategory(gui$),
				new MachineAssemblyCategoryB(gui$)
		);
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		registration.addRecipes(RecipeTypesZT.MANUAL_HAMMERING, RecipeRegistriesZT_Processing.HAMMERING
				.getRecipes()
				.stream()
				.filter(t -> t.isTierGoodEnough(TechTier.BASIC))
				.toList()
		);
		
		registration.addRecipes(RecipeTypesZT.MACHINE_ASSEMBLY_BASIC, RecipeRegistriesZT_Processing.MACHINE_ASSEBMLY
				.getRecipes()
				.stream()
				.filter(t -> t.isTierGoodEnough(TechTier.BASIC))
				.toList()
		);
		
		registration.addRecipes(RecipeTypes.ANVIL, getRepairRecipes(registration.getVanillaRecipeFactory()).toList());
		
		registration.addItemStackInfo(new ItemStack(ItemsZT.BOWL_OF_RESIN), Component.translatable("jei.info.zeithtech.bowl_of_resin"));
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addRecipeCatalyst(new ItemStack(BlocksZT_Processing.BASIC_FUEL_GENERATOR), RecipeTypes.FUELING);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT_Processing.BASIC_ELECTRIC_FURNACE), RecipeTypes.SMELTING);
		registration.addRecipeCatalyst(new ItemStack(ItemsZT_Processing.IRON_HAMMER), RecipeTypesZT.MANUAL_HAMMERING);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT_Processing.BASIC_MACHINE_ASSEMBLER), RecipeTypesZT.MACHINE_ASSEMBLY_BASIC);
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration)
	{
		registration.addRecipeClickArea(GuiMachineAssemblerB.class, 107, 45, 22, 15, RecipeTypesZT.MACHINE_ASSEMBLY_BASIC);
		registration.addRecipeClickArea(GuiElectricFurnaceB.class, 72, 35, 22, 15, RecipeTypes.SMELTING);
	}
	
	private static Stream<RepairData> getRepairData()
	{
		return Stream.of(
				new RepairData(RecipeHelper.fromTag(Tags.Items.INGOTS_IRON),
						new ItemStack(ItemsZT_Processing.IRON_HAMMER),
						new ItemStack(ItemsZT_Processing.WIRE_CUTTER))
		);
	}
	
	private static Stream<IJeiAnvilRecipe> getRepairRecipes(IVanillaRecipeFactory vanillaRecipeFactory)
	{
		return getRepairData().flatMap((repairData) ->
		{
			return getRepairRecipes(repairData, vanillaRecipeFactory);
		});
	}
	
	private static class RepairData
	{
		private final Ingredient repairIngredient;
		private final List<ItemStack> repairables;
		
		public RepairData(Ingredient repairIngredient, ItemStack... repairables)
		{
			this.repairIngredient = repairIngredient;
			this.repairables = List.of(repairables);
		}
		
		public Ingredient getRepairIngredient()
		{
			return this.repairIngredient;
		}
		
		public List<ItemStack> getRepairables()
		{
			return this.repairables;
		}
	}
	
	private static Stream<IJeiAnvilRecipe> getRepairRecipes(RepairData repairData, IVanillaRecipeFactory vanillaRecipeFactory)
	{
		Ingredient repairIngredient = repairData.getRepairIngredient();
		List<ItemStack> repairables = repairData.getRepairables();
		List<ItemStack> repairMaterials = List.of(repairIngredient.getItems());
		return repairables.stream().mapMulti((itemStack, consumer) ->
		{
			ItemStack damagedThreeQuarters = itemStack.copy();
			damagedThreeQuarters.setDamageValue(damagedThreeQuarters.getMaxDamage() * 3 / 4);
			ItemStack damagedHalf = itemStack.copy();
			damagedHalf.setDamageValue(damagedHalf.getMaxDamage() / 2);
			IJeiAnvilRecipe repairWithSame = vanillaRecipeFactory.createAnvilRecipe(List.of(damagedThreeQuarters), List.of(damagedThreeQuarters), List.of(damagedHalf));
			consumer.accept(repairWithSame);
			if(!repairMaterials.isEmpty())
			{
				ItemStack damagedFully = itemStack.copy();
				damagedFully.setDamageValue(damagedFully.getMaxDamage());
				IJeiAnvilRecipe repairWithMaterial = vanillaRecipeFactory.createAnvilRecipe(List.of(damagedFully), repairMaterials, List.of(damagedThreeQuarters));
				consumer.accept(repairWithMaterial);
			}
			
		});
	}
}