package org.zeith.tech.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.annotations.ProvideRecipes;
import org.zeith.hammerlib.api.IRecipeProvider;
import org.zeith.hammerlib.core.adapter.recipe.SmeltingRecipeBuilder;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.enums.MachineTier;
import org.zeith.tech.api.recipes.*;
import org.zeith.tech.init.blocks.MachinesZT;
import org.zeith.tech.init.blocks.OresZT;
import org.zeith.tech.init.items.CraftingMaterialsZT;
import org.zeith.tech.init.items.ToolsZT;

import java.util.*;

@ProvideRecipes
public class RecipesZT
		implements IRecipeProvider
{
	static
	{
		HammerLib.EVENT_BUS.addGenericListener(RecipeMachineAssembler.class, RecipesZT::addMachineAssemblyRecipes);
		HammerLib.EVENT_BUS.addGenericListener(RecipeHammering.class, RecipesZT::addHammeringRecipes);
	}
	
	@Override
	public void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.RAW_MATERIALS_TIN).result(OresZT.RAW_TIN_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.RAW_MATERIALS_LEAD).result(OresZT.RAW_LEAD_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.RAW_MATERIALS_ALUMINUM).result(OresZT.RAW_ALUMINUM_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.INGOTS_TIN).result(OresZT.TIN_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.INGOTS_LEAD).result(OresZT.LEAD_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.INGOTS_ALUMINUM).result(OresZT.ALUMINUM_BLOCK).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_RAW_TIN).result(new ItemStack(OresZT.RAW_TIN, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_RAW_LEAD).result(new ItemStack(OresZT.RAW_LEAD, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_RAW_ALUMINUM).result(new ItemStack(OresZT.RAW_ALUMINUM, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_TIN).result(new ItemStack(OresZT.TIN_INGOT, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_LEAD).result(new ItemStack(OresZT.LEAD_INGOT, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_ALUMINUM).result(new ItemStack(OresZT.ALUMINUM_INGOT, 9)).register();
		
		event.shaped().shape("ll", "ll").map('l', BlocksZT.HEVEA_LOG).result(new ItemStack(BlocksZT.HEVEA_WOOD, 3)).register();
		event.shaped().shape("ll", "ll").map('l', BlocksZT.STRIPPED_HEVEA_LOG).result(new ItemStack(BlocksZT.STRIPPED_HEVEA_WOOD, 3)).register();
		event.shapeless().add(TagsZT.Items.HEVEA_LOGS).result(new ItemStack(BlocksZT.HEVEA_PLANKS, 4)).register();
		event.shapeless().add(BlocksZT.HEVEA_PLANKS).result(BlocksZT.HEVEA_BUTTON).register();
		event.shaped().shape("p  ", "pp ", "ppp").map('p', BlocksZT.HEVEA_PLANKS).result(new ItemStack(BlocksZT.HEVEA_STAIRS, 4)).register();
		event.shaped().shape("ppp").map('p', BlocksZT.HEVEA_PLANKS).result(new ItemStack(BlocksZT.HEVEA_SLAB, 6)).register();
		event.shaped().shape("sps", "sps").map('p', BlocksZT.HEVEA_PLANKS).map('s', Tags.Items.RODS_WOODEN).result(BlocksZT.HEVEA_FENCE_GATE).register();
		event.shaped().shape("psp", "psp").map('p', BlocksZT.HEVEA_PLANKS).map('s', Tags.Items.RODS_WOODEN).result(new ItemStack(BlocksZT.HEVEA_FENCE, 3)).register();
		event.shaped().shape("pp", "pp", "pp").map('p', BlocksZT.HEVEA_PLANKS).result(new ItemStack(BlocksZT.HEVEA_DOOR, 3)).register();
		event.shaped().shape("ppp", "ppp", " s ").map('p', BlocksZT.HEVEA_PLANKS).map('s', Tags.Items.RODS_WOODEN).result(new ItemStack(BlocksZT.HEVEA_SIGN, 3)).register();
		event.shaped().shape("pp").map('p', BlocksZT.HEVEA_PLANKS).result(BlocksZT.HEVEA_PRESSURE_PLATE).register();
		event.shaped().shape("ppp", "ppp").map('p', BlocksZT.HEVEA_PLANKS).result(new ItemStack(BlocksZT.HEVEA_TRAP_DOOR, 2)).register();
		event.shaped().shape("p p", "ppp").map('p', BlocksZT.HEVEA_PLANKS).result(ItemsZT.HEVEA_BOAT).register();
		event.shapeless().addAll(ItemsZT.HEVEA_BOAT, Tags.Items.CHESTS_WOODEN).result(ItemsZT.HEVEA_CHEST_BOAT).register();
		event.shaped().shape("ppp", "p p", "ppp").map('p', BlocksZT.HEVEA_PLANKS).result(BlocksZT.HEVEA_CHEST).register();
		event.shaped().shape("ppp", "p p", "ppp").map('p', TagsZT.Items.HEVEA_LOGS).result(new ItemStack(BlocksZT.HEVEA_CHEST, 4)).register();
		event.shapeless().addAll(BlocksZT.HEVEA_CHEST, Items.TRIPWIRE_HOOK).result(BlocksZT.HEVEA_TRAPPED_CHEST).register();
		
		event.smelting().xp(0.5F).input(ItemsZT.BOWL_OF_RUBBER).cookTime(100).result(ItemsZT.LATEX).register();
		
		event.shaped().shape("pgp").map('p', TagsZT.Items.PLATES_COPPER).map('g', Tags.Items.GLASS).result(new ItemStack(BlocksZT.COPPER_ITEM_PIPE, 3)).register();
		
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.RAW_MATERIALS_TIN).result(OresZT.TIN_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.RAW_MATERIALS_LEAD).result(OresZT.LEAD_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.RAW_MATERIALS_ALUMINUM).result(OresZT.ALUMINUM_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.ORES_TIN).result(OresZT.TIN_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.ORES_LEAD).result(OresZT.LEAD_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.ORES_ALUMINUM).result(OresZT.ALUMINUM_INGOT).register();
		
		event.shaped().shape("iin", "is ", " s ").map('i', Tags.Items.INGOTS_IRON).map('s', Tags.Items.RODS_WOODEN).map('n', Tags.Items.NUGGETS_IRON).result(ToolsZT.IRON_HAMMER).register();
		
		event.shaped()
				.shape("ici", "ici", "sss")
				.map('i', Tags.Items.STORAGE_BLOCKS_IRON)
				.map('c', Blocks.CRAFTING_TABLE)
				.map('s', Blocks.SMOOTH_STONE)
				.result(MachinesZT.MACHINE_ASSEMBLER_BASIC)
				.register();
	}
	
	public static void addMachineAssemblyRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeMachineAssembler> evt)
	{
		if(evt.is(RecipeRegistriesZT.MACHINE_ASSEBMLY))
		{
			var f = evt.<RecipeMachineAssembler.Builder> builderFactory();
			
			f.get()
					.minTier(MachineTier.BASIC)
					.shape("  g  ", " ici ", "gibig", " scs ", "  g  ")
					.map('i', Tags.Items.INGOTS_IRON)
					.map('c', CraftingMaterialsZT.COPPER_COIL)
					.map('b', Blocks.BLAST_FURNACE)
					.map('s', Tags.Items.STONE)
					.map('g', Tags.Items.INGOTS_COPPER)
					.result(MachinesZT.FUEL_GENERATOR_BASIC)
					.register();
		}
	}
	
	public static void addHammeringRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeHammering> evt)
	{
		if(evt.is(RecipeRegistriesZT.HAMMERING))
		{
			var f = evt.<RecipeHammering.Builder> builderFactory();
			
			var itemTags = evt.getContext().getAllTags(ForgeRegistries.Keys.ITEMS);
			
			Set<String> excludePlates = new HashSet<>();
			
			// TODO: HammerLib.EVENT_BUS event!
			excludePlates.add("steel");
			
			Map<String, Integer> materialHitOverride = new HashMap<>();
			materialHitOverride.put("gold", 3);
			materialHitOverride.put("aluminum", 2);
			
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
									.hitCount(materialHitOverride.getOrDefault(metalType, 4))
									.register();
						else
							ZeithTech.LOG.warn("Unable to find plate for metal " + metalType + ", but the plate tag (" + plateTag + ") is present...");
					}
				}
			}
		}
	}
	
	public static SmeltingRecipeBuilder smeltingAndBlasting(RegisterRecipesEvent event)
	{
		return new SmeltingRecipeBuilder(event)
		{
			private Recipe<?> generateBlastRecipe()
			{
				var id = getIdentifier();
				return new BlastingRecipe(new ResourceLocation(id.getNamespace(), id.getPath() + "/blasting"), group, input, result, xp, cookTime / 2);
			}
			
			@Override
			public void register()
			{
				super.register();
				var bl = generateBlastRecipe();
				event.register(bl.getId(), bl);
			}
		};
	}
}