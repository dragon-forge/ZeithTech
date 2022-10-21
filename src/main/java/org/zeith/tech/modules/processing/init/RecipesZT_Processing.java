package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredientStack;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.events.recipe.BasicHammeringRegistryEvent;
import org.zeith.tech.api.events.recipe.GrindingRegistryEvent;
import org.zeith.tech.api.item.ItemComparators;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.init.*;
import org.zeith.tech.utils.RecipeManagerHelper;

import java.util.*;
import java.util.function.Supplier;

public interface RecipesZT_Processing
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("iin", "is ", " s ").map('i', Tags.Items.INGOTS_IRON).map('s', Tags.Items.RODS_WOODEN).map('n', Tags.Items.NUGGETS_IRON).result(ItemsZT_Processing.IRON_HAMMER).register();
		event.shaped().shape("i i", " i ", "s s").map('i', Tags.Items.INGOTS_IRON).map('s', Tags.Items.RODS_WOODEN).result(ItemsZT_Processing.WIRE_CUTTER).register();
		
		event.shaped().shape("fpf", "ptp", "fpf").map('f', Items.FLINT).map('p', TagsZT.Items.PLATES_IRON).map('t', BlocksZT.MINING_PIPE).result(ItemsZT_Processing.MINING_HEAD).register();
		
		event.shaped().shape(" p ", "ptp", " p ").map('p', Tags.Items.INGOTS_IRON).map('t', ItemsZT.MINING_HEAD).result(ItemsZT_Processing.IRON_MINING_HEAD).register();
		event.shaped().shape(" p ", "ptp", " p ").map('p', Tags.Items.GEMS_DIAMOND).map('t', ItemsZT.MINING_HEAD).result(ItemsZT_Processing.DIAMOND_MINING_HEAD).register();
		
		event.shaped().shape("prp", "pcp", " p ").map('p', ItemsZT.PLASTIC).map('r', Tags.Items.DUSTS_REDSTONE).map('c', Items.COMPARATOR).result(ItemsZT_Processing.REDSTONE_CONTROL_TOOL).register();
		
		var netheriteMiningHeadId = ForgeRegistries.ITEMS.getKey(ItemsZT_Processing.NETHERITE_MINING_HEAD);
		event.register(netheriteMiningHeadId, new UpgradeRecipe(netheriteMiningHeadId, Ingredient.of(ItemsZT.DIAMOND_MINING_HEAD), RecipeHelper.fromTag(Tags.Items.INGOTS_NETHERITE), new ItemStack(ItemsZT_Processing.NETHERITE_MINING_HEAD)));
		
		event.shaped().shape("p p", "i i", "p p").map('p', TagsZT.Items.PLATES_IRON).map('i', Tags.Items.INGOTS_IRON).result(new ItemStack(BlocksZT_Processing.MINING_PIPE, 6)).register();
		
		event.shaped()
				.shape("ppp", "ici", "sss")
				.map('p', TagsZT.Items.PLATES_IRON)
				.map('i', Tags.Items.STORAGE_BLOCKS_IRON)
				.map('c', Blocks.CRAFTING_TABLE)
				.map('s', Blocks.SMOOTH_STONE)
				.result(BlocksZT_Processing.BASIC_MACHINE_ASSEMBLER)
				.register();
	}
	
	static void addMachineAssemblyRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeMachineAssembler> evt)
	{
		if(!evt.is(RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY)) return;
		
		var f = evt.<RecipeMachineAssembler.Builder> builderFactory();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  c  ", " sps ", "bgfgb", " sps ", "  c  ")
				.map('c', Tags.Items.INGOTS_COPPER)
				.map('s', Tags.Items.STONE)
				.map('p', ItemsZT.COPPER_COIL)
				.map('b', Tags.Items.STORAGE_BLOCKS_IRON)
				.map('g', Tags.Items.GLASS)
				.map('f', Items.FURNACE)
				.result(BlocksZT_Processing.BASIC_ELECTRIC_FURNACE)
				.register();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  c  ", " gpg ", "cmdmc", " php ", "  c  ")
				.map('c', Tags.Items.INGOTS_COPPER)
				.map('p', TagsZT.Items.PLATES_IRON)
				.map('m', ItemsZT.MOTOR)
				.map('g', TagsZT.Items.GEARS_STONE)
				.map('h', Tags.Items.CHESTS)
				.map('d', ItemsZT_Processing.IRON_MINING_HEAD)
				.result(BlocksZT_Processing.BASIC_GRINDER)
				.register();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  c  ", " pdp ", "cpmpc", " php ", "  c  ")
				.map('c', Tags.Items.INGOTS_COPPER)
				.map('p', TagsZT.Items.PLATES_IRON)
				.map('m', ItemsZT.MOTOR)
				.map('h', Tags.Items.CHESTS)
				.map('d', ItemsZT.CIRCULAR_SAW)
				.result(BlocksZT_Processing.BASIC_SAWMILL)
				.register();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  h  ", " ccc ", "iCwCi", " mgm ", "  p  ")
				.map('h', Items.HOPPER)
				.map('c', TagsZT.Items.PLATES_COPPER)
				.map('i', Tags.Items.INGOTS_IRON)
				.map('C', ItemsZT.COPPER_COIL)
				.map('w', Tags.Items.CHESTS)
				.map('m', ItemsZT.MOTOR)
				.map('g', TagsZT.Items.GEARS_DIAMOND)
				.map('p', BlocksZT.MINING_PIPE)
				.result(BlocksZT_Processing.BASIC_QUARRY)
				.register();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  i  ", " igi ", "iCGCi", " pmp ", "  r  ")
				.map('i', TagsZT.Items.PLATES_IRON)
				.map('r', Tags.Items.INGOTS_IRON)
				.map('g', Tags.Items.GLASS)
				.map('C', ItemsZT.COPPER_COIL)
				.map('G', TagsZT.Items.GEARS_GOLD)
				.map('p', BlocksZT.IRON_FLUID_PIPE)
				.map('m', ItemsZT.MOTOR)
				.result(BlocksZT_Processing.FLUID_CENTRIFUGE)
				.register();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  p  ", " gmg ", "actca", " lMl ", "  M  ")
				.map('p', BlocksZT.IRON_FLUID_PIPE)
				.map('g', TagsZT.Items.GEARS_IRON)
				.map('a', TagsZT.Items.INGOTS_ALUMINUM)
				.map('m', ItemsZT.MOTOR)
				.map('c', ItemsZT.COPPER_COIL)
				.map('t', BlocksZT.BASIC_FLUID_TANK)
				.map('l', ItemsZT.LATEX)
				.map('M', BlocksZT.MINING_PIPE)
				.result(BlocksZT_Processing.FLUID_PUMP)
				.register();
		
		
		f.get().minTier(TechTier.BASIC)
				.shape("  g  ", " aca ", "icsci", " aca ", "  b  ")
				.map('g', Tags.Items.GLASS)
				.map('i', TagsZT.Items.GEARS_SILVER)
				.map('a', BlocksZT.BASIC_FLUID_TANK)
				.map('c', ItemsZT.COPPER_COIL)
				.map('s', TagsZT.Items.STORAGE_BLOCKS_SILVER)
				.map('b', Tags.Items.STORAGE_BLOCKS_IRON)
				.result(BlocksZT_Processing.WASTE_PROCESSOR)
				.register();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  c  ", " mbm ", "gsasg", " mbm ", "  c  ")
				.map('c', Items.CHAIN)
				.map('m', ItemsZT.MOTOR)
				.map('b', Tags.Items.STORAGE_BLOCKS_IRON)
				.map('s', TagsZT.Items.INGOTS_SILVER)
				.map('g', TagsZT.Items.GEARS_IRON)
				.map('a', Items.ANVIL)
				.result(BlocksZT_Processing.METAL_PRESS)
				.register();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  p  ", " lcl ", "pmgmp", " lsl ", "  p  ")
				.map('p', TagsZT.Items.PLATES_GOLD)
				.map('l', ItemTags.LOGS)
				.map('g', TagsZT.Items.GEARS_GOLD)
				.map('m', ItemsZT.MOTOR)
				.map('c', ItemsZT.CIRCULAR_SAW)
				.map('s', TagsZT.Items.STORAGE_BLOCKS_SILVER)
				.result(BlocksZT_Processing.FACADE_SLICER)
				.register();
		
	}
	
	static void addHammeringRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeHammering> evt)
	{
		if(!evt.is(RecipeRegistriesZT_Processing.HAMMERING)) return;
		
		var f = evt.<RecipeHammering.Builder> builderFactory();
		
		var itemTags = evt.getContext().getAllTags(ForgeRegistries.Keys.ITEMS);
		
		Set<String> excludePlates = new HashSet<>();
		Map<String, Integer> materialHitOverride = new HashMap<>();
		Map<String, TechTier> minTierOverride = new HashMap<>();
		
		minTierOverride.put("steel", TechTier.ADVANCED);
		minTierOverride.put("tungsten", TechTier.ADVANCED);
		materialHitOverride.put("gold", 3);
		materialHitOverride.put("aluminum", 2);
		materialHitOverride.put("tungsten", 10);
		
		var hevt = new BasicHammeringRegistryEvent(excludePlates, materialHitOverride, minTierOverride);
		MinecraftForge.EVENT_BUS.post(hevt);
		
		for(var tag : itemTags.keySet())
		{
			if(tag.getNamespace().equals("forge") && tag.getPath().startsWith("ingots/"))
			{
				var metalType = tag.getPath().substring(7);
				var plateTag = new ResourceLocation("forge", "plates/" + metalType);
				
				if(itemTags.containsKey(plateTag) && !excludePlates.contains(metalType))
				{
					var plateItem = itemTags.get(plateTag)
							.stream()
							.max(ItemComparators.PREFER_ZEITHTECH_ITEM_HOLDERS)
							.orElse(null);
					if(plateItem != null)
						f.get()
								.input(ItemTags.create(tag))
								.result(plateItem.get())
								.hitCount(hevt.getHitsForMetal(metalType))
								.withTier(hevt.getMaterialTier(metalType))
								.register();
					else
						ZeithTech.LOG.warn("Unable to find plate for metal " + metalType + ", but the plate tag (" + plateTag + ") is present...");
				}
			}
		}
	}
	
	static void addGrindingRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeGrinding> evt)
	{
		if(!evt.is(RecipeRegistriesZT_Processing.GRINDING)) return;
		
		Supplier<RecipeGrinding.GrindingRecipeBuilder> f = () -> new RecipeGrinding.GrindingRecipeBuilder(evt);
		
		f.get().input(Items.STONE).result(Items.COBBLESTONE).register();
		f.get().input(Items.DEEPSLATE).result(Items.COBBLED_DEEPSLATE).register();
		f.get().input(Tags.Items.COBBLESTONE).result(Items.GRAVEL).register();
		f.get().input(Tags.Items.GRAVEL).result(Items.SAND).craftTime(100).register();
		f.get().input(Items.AMETHYST_BLOCK).result(new ItemStack(Items.AMETHYST_SHARD, 4)).craftTime(80).register();
		f.get().input(Items.GLOWSTONE).result(new ItemStack(Items.GLOWSTONE_DUST, 4)).craftTime(40).register();
		f.get().extraOutput(new ExtraOutput(new ItemStack(ItemsZT.BIOLUMINESCENT_DUST), 0.5F)).input(Items.GLOW_BERRIES).result(Items.STICK).craftTime(50).register();
		f.get().input(Items.COAL).result(ItemsZT.COAL_DUST).craftTime(20).register();
		
		f.get().extraOutput(new ExtraOutput.Ranged(new ItemStack(Items.BONE_MEAL), 1, 3, 0.85F)).input(Items.BONE).result(new ItemStack(Items.BONE_MEAL, 3)).craftTime(50).register();
		
		Set<String> excludeDusts = new HashSet<>();
		Map<String, Integer> materialHitOverride = new HashMap<>();
		Map<String, TechTier> minTierOverride = new HashMap<>();
		
		var hevt = new GrindingRegistryEvent(excludeDusts, materialHitOverride, minTierOverride);
		MinecraftForge.EVENT_BUS.post(hevt);
		
		var itemTags = evt.getContext().getAllTags(ForgeRegistries.Keys.ITEMS);
		
		for(var tag : itemTags.keySet())
		{
			String grindType = null;
			
			if(tag.getNamespace().equals("forge") && tag.getPath().startsWith("ingots/"))
				grindType = tag.getPath().substring(7);
			
			if(tag.getNamespace().equals("forge") && tag.getPath().startsWith("gems/"))
				grindType = tag.getPath().substring(5);
			
			if(grindType != null)
			{
				var dustTag = new ResourceLocation("forge", "dusts/" + grindType);
				
				if(itemTags.containsKey(dustTag) && !excludeDusts.contains(grindType))
				{
					var dustItem = itemTags.get(dustTag)
							.stream()
							.max(ItemComparators.PREFER_ZEITHTECH_ITEM_HOLDERS)
							.orElse(null);
					if(dustItem != null)
						f.get()
								.input(ItemTags.create(tag))
								.result(dustItem.value())
								.craftTime(hevt.getTimeForMaterial(grindType))
								.tier(hevt.getMaterialTier(grindType))
								.register();
					else
						ZeithTech.LOG.warn("Unable to find dust for material " + grindType + ", but the dust tag (" + dustTag + ") is present...");
				}
			}
		}
	}
	
	static void addSawmillRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeSawmill> evt)
	{
		if(!evt.is(RecipeRegistriesZT_Processing.SAWMILL)) return;
		
		Supplier<RecipeSawmill.SawmillRecipeBuilder> f = () -> new RecipeSawmill.SawmillRecipeBuilder(evt);
		
		var logs = evt.getContext().getTag(ItemTags.LOGS);
		var planks = evt.getContext().getTag(ItemTags.PLANKS);
		
		final ExtraOutput sawdust = new ExtraOutput.Ranged(new ItemStack(ItemsZT.SAWDUST), 1, 2, 0.75F);
		
		RecipeManagerHelper.getRecipeManager(evt)
				.stream()
				.flatMap(r -> r.getAllRecipesFor(RecipeType.CRAFTING).stream())
				.filter(recipe -> !recipe.isSpecial() && recipe.getResultItem().is(planks::contains))
				.forEach(recipe ->
				{
					var input = recipe.getIngredients();
					if(input.size() == 1)
					{
						var main = input.get(0);
						if(Arrays.stream(main.getItems()).anyMatch(out -> out.is(logs::contains)))
						{
							var plankStack = recipe.getResultItem().copy();
							plankStack.setCount(plankStack.getCount() * 3 / 2);
							
							f.get().extraOutput(sawdust)
									.tier(TechTier.BASIC)
									.input(main)
									.result(plankStack)
									.craftTime(100)
									.register();
						}
					}
				});
	}
	
	static void addFluidCentrifugeRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeFluidCentrifuge> evt)
	{
		if(!evt.is(RecipeRegistriesZT_Processing.FLUID_CENTRIFUGE)) return;
		
		var f = evt.<RecipeFluidCentrifuge.FluidCentrifugeRecipeBuilder> builderFactory();
		
		f.get()
				.input(new FluidIngredientStack(FluidIngredient.ofTags(List.of(TagsZT.Fluids.CRUDE_OIL)), 1000))
				.energy(5000)
				.result(FluidsZT_Processing.REFINED_OIL.stack(700))
				.extraOutput(new ExtraOutput.Ranged(new ItemStack(ItemsZT.OIL_SLUDGE), 1, 3, 0.75F))
				.register();
	}
	
	static void addWasteProcessingRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeWasteProcessor> evt)
	{
		if(!evt.is(RecipeRegistriesZT_Processing.WASTE_PROCESSING)) return;
		
		var f = evt.<RecipeWasteProcessor.WasteProcessorRecipeBuilder> builderFactory();
		
		var water = FluidIngredient.ofTags(List.of(FluidTags.WATER));
		
		f.get()
				.input(
						FluidsZT_Processing.REFINED_OIL.ingredient(800),
						new FluidIngredientStack(water, 1000)
				)
				.time(20 * 20)
				.result(
						FluidsZT_Processing.DIESEL_FUEL.stack(300),
						FluidsZT_Processing.GAS.stack(400)
				)
				.byproduct(new ExtraOutput(new ItemStack(BlocksZT.MASUT), 1F))
				.register();
		
		f.get()
				.input(
						FluidsZT_Processing.REFINED_OIL.ingredient(1000)
				)
				.time(40 * 20)
				.result(
						FluidsZT_Processing.GAS.stack(850)
				)
				.register();
		
		f.get()
				.input(Ingredient.of(ItemsZT.OIL_SLUDGE))
				.time(30 * 20)
				.result(
						FluidsZT_Processing.REFINED_OIL.stack(35),
						new FluidStack(Fluids.WATER, 50)
				)
				.byproduct(
						new ExtraOutput(new ItemStack(Items.SAND), 1F),
						new ExtraOutput(new ItemStack(Items.DIRT), 1F),
						new ExtraOutput(new ItemStack(Items.GRAVEL), 1F)
				)
				.register();
		
		f.get()
				.input(FluidsZT_Processing.GAS.ingredient(1000))
				.input(RecipeHelper.fromTag(TagsZT.Items.DUSTS_COAL))
				.time(6 * 20)
				.byproduct(
						new ExtraOutput.Ranged(new ItemStack(ItemsZT.PLASTIC), 2, 4, 1F)
				)
				.register();
		
		f.get()
				.input(
						FluidsZT_Processing.GAS.ingredient(3000),
						new FluidIngredientStack(water, 3000)
				)
				.input(RecipeHelper.fromTag(TagsZT.Items.DUSTS_IRON))
				.time(30 * 20)
				.result(
						FluidsZT_Processing.SULFURIC_ACID.stack(1000)
				)
				.register();
	}
}