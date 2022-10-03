package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.events.recipe.BasicHammeringRegistryEvent;
import org.zeith.tech.api.recipes.RecipeHammering;
import org.zeith.tech.api.recipes.RecipeMachineAssembler;
import org.zeith.tech.modules.shared.init.ItemsZT;
import org.zeith.tech.modules.shared.init.TagsZT;

import java.util.*;

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
}