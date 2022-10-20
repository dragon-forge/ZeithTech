package org.zeith.tech.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.LogicalSide;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.LogicalSidePredictor;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.compat.jei.ITieredRecipeType;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.ITieredRecipe;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;
import org.zeith.tech.compat.BaseCompat;
import org.zeith.tech.compat.jei.category.*;
import org.zeith.tech.compat.jei.category.grinder.GrinderCategoryB;
import org.zeith.tech.compat.jei.category.hammering.AdvancedHammeringCategory;
import org.zeith.tech.compat.jei.category.hammering.ManualHammeringCategory;
import org.zeith.tech.compat.jei.category.machine_assembly.MachineAssemblyCategoryB;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.cfg.ZeithTechTransportConfigs;
import org.zeith.tech.modules.generators.blocks.fuel_generator.liquid.basic.GuiLiquidFuelGeneratorB;
import org.zeith.tech.modules.generators.blocks.fuel_generator.solid.basic.GuiSolidFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.ContainerElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.GuiElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.grinder.basic.GuiGrinderB;
import org.zeith.tech.modules.processing.blocks.grinder.basic.TileGrinderB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.ContainerMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.GuiMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.metal_press.GuiMetalPress;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.GuiSawmillB;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.TileSawmillB;
import org.zeith.tech.modules.processing.blocks.waste_processor.GuiWasteProcessor;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.ItemsZT;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class JeiZT
		extends BaseCompat
		implements IModPlugin
{
	public static final ResourceLocation UID = new ResourceLocation(ZeithTech.MOD_ID, "jei");
	
	IJeiRuntime jeiRuntime;
	List<RecipeType<?>> jeiRecipeTypes = List.of();
	
	public JeiZT()
	{
	}
	
	@Override
	public ResourceLocation getPluginUid()
	{
		return UID;
	}
	
	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{
		if(this.jeiRuntime == null) ZeithTech.compats.add(this);
		this.jeiRuntime = jeiRuntime;
		
		if(ZeithTechTransportConfigs.INSTANCE.getCurrent().main.facadesInJEI)
		{
			NonNullList<ItemStack> items = NonNullList.create();
			ZeithTech.FACADES_TAB.fillItemList(items);
			jeiRuntime.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, items);
		}
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		var gui$ = registration.getJeiHelpers().getGuiHelper();
		
		registerRecipeCategories(registration,
				new ManualHammeringCategory(gui$),
				new AdvancedHammeringCategory(gui$),
				new MachineAssemblyCategoryB(gui$),
				new GrinderCategoryB(gui$),
				new SawmillCategoryB(gui$),
				new FluidCentrifugeCategory(gui$),
				new LiquidFuelCategory(gui$),
				new WasteProcessorCategory(gui$)
		);
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		var gapi = ZeithTechAPI.get();
		var api = gapi.getRecipeRegistries();
		
		registration.addRecipes(RecipeTypesZT.MANUAL_HAMMERING, api.getRecipesUpToTier(api.hammering(), TechTier.BASIC));
		registration.addRecipes(RecipeTypesZT.ADVANCED_HAMMERING, api.getRecipesUpToTier(api.hammering(), TechTier.ADVANCED));
		registration.addRecipes(RecipeTypesZT.MACHINE_ASSEMBLY_BASIC, api.getRecipesUpToTier(api.machineAssembly(), TechTier.BASIC));
		registration.addRecipes(RecipeTypesZT.GRINDER_BASIC, api.getRecipesUpToTier(api.grinding(), TechTier.BASIC));
		registration.addRecipes(RecipeTypesZT.SAWMILL, api.sawmill().getRecipes().stream().toList());
		registration.addRecipes(RecipeTypesZT.FLUID_CENTRIFUGE, api.fluidCentrifuge().getRecipes().stream().toList());
		registration.addRecipes(RecipeTypesZT.LIQUID_FUEL, api.liquidFuel().getRecipes().stream().toList());
		registration.addRecipes(RecipeTypesZT.WASTE_PROCESSING, api.wasteProcessing().getRecipes().stream().toList());
		
		registration.addRecipes(RecipeTypes.ANVIL, getRepairRecipes(registration.getVanillaRecipeFactory()).toList());
		
		registration.addItemStackInfo(new ItemStack(ItemsZT.BOWL_OF_RESIN), Component.translatable("jei.info.zeithtech.bowl_of_resin"));
	}
	
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
	{
		registration.addRecipeTransferHandler(ContainerElectricFurnaceB.class, ContainerAPI.TILE_CONTAINER, RecipeTypes.SMELTING, 36, 1, 0, 36);
		registration.addRecipeTransferHandler(ContainerMachineAssemblerB.class, ContainerAPI.TILE_CONTAINER, RecipeTypesZT.MACHINE_ASSEMBLY_BASIC, 36, 13, 0, 36);
		registration.addRecipeTransferHandler(TileGrinderB.ContainerGrinder.class, ContainerAPI.TILE_CONTAINER, RecipeTypesZT.GRINDER_BASIC, 36, 1, 0, 36);
		registration.addRecipeTransferHandler(TileSawmillB.ContainerSawmill.class, ContainerAPI.TILE_CONTAINER, RecipeTypesZT.SAWMILL, 36, 1, 0, 36);
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addRecipeCatalyst(new ItemStack(BlocksZT.BASIC_FUEL_GENERATOR), RecipeTypes.FUELING);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT.BASIC_ELECTRIC_FURNACE), RecipeTypes.SMELTING);
		registration.addRecipeCatalyst(new ItemStack(ItemsZT.IRON_HAMMER), RecipeTypesZT.MANUAL_HAMMERING);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT.METAL_PRESS), RecipeTypesZT.ADVANCED_HAMMERING);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT.BASIC_MACHINE_ASSEMBLER), RecipeTypesZT.MACHINE_ASSEMBLY_BASIC);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT.BASIC_GRINDER), RecipeTypesZT.GRINDER_BASIC);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT.BASIC_SAWMILL), RecipeTypesZT.SAWMILL);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT.FLUID_CENTRIFUGE), RecipeTypesZT.FLUID_CENTRIFUGE);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT.BASIC_LIQUID_FUEL_GENERATOR), RecipeTypesZT.LIQUID_FUEL);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT.WASTE_PROCESSOR), RecipeTypesZT.WASTE_PROCESSING);
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration)
	{
		registration.addRecipeClickArea(GuiMachineAssemblerB.class, 107, 45, 22, 15, RecipeTypesZT.MACHINE_ASSEMBLY_BASIC);
		registration.addRecipeClickArea(GuiSolidFuelGeneratorB.class, 81, 29, 13, 14, RecipeTypes.FUELING);
		registration.addRecipeClickArea(GuiElectricFurnaceB.class, 72, 35, 22, 15, RecipeTypes.SMELTING);
		registration.addRecipeClickArea(GuiGrinderB.class, 61, 35, 22, 15, RecipeTypesZT.GRINDER_BASIC);
		registration.addRecipeClickArea(GuiSawmillB.class, 61, 35, 22, 15, RecipeTypesZT.SAWMILL);
		registration.addRecipeClickArea(GuiLiquidFuelGeneratorB.class, 84, 36, 13, 14, RecipeTypesZT.LIQUID_FUEL);
		registration.addRecipeClickArea(GuiWasteProcessor.class, 103, 38, 22, 15, RecipeTypesZT.WASTE_PROCESSING);
		registration.addRecipeClickArea(GuiMetalPress.class, 72, 35, 22, 15, RecipeTypesZT.ADVANCED_HAMMERING);
	}
	
	private static Stream<RepairData> getRepairData()
	{
		return Stream.of(
				new RepairData(RecipeHelper.fromTag(Tags.Items.INGOTS_IRON),
						new ItemStack(ItemsZT.IRON_HAMMER),
						new ItemStack(ItemsZT.WIRE_CUTTER),
						new ItemStack(ItemsZT.IRON_MINING_HEAD)),
				
				new RepairData(RecipeHelper.fromTag(Tags.Items.GEMS_DIAMOND),
						new ItemStack(ItemsZT.DIAMOND_MINING_HEAD)),
				
				new RepairData(RecipeHelper.fromTag(Tags.Items.INGOTS_NETHERITE),
						new ItemStack(ItemsZT.NETHERITE_MINING_HEAD))
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
	
	private void registerRecipeCategories(IRecipeCategoryRegistration registration, IRecipeCategory<?>... categories)
	{
		jeiRecipeTypes = Stream.of(categories).map(IRecipeCategory::getRecipeType).collect(Collectors.toList());
		registration.addRecipeCategories(categories);
	}
	
	@Override
	public <T extends IZeithTechRecipe> void onRecipeRegistered(T recipe)
	{
		super.onRecipeRegistered(recipe);
		
		if(LogicalSidePredictor.getCurrentLogicalSide() == LogicalSide.CLIENT)
		{
			for(RecipeType<?> type : jeiRecipeTypes)
			{
				if(recipe.is(type.getRecipeClass()))
				{
					RecipeType<T> type1 = Cast.cast(type);
					
					if(recipe instanceof ITieredRecipe tiered && !ITieredRecipeType.get(type)
							.map(itrt -> itrt.canHandle(tiered.getMinTier()))
							.orElse(true))
						continue;
					
					jeiRuntime.getRecipeManager().addRecipes(type1, List.of(recipe));
				}
			}
		}
	}
	
	@Override
	public <T extends IZeithTechRecipe> void onRecipeDeRegistered(T recipe)
	{
		super.onRecipeDeRegistered(recipe);
		
		if(LogicalSidePredictor.getCurrentLogicalSide() == LogicalSide.CLIENT)
		{
			for(RecipeType<?> type : jeiRecipeTypes)
			{
				if(recipe.is(type.getRecipeClass()))
				{
					RecipeType<T> type1 = Cast.cast(type);
					
					jeiRuntime.getRecipeManager().hideRecipes(type1, List.of(recipe));
				}
			}
		}
	}
}