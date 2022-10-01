package org.zeith.tech.modules.processing.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.events.recipe.BasicHammeringRegistryEvent;
import org.zeith.tech.api.recipes.RecipeHammering;
import org.zeith.tech.api.recipes.RecipeMachineAssembler;
import org.zeith.tech.modules.shared.init.ItemsZT;

import java.util.*;

public interface RecipesZT_Processing
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("iin", "is ", " s ").map('i', Tags.Items.INGOTS_IRON).map('s', Tags.Items.RODS_WOODEN).map('n', Tags.Items.NUGGETS_IRON).result(ItemsZT_Processing.IRON_HAMMER).register();
		
		event.shaped()
				.shape("ici", "ici", "sss")
				.map('i', Tags.Items.STORAGE_BLOCKS_IRON)
				.map('c', Blocks.CRAFTING_TABLE)
				.map('s', Blocks.SMOOTH_STONE)
				.result(BlocksZT_Processing.MACHINE_ASSEMBLER_BASIC)
				.register();
	}
	
	static void addMachineAssemblyRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeMachineAssembler> evt)
	{
		if(evt.is(RecipeRegistriesZT_Processing.MACHINE_ASSEBMLY))
		{
			var f = evt.<RecipeMachineAssembler.Builder> builderFactory();
			
			f.get()
					.minTier(TechTier.BASIC)
					.shape("  g  ", " ici ", "gibig", " scs ", "  g  ")
					.map('i', Tags.Items.INGOTS_IRON)
					.map('c', ItemsZT.COPPER_COIL)
					.map('b', Blocks.BLAST_FURNACE)
					.map('s', Tags.Items.STONE)
					.map('g', Tags.Items.INGOTS_COPPER)
					.result(BlocksZT_Processing.FUEL_GENERATOR_BASIC)
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