package org.zeith.tech.modules.generators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.HammerLib;
import org.zeith.tech.api.modules.IModuleGenerators;
import org.zeith.tech.api.recipes.processing.RecipeLiquidFuel;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.core.IInternalCode;
import org.zeith.tech.modules.generators.init.RecipeRegistriesZT_Generators;
import org.zeith.tech.modules.generators.init.RecipesZT_Generators;
import org.zeith.tech.utils.LegacyEventBus;

public class GeneratorsModule
		implements IModuleGenerators, IInternalCode
{
	public static final Logger LOG = LogManager.getLogger("ZeithTech [Generators]");
	
	protected boolean wasEnabled;
	
	@Override
	public void construct(LegacyEventBus bus)
	{
		RecipeRegistriesZT_Generators.construct();
	}
	
	@Override
	public boolean isModuleActivated()
	{
		return wasEnabled;
	}
	
	@Override
	public void enable()
	{
		wasEnabled = true;
		HammerLib.EVENT_BUS.addListener(RecipesZT_Generators::provideRecipes);
		HammerLib.EVENT_BUS.addGenericListener(RecipeMachineAssembler.class, RecipesZT_Generators::addMachineAssemblyRecipes);
		HammerLib.EVENT_BUS.addGenericListener(RecipeLiquidFuel.class, RecipesZT_Generators::addLiquidFuelRecipes);
	}
}