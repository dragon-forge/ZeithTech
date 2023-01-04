package org.zeith.tech.modules.world.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.core.adapter.recipe.SmeltingRecipeBuilder;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.tech.modules.shared.init.ItemsZT;
import org.zeith.tech.modules.shared.init.TagsZT;

public interface RecipesZT_World
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("ll", "ll").map('l', BlocksZT_World.HEVEA_LOG).result(new ItemStack(BlocksZT_World.HEVEA_WOOD, 3)).register();
		event.shaped().shape("ll", "ll").map('l', BlocksZT_World.STRIPPED_HEVEA_LOG).result(new ItemStack(BlocksZT_World.STRIPPED_HEVEA_WOOD, 3)).register();
		event.shapeless().add(TagsZT.Items.HEVEA_LOGS).result(new ItemStack(BlocksZT_World.HEVEA_PLANKS, 4)).register();
		event.shapeless().add(BlocksZT_World.HEVEA_PLANKS).result(BlocksZT_World.HEVEA_BUTTON).register();
		event.shaped().shape("p  ", "pp ", "ppp").map('p', BlocksZT_World.HEVEA_PLANKS).result(new ItemStack(BlocksZT_World.HEVEA_STAIRS, 4)).register();
		event.shaped().shape("ppp").map('p', BlocksZT_World.HEVEA_PLANKS).result(new ItemStack(BlocksZT_World.HEVEA_SLAB, 6)).register();
		event.shaped().shape("sps", "sps").map('p', BlocksZT_World.HEVEA_PLANKS).map('s', Tags.Items.RODS_WOODEN).result(BlocksZT_World.HEVEA_FENCE_GATE).register();
		event.shaped().shape("psp", "psp").map('p', BlocksZT_World.HEVEA_PLANKS).map('s', Tags.Items.RODS_WOODEN).result(new ItemStack(BlocksZT_World.HEVEA_FENCE, 3)).register();
		event.shaped().shape("pp", "pp", "pp").map('p', BlocksZT_World.HEVEA_PLANKS).result(new ItemStack(BlocksZT_World.HEVEA_DOOR, 3)).register();
		event.shaped().shape("ppp", "ppp", " s ").map('p', BlocksZT_World.HEVEA_PLANKS).map('s', Tags.Items.RODS_WOODEN).result(new ItemStack(BlocksZT_World.HEVEA_SIGN, 3)).register();
		event.shaped().shape("pp").map('p', BlocksZT_World.HEVEA_PLANKS).result(BlocksZT_World.HEVEA_PRESSURE_PLATE).register();
		event.shaped().shape("ppp", "ppp").map('p', BlocksZT_World.HEVEA_PLANKS).result(new ItemStack(BlocksZT_World.HEVEA_TRAP_DOOR, 2)).register();
		event.shaped().shape("p p", "ppp").map('p', BlocksZT_World.HEVEA_PLANKS).result(ItemsZT_World.HEVEA_BOAT).register();
		event.shapeless().addAll(ItemsZT_World.HEVEA_BOAT, Tags.Items.CHESTS_WOODEN).result(ItemsZT_World.HEVEA_CHEST_BOAT).register();
		event.shaped().shape("ppp", "p p", "ppp").map('p', BlocksZT_World.HEVEA_PLANKS).result(BlocksZT_World.HEVEA_CHEST).register();
		event.shaped().shape("ppp", "p p", "ppp").map('p', TagsZT.Items.HEVEA_LOGS).result(new ItemStack(BlocksZT_World.HEVEA_CHEST, 4)).register();
		event.shapeless().addAll(BlocksZT_World.HEVEA_CHEST, Items.TRIPWIRE_HOOK).result(BlocksZT_World.HEVEA_TRAPPED_CHEST).register();
		event.shaped().shape("s s", "sps").map('p', ItemTags.PLANKS).map('s', Tags.Items.RODS_WOODEN).result(ItemsZT.TREE_TAP).register();
		
		event.smelting().xp(0.5F).input(ItemsZT.BOWL_OF_RESIN).cookTime(100).result(new ItemStack(ItemsZT.LATEX, 3)).register();
		
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.RAW_MATERIALS_TIN).result(BlocksZT_World.RAW_TIN_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.RAW_MATERIALS_LEAD).result(BlocksZT_World.RAW_LEAD_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.RAW_MATERIALS_ALUMINUM).result(BlocksZT_World.RAW_ALUMINUM_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.RAW_MATERIALS_SILVER).result(BlocksZT_World.RAW_SILVER_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.RAW_MATERIALS_ZINC).result(BlocksZT_World.RAW_ZINC_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.RAW_MATERIALS_TUNGSTEN).result(BlocksZT_World.RAW_TUNGSTEN_BLOCK).register();
		
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.INGOTS_TIN).result(BlocksZT_World.TIN_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.INGOTS_LEAD).result(BlocksZT_World.LEAD_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.INGOTS_ALUMINUM).result(BlocksZT_World.ALUMINUM_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.INGOTS_SILVER).result(BlocksZT_World.SILVER_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.INGOTS_ZINC).result(BlocksZT_World.ZINC_BLOCK).register();
		event.shaped().shape("rrr", "rrr", "rrr").map('r', TagsZT.Items.INGOTS_TUNGSTEN).result(BlocksZT_World.TUNGSTEN_BLOCK).register();
		
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_RAW_TIN).result(new ItemStack(ItemsZT_World.RAW_TIN, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_RAW_LEAD).result(new ItemStack(ItemsZT_World.RAW_LEAD, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_RAW_ALUMINUM).result(new ItemStack(ItemsZT_World.RAW_ALUMINUM, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_RAW_SILVER).result(new ItemStack(ItemsZT_World.RAW_SILVER, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_RAW_ZINC).result(new ItemStack(ItemsZT_World.RAW_ZINC, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_RAW_TUNGSTEN).result(new ItemStack(ItemsZT_World.RAW_TUNGSTEN, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_TIN).result(new ItemStack(ItemsZT_World.TIN_INGOT, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_LEAD).result(new ItemStack(ItemsZT_World.LEAD_INGOT, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_ALUMINUM).result(new ItemStack(ItemsZT_World.ALUMINUM_INGOT, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_ZINC).result(new ItemStack(ItemsZT_World.ZINC_INGOT, 9)).register();
		event.shapeless().add(TagsZT.Items.STORAGE_BLOCKS_TUNGSTEN).result(new ItemStack(ItemsZT_World.TUNGSTEN_INGOT, 9)).register();
		
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.RAW_MATERIALS_TIN).result(ItemsZT_World.TIN_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.RAW_MATERIALS_LEAD).result(ItemsZT_World.LEAD_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.RAW_MATERIALS_ALUMINUM).result(ItemsZT_World.ALUMINUM_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.RAW_MATERIALS_SILVER).result(ItemsZT_World.SILVER_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.RAW_MATERIALS_ZINC).result(ItemsZT_World.ZINC_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.ORES_TIN).result(ItemsZT_World.TIN_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.ORES_LEAD).result(ItemsZT_World.LEAD_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.ORES_ALUMINUM).result(ItemsZT_World.ALUMINUM_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.ORES_SILVER).result(ItemsZT_World.SILVER_INGOT).register();
		smeltingAndBlasting(event).xp(0.5F).input(TagsZT.Items.ORES_ZINC).result(ItemsZT_World.ZINC_INGOT).register();
	}
	
	static SmeltingRecipeBuilder smeltingAndBlasting(RegisterRecipesEvent event)
	{
		return new SmeltingRecipeBuilder(event)
		{
			private Recipe<?> generateBlastRecipe()
			{
				var id = getIdentifier();
				return new BlastingRecipe(new ResourceLocation(id.getNamespace(), id.getPath() + "/blasting"), group, CookingBookCategory.BLOCKS, input, result, xp, cookTime / 2);
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