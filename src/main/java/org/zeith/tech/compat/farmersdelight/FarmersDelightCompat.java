package org.zeith.tech.compat.farmersdelight;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.compat.BaseCompat;
import org.zeith.tech.modules.world.init.BlocksZT_World;
import org.zeith.tech.utils.LegacyEventBus;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.ingredient.ChanceResult;
import vectorwing.farmersdelight.common.crafting.ingredient.ToolActionIngredient;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.List;

public class FarmersDelightCompat
		extends BaseCompat
{
	@Override
	public void setup(LegacyEventBus bus)
	{
		bus.addListener(FMLLoadCompleteEvent.class, this::loadComplete);
		HammerLib.EVENT_BUS.addListener(this::registerRecipes);
	}
	
	public void registerRecipes(RegisterRecipesEvent e)
	{
		e.add(new CuttingBoardRecipe(e.nextId(BlocksZT_World.STRIPPED_HEVEA_LOG.asItem()), "", Ingredient.of(BlocksZT_World.HEVEA_LOG), new ToolActionIngredient(ToolActions.AXE_STRIP),
				NonNullList.of(ChanceResult.EMPTY, new ChanceResult(new ItemStack(ModItems.TREE_BARK.get()), 1F), new ChanceResult(new ItemStack(BlocksZT_World.STRIPPED_HEVEA_LOG), 1F)),
				"minecraft:item.axe.strip"
		));
		
		e.add(new CuttingBoardRecipe(e.nextId(BlocksZT_World.STRIPPED_HEVEA_WOOD.asItem()), "", Ingredient.of(BlocksZT_World.HEVEA_WOOD), new ToolActionIngredient(ToolActions.AXE_STRIP),
				NonNullList.of(ChanceResult.EMPTY, new ChanceResult(new ItemStack(ModItems.TREE_BARK.get()), 1F), new ChanceResult(new ItemStack(BlocksZT_World.STRIPPED_HEVEA_WOOD), 1F)),
				"minecraft:item.axe.strip"
		));
	}
	
	public void loadComplete(FMLLoadCompleteEvent e)
	{
		var farmData = ZeithTechAPI.get()
				.getModules()
				.processing()
				.farmData();
		
		log.info("Added rich soil to list of dirts for farming.");
		farmData.addFarmlandPlaceable(ModItems.RICH_SOIL.get());
		
		log.info("Added tomato crop support.");
		farmData.registerCropSubAlgorithm(new TomatoCropSubAlgorithm(), List.of(ModItems.TOMATO_SEEDS.get()), List.of(ModBlocks.BUDDING_TOMATO_CROP.get(), ModBlocks.TOMATO_CROP.get()));
	}
}