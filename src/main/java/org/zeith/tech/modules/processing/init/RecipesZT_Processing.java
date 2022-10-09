package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
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
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.base.RecipeUnaryBase;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.init.ItemsZT;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;
import org.zeith.tech.utils.RecipeManagerHelper;

import java.util.*;
import java.util.function.Supplier;

public interface RecipesZT_Processing
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("iin", "is ", " s ").map('i', Tags.Items.INGOTS_IRON).map('s', Tags.Items.RODS_WOODEN).map('n', Tags.Items.NUGGETS_IRON).result(ItemsZT_Processing.IRON_HAMMER).register();
		event.shaped().shape("i i", " i ", "s s").map('i', Tags.Items.INGOTS_IRON).map('s', Tags.Items.RODS_WOODEN).result(ItemsZT_Processing.WIRE_CUTTER).register();
		
		event.shaped().shape(" p ", "ptp", " p ").map('p', TagsZT.Items.PLATES_IRON).map('t', BlocksZT_Processing.MINING_PIPE).result(ItemsZT_Processing.MINING_HEAD).register();
		
		event.shaped().shape(" p ", "ptp", " p ").map('p', Tags.Items.INGOTS_IRON).map('t', ItemsZT_Processing.MINING_HEAD).result(ItemsZT_Processing.IRON_MINING_HEAD).register();
		event.shaped().shape(" p ", "ptp", " p ").map('p', Tags.Items.GEMS_DIAMOND).map('t', ItemsZT_Processing.MINING_HEAD).result(ItemsZT_Processing.DIAMOND_MINING_HEAD).register();
		event.register(ForgeRegistries.ITEMS.getKey(ItemsZT_Processing.NETHERITE_MINING_HEAD), new UpgradeRecipe(ForgeRegistries.ITEMS.getKey(ItemsZT_Processing.NETHERITE_MINING_HEAD), Ingredient.of(ItemsZT_Processing.DIAMOND_MINING_HEAD), RecipeHelper.fromTag(Tags.Items.INGOTS_NETHERITE), new ItemStack(ItemsZT_Processing.NETHERITE_MINING_HEAD)));
		
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
		if(evt.is(RecipeRegistriesZT_Processing.MACHINE_ASSEBMLY))
		{
			var f = evt.<RecipeMachineAssembler.Builder> builderFactory();
			
			f.get().minTier(TechTier.BASIC)
					.shape("  g  ", " ici ", "gibig", " scs ", "  g  ")
					.map('i', Tags.Items.INGOTS_IRON)
					.map('c', ItemsZT.COPPER_COIL)
					.map('b', Blocks.BLAST_FURNACE)
					.map('s', Tags.Items.STONE)
					.map('g', Tags.Items.INGOTS_COPPER)
					.result(BlocksZT_Processing.BASIC_FUEL_GENERATOR)
					.register();
			
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
					.shape("  c  ", " ppp ", "cmdmc", " php ", "  c  ")
					.map('c', Tags.Items.INGOTS_COPPER)
					.map('p', TagsZT.Items.PLATES_IRON)
					.map('m', ItemsZT.MOTOR)
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
					.shape("  h  ", " ccc ", "iCwCi", " mpm ", "  p  ")
					.map('h', Items.HOPPER)
					.map('c', TagsZT.Items.PLATES_COPPER)
					.map('i', Tags.Items.INGOTS_IRON)
					.map('C', ItemsZT.COPPER_COIL)
					.map('w', Tags.Items.CHESTS)
					.map('m', ItemsZT.MOTOR)
					.map('p', BlocksZT_Processing.MINING_PIPE)
					.result(BlocksZT_Processing.BASIC_QUARRY)
					.register();
			
			f.get().minTier(TechTier.BASIC)
					.shape("  i  ", " igi ", "iCgCi", " pmp ", "  r  ")
					.map('i', TagsZT.Items.PLATES_IRON)
					.map('r', Tags.Items.INGOTS_IRON)
					.map('g', Tags.Items.GLASS)
					.map('C', ItemsZT.COPPER_COIL)
					.map('p', BlocksZT_Transport.IRON_FLUID_PIPE)
					.map('m', ItemsZT.MOTOR)
					.result(BlocksZT_Processing.FLUID_CENTRIFUGE)
					.register();
			
			f.get().minTier(TechTier.BASIC)
					.shape("  p  ", " ama ", "actca", " lMl ", "  M  ")
					.map('p', BlocksZT_Transport.IRON_FLUID_PIPE)
					.map('a', TagsZT.Items.INGOTS_ALUMINUM)
					.map('m', ItemsZT.MOTOR)
					.map('c', ItemsZT.COPPER_COIL)
					.map('t', BlocksZT_Transport.BASIC_FLUID_TANK)
					.map('l', ItemsZT.LATEX)
					.map('M', BlocksZT_Processing.MINING_PIPE)
					.result(BlocksZT_Processing.FLUID_PUMP)
					.register();
			
			f.get().minTier(TechTier.BASIC)
					.shape("  o  ", " lll ", "cpgtc", " iii ", "  o  ")
					.map('o', Tags.Items.INGOTS_COPPER)
					.map('l', TagsZT.Items.INGOTS_LEAD)
					.map('c', ItemsZT.COPPER_COIL)
					.map('p', TagsZT.Items.PLATES_ALUMINUM)
					.map('g', BlocksZT_Processing.BASIC_FUEL_GENERATOR)
					.map('t', BlocksZT_Transport.BASIC_FLUID_TANK)
					.map('i', Tags.Items.INGOTS_IRON)
					.result(BlocksZT_Processing.BASIC_LIQUID_FUEL_GENERATOR)
					.register();
			
		}
	}
	
	static void addHammeringRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeHammering> evt)
	{
		if(evt.is(RecipeRegistriesZT_Processing.HAMMERING))
		{
			var f = evt.<RecipeHammering.Builder> builderFactory();
			
			var itemTags = evt.getContext().getAllTags(ForgeRegistries.Keys.ITEMS);
			
			Set<String> excludePlates = new HashSet<>();
			Map<String, Integer> materialHitOverride = new HashMap<>();
			Map<String, TechTier> minTierOverride = new HashMap<>();
			
			excludePlates.add("tungsten");
			minTierOverride.put("steel", TechTier.ADVANCED);
			materialHitOverride.put("gold", 3);
			materialHitOverride.put("aluminum", 2);
			
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
						var plateItem = itemTags.get(plateTag).stream().findFirst().orElse(null);
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
	}
	
	static void addGrindingRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeGrinding> evt)
	{
		if(evt.is(RecipeRegistriesZT_Processing.GRINDING))
		{
			var f = evt.<RecipeUnaryBase.Builder<RecipeGrinding>> builderFactory();
			
			f.get().input(Items.STONE).result(Items.COBBLESTONE).register();
			f.get().input(Items.DEEPSLATE).result(Items.COBBLED_DEEPSLATE).register();
			f.get().input(Tags.Items.COBBLESTONE).result(Items.GRAVEL).register();
			f.get().input(Tags.Items.GRAVEL).result(Items.SAND).craftTime(100).register();
			f.get().input(Items.AMETHYST_BLOCK).result(new ItemStack(Items.AMETHYST_SHARD, 4)).craftTime(80).register();
			
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
						var dustItem = itemTags.get(dustTag).stream().findFirst().orElse(null);
						if(dustItem != null)
							f.get()
									.input(ItemTags.create(tag))
									.result(dustItem.get())
									.craftTime(hevt.getTimeForMaterial(grindType))
									.tier(hevt.getMaterialTier(grindType))
									.register();
						else
							ZeithTech.LOG.warn("Unable to find dust for material " + grindType + ", but the dust tag (" + dustTag + ") is present...");
					}
				}
			}
		}
	}
	
	static void addSawmillRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeSawmill> evt)
	{
		if(evt.is(RecipeRegistriesZT_Processing.SAWMILL))
		{
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
	}
	
	static void addFluidCentrifugeRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeFluidCentrifuge> evt)
	{
		if(evt.is(RecipeRegistriesZT_Processing.FLUID_CENTRIFUGE))
		{
			var f = evt.<RecipeFluidCentrifuge.FluidCentrifugeRecipeBuilder> builderFactory();
			
			f.get()
					.input(new FluidIngredientStack(FluidIngredient.ofTags(List.of(TagsZT.Fluids.CRUDE_OIL)), 1000))
					.energy(5000)
					.result(new FluidStack(FluidsZT_Processing.REFINED_OIL.getSource(), 700))
					.extraOutput(new ExtraOutput.Ranged(new ItemStack(ItemsZT.OIL_SLUDGE), 1, 3, 0.75F))
					.register();
		}
	}
}