package org.zeith.tech.modules.shared.init;

import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredientStack;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.api.recipes.processing.RecipeWasteProcessor;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;

import java.util.List;
import java.util.stream.Stream;

public interface RecipesZT
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("dd", "dd").map('d', ItemsZT.BIOLUMINESCENT_DUST).result(BlocksZT.BIOLUMINESCENT_BLOCK).register();
		event.shaped().shape("dd", "dd").map('d', ItemsZT.PLASTIC).result(BlocksZT.PLASTIC_CASING).register();
		
		event.shaped().shape("nnn", "npn", "nnn").map('n', Tags.Items.NUGGETS_IRON).map('p', ItemTags.PLANKS).result(BlocksZT.REINFORCED_PLANKS).register();
		
		event.shaped().shape("wsw", "wsw", "wsw").map('w', BlocksZT_Transport.UNINSULATED_COPPER_WIRE).map('s', Tags.Items.RODS_WOODEN).result(ItemsZT.COPPER_COIL).register();
		event.shaped().shape("wsw", "wsw", "wsw").map('w', BlocksZT_Transport.UNINSULATED_GOLD_WIRE).map('s', Tags.Items.RODS_WOODEN).result(ItemsZT.GOLD_COIL).register();
		event.shaped().shape("icc", "iii", "icc").map('i', Tags.Items.INGOTS_IRON).map('c', ItemsZT.COPPER_COIL).result(ItemsZT.MOTOR).register();
		event.shaped().shape("ppp", "pgp", "ppp").map('p', TagsZT.Items.PLATES_IRON).map('g', TagsZT.Items.GEARS_STONE).result(ItemsZT.CIRCULAR_SAW).register();
		
		event.shaped().shape("ppp", "psp", "ddd").map('p', Tags.Items.INGOTS_IRON).map('s', ItemsZT.CIRCULAR_SAW).map('d', TagsZT.Items.PLATES_IRON).result(ItemsZT.IRON_ELECTRIC_SAW).register();
		event.shaped().shape("ppp", "psp", "ddd").map('p', Tags.Items.INGOTS_IRON).map('s', ItemsZT.CIRCULAR_SAW).map('d', Tags.Items.GEMS_DIAMOND).result(ItemsZT.DIAMOND_ELECTRIC_SAW).register();
		event.shaped().shape("ppp", "psp", "ddd").map('p', Tags.Items.INGOTS_IRON).map('s', ItemsZT.CIRCULAR_SAW).map('d', TagsZT.Items.PLATES_TUNGSTEN).result(ItemsZT.TUNGSTEN_ELECTRIC_SAW).register();
		
		event.shaped().shape("pgm", "pgc", "pgm").map('p', TagsZT.Items.PLATES_IRON).map('g', TagsZT.Items.GEARS_IRON).map('m', ItemsZT.MOTOR).map('c', TagsZT.Items.COILS_GOLD).result(ItemsZT.MULTI_TOOL_IRON_MOTOR).register();
		event.shaped().shape("pgm", "pgc", "pgm").map('p', Tags.Items.GEMS_DIAMOND).map('g', TagsZT.Items.GEARS_DIAMOND).map('m', ItemsZT.MOTOR).map('c', TagsZT.Items.COILS_GOLD).result(ItemsZT.MULTI_TOOL_DIAMOND_MOTOR).register();
		event.shaped().shape("pgm", "pgc", "pgm").map('p', TagsZT.Items.PLATES_TUNGSTEN).map('g', TagsZT.Items.GEARS_TUNGSTEN).map('m', ItemsZT.MOTOR).map('c', TagsZT.Items.COILS_GOLD).result(ItemsZT.MULTI_TOOL_TUNGSTEN_MOTOR).register();
		
		event.shaped().shape("ppl", "gcl", "ppl").map('p', TagsZT.Items.PLATES_GOLD).map('l', ItemsZT.LATEX).map('g', TagsZT.Items.COILS_GOLD).map('c', ItemsZT.BASIC_CIRCUIT).result(ItemsZT.BASIC_MULTI_TOOL).register();
		
		event.smelting().input(Ingredient.fromValues(Stream.of(new Ingredient.TagValue(Tags.Items.GEMS_QUARTZ), new Ingredient.ItemValue(new ItemStack(BlocksZT.PURE_SAND))))).xp(0.5F).result(ItemsZT.SILICON).register();
		
		event.shaped().shape(" r ", "psp", " c ").map('r', Tags.Items.DUSTS_REDSTONE).map('s', TagsZT.Items.SILICON).map('c', TagsZT.Items.COILS_COPPER).map('p', Items.REPEATER).result(ItemsZT.BASIC_CIRCUIT).register();
		
		event.shaped().shape("irc", "idc", "ada").map('i', TagsZT.Items.PLATES_IRON).map('r', Tags.Items.DUSTS_REDSTONE).map('c', TagsZT.Items.PLATES_COPPER).map('d', TagsZT.Items.DUSTS_COAL).map('a', TagsZT.Items.PLATES_ALUMINUM).result(ItemsZT.ACCUMULATOR_BASIC).register();
		
		var upgradeId = ForgeRegistries.ITEMS.getKey(ItemsZT.NETHERITE_ELECTRIC_SAW);
		event.register(upgradeId, new UpgradeRecipe(upgradeId, Ingredient.of(ItemsZT.TUNGSTEN_ELECTRIC_SAW), RecipeHelper.fromTag(Tags.Items.INGOTS_NETHERITE), new ItemStack(ItemsZT.NETHERITE_ELECTRIC_SAW)));
		
		upgradeId = ForgeRegistries.ITEMS.getKey(ItemsZT.MULTI_TOOL_NETHERITE_MOTOR);
		event.register(upgradeId, new UpgradeRecipe(upgradeId, Ingredient.of(ItemsZT.MULTI_TOOL_TUNGSTEN_MOTOR), RecipeHelper.fromTag(TagsZT.Items.GEARS_NETHERITE), new ItemStack(ItemsZT.MULTI_TOOL_NETHERITE_MOTOR)));
		
		// PATCHOULI COMPAT
		var guideBook = ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli", "guide_book"));
		if(guideBook != Items.AIR)
		{
			ItemStack bookStack = new ItemStack(guideBook);
			if(!bookStack.isEmpty())
			{
				bookStack.addTagElement("patchouli:book", StringTag.valueOf(ZeithTechAPI.id("guide").toString()));
				event.shapeless().add(Items.BOOK).add(Tags.Items.INGOTS_COPPER).result(bookStack).register();
			}
		}
	}
	
	static void machineAssembler(ReloadRecipeRegistryEvent.AddRecipes<RecipeMachineAssembler> e)
	{
		if(!e.is(RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY)) return;
		
		var b = e.<RecipeMachineAssembler.Builder> builderFactory();
		
		b.get().minTier(TechTier.BASIC)
				.shape("  m  ", " gGg ", "pibip", " fef ", "  m  ")
				.map('m', ItemsZT.MOTOR)
				.map('g', TagsZT.Items.INGOTS_ALUMINUM)
				.map('G', TagsZT.Items.GEARS_COPPER)
				.map('p', TagsZT.Items.PLATES_ALUMINUM)
				.map('i', BlocksZT_Transport.COPPER_ITEM_PIPE)
				.map('b', TagsZT.Items.STORAGE_BLOCKS_SILVER)
				.map('f', BlocksZT_Transport.IRON_FLUID_PIPE)
				.map('e', BlocksZT_Transport.INSULATED_ALUMINUM_WIRE)
				.result(BlocksZT.AUXILIARY_IO_PORT)
				.register();
	}
	
	static void wasteProcessing(ReloadRecipeRegistryEvent.AddRecipes<RecipeWasteProcessor> e)
	{
		if(!e.is(RecipeRegistriesZT_Processing.WASTE_PROCESSING)) return;
		
		var b = e.<RecipeWasteProcessor.WasteProcessorRecipeBuilder> builderFactory();
		
		var water = FluidIngredient.ofTags(List.of(FluidTags.WATER));
		
		b.get().input(Ingredient.fromValues(Stream.of(new Ingredient.TagValue(ItemTags.SAND), new Ingredient.ItemValue(new ItemStack(BlocksZT.PURE_SAND)))))
				.input(new FluidIngredientStack(water, 1000))
				.time(7 * 20)
				.byproduct(
						new ExtraOutput(new ItemStack(BlocksZT.PURE_SAND), 1F)
				)
				.register();
	}
}