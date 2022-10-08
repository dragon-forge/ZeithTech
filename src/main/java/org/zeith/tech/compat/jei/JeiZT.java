package org.zeith.tech.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
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
import org.zeith.tech.compat.jei.category.FluidCentrifugeCategory;
import org.zeith.tech.compat.jei.category.SawmillCategoryB;
import org.zeith.tech.compat.jei.category.grinder.GrinderCategoryB;
import org.zeith.tech.compat.jei.category.hammering.ManualHammeringCategory;
import org.zeith.tech.compat.jei.category.machine_assembly.MachineAssemblyCategoryB;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.ContainerElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.electric_furnace.basic.GuiElectricFurnaceB;
import org.zeith.tech.modules.processing.blocks.fuelgen.basic.GuiFuelGeneratorB;
import org.zeith.tech.modules.processing.blocks.grinder.basic.GuiGrinderB;
import org.zeith.tech.modules.processing.blocks.grinder.basic.TileGrinderB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.ContainerMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.machine_assembler.basic.GuiMachineAssemblerB;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.GuiSawmillB;
import org.zeith.tech.modules.processing.blocks.sawmill.basic.TileSawmillB;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.processing.init.ItemsZT_Processing;
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
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		var gui$ = registration.getJeiHelpers().getGuiHelper();
		
		registerRecipeCategories(registration,
				new ManualHammeringCategory(gui$),
				new MachineAssemblyCategoryB(gui$),
				new GrinderCategoryB(gui$),
				new SawmillCategoryB(gui$),
				new FluidCentrifugeCategory(gui$)
		);
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		var api = ZeithTechAPI.get().getRecipeRegistries();
		
		registration.addRecipes(RecipeTypesZT.MANUAL_HAMMERING, api.getRecipesUpToTier(api.hammering(), TechTier.BASIC));
		registration.addRecipes(RecipeTypesZT.MACHINE_ASSEMBLY_BASIC, api.getRecipesUpToTier(api.machineAssembly(), TechTier.BASIC));
		registration.addRecipes(RecipeTypesZT.GRINDER_BASIC, api.getRecipesUpToTier(api.grinding(), TechTier.BASIC));
		registration.addRecipes(RecipeTypesZT.SAWMILL, api.sawmill().getRecipes().stream().toList());
		registration.addRecipes(RecipeTypesZT.FLUID_CENTRIFUGE, api.fluidCentrifuge().getRecipes().stream().toList());
		
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
		registration.addRecipeCatalyst(new ItemStack(BlocksZT_Processing.BASIC_FUEL_GENERATOR), RecipeTypes.FUELING);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT_Processing.BASIC_ELECTRIC_FURNACE), RecipeTypes.SMELTING);
		registration.addRecipeCatalyst(new ItemStack(ItemsZT_Processing.IRON_HAMMER), RecipeTypesZT.MANUAL_HAMMERING);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT_Processing.BASIC_MACHINE_ASSEMBLER), RecipeTypesZT.MACHINE_ASSEMBLY_BASIC);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT_Processing.BASIC_GRINDER), RecipeTypesZT.GRINDER_BASIC);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT_Processing.BASIC_SAWMILL), RecipeTypesZT.SAWMILL);
		registration.addRecipeCatalyst(new ItemStack(BlocksZT_Processing.FLUID_CENTRIFUGE), RecipeTypesZT.FLUID_CENTRIFUGE);
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration)
	{
		registration.addRecipeClickArea(GuiMachineAssemblerB.class, 107, 45, 22, 15, RecipeTypesZT.MACHINE_ASSEMBLY_BASIC);
		registration.addRecipeClickArea(GuiFuelGeneratorB.class, 81, 29, 13, 14, RecipeTypes.FUELING);
		registration.addRecipeClickArea(GuiElectricFurnaceB.class, 72, 35, 22, 15, RecipeTypes.SMELTING);
		registration.addRecipeClickArea(GuiGrinderB.class, 72, 35, 22, 15, RecipeTypesZT.GRINDER_BASIC);
		registration.addRecipeClickArea(GuiSawmillB.class, 61, 35, 22, 15, RecipeTypesZT.SAWMILL);
	}
	
	private static Stream<RepairData> getRepairData()
	{
		return Stream.of(
				new RepairData(RecipeHelper.fromTag(Tags.Items.INGOTS_IRON),
						new ItemStack(ItemsZT_Processing.IRON_HAMMER),
						new ItemStack(ItemsZT_Processing.WIRE_CUTTER),
						new ItemStack(ItemsZT_Processing.IRON_MINING_HEAD)),
				
				new RepairData(RecipeHelper.fromTag(Tags.Items.GEMS_DIAMOND),
						new ItemStack(ItemsZT_Processing.DIAMOND_MINING_HEAD)),
				
				new RepairData(RecipeHelper.fromTag(Tags.Items.INGOTS_NETHERITE),
						new ItemStack(ItemsZT_Processing.NETHERITE_MINING_HEAD))
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