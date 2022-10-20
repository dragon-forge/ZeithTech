package org.zeith.tech.modules.generators.init;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.processing.RecipeLiquidFuel;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.modules.processing.init.FluidsZT_Processing;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.shared.init.*;

public interface RecipesZT_Generators
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
	
	}
	
	static void addMachineAssemblyRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeMachineAssembler> evt)
	{
		if(!evt.is(RecipeRegistriesZT_Processing.MACHINE_ASSEMBLY)) return;
		
		var f = evt.<RecipeMachineAssembler.Builder> builderFactory();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  g  ", " ici ", "gibig", " scs ", "  g  ")
				.map('i', Tags.Items.INGOTS_IRON)
				.map('c', ItemsZT.COPPER_COIL)
				.map('b', Blocks.BLAST_FURNACE)
				.map('s', Tags.Items.STONE)
				.map('g', Tags.Items.INGOTS_COPPER)
				.result(BlocksZT_Generators.BASIC_FUEL_GENERATOR)
				.register();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  o  ", " lll ", "cpgtc", " iii ", "  o  ")
				.map('o', Tags.Items.INGOTS_COPPER)
				.map('l', TagsZT.Items.INGOTS_LEAD)
				.map('c', ItemsZT.COPPER_COIL)
				.map('p', TagsZT.Items.PLATES_ALUMINUM)
				.map('g', BlocksZT_Generators.BASIC_FUEL_GENERATOR)
				.map('t', BlocksZT.BASIC_FLUID_TANK)
				.map('i', Tags.Items.INGOTS_IRON)
				.result(BlocksZT_Generators.BASIC_LIQUID_FUEL_GENERATOR)
				.register();
		
		f.get().minTier(TechTier.BASIC)
				.shape("  w  ", " ptp ", "iptpi", " cgc ", "  b  ")
				.map('w', BlocksZT.UNINSULATED_COPPER_WIRE)
				.map('p', TagsZT.Items.PLATES_SILVER)
				.map('t', BlocksZT.BASIC_FLUID_TANK)
				.map('c', ItemsZT.COPPER_COIL)
				.map('g', BlocksZT.BASIC_FUEL_GENERATOR)
				.map('b', Items.IRON_BARS)
				.map('i', BlocksZT.IRON_FLUID_PIPE)
				.result(BlocksZT_Generators.MAGMATIC_GENERATOR)
				.register();
	}
	
	static void addLiquidFuelRecipes(ReloadRecipeRegistryEvent.AddRecipes<RecipeLiquidFuel> evt)
	{
		if(!evt.is(RecipeRegistriesZT.LIQUID_FUEL)) return;
		
		var f = evt.<RecipeLiquidFuel.LiquidFuelRecipeBuilder> builderFactory();
		
		f.get()
				.input(FluidsZT_Processing.DIESEL_FUEL.ingredient())
				.burnTime(1000)
				.register();
	}
}