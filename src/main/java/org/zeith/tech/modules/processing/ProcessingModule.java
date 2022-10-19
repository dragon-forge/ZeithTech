package org.zeith.tech.modules.processing;

import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.HammerLib;
import org.zeith.tech.api.modules.IModuleProcessing;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.core.IInternalCode;
import org.zeith.tech.modules.processing.init.*;
import org.zeith.tech.modules.processing.proxy.ClientProcessingProxyZT;
import org.zeith.tech.modules.processing.proxy.CommonProcessingProxyZT;
import org.zeith.tech.utils.LegacyEventBus;

public class ProcessingModule
		implements IModuleProcessing, IInternalCode
{
	public static final Logger LOG = LogManager.getLogger("ZeithTech [Processing]");
	public static final CommonProcessingProxyZT PROXY = DistExecutor.unsafeRunForDist(() -> ClientProcessingProxyZT::new, () -> CommonProcessingProxyZT::new);
	
	private boolean wasEnabled = false;
	
	@Override
	public void construct(LegacyEventBus bus)
	{
		PROXY.subEvents(bus);
		RecipeRegistriesZT_Processing.construct();
		FluidsZT_Processing.register(bus);
	}
	
	@Override
	public void enable()
	{
		wasEnabled = true;
		HammerLib.EVENT_BUS.addListener(RecipesZT_Processing::provideRecipes);
		HammerLib.EVENT_BUS.addGenericListener(RecipeMachineAssembler.class, RecipesZT_Processing::addMachineAssemblyRecipes);
		HammerLib.EVENT_BUS.addGenericListener(RecipeHammering.class, RecipesZT_Processing::addHammeringRecipes);
		HammerLib.EVENT_BUS.addGenericListener(RecipeGrinding.class, RecipesZT_Processing::addGrindingRecipes);
		HammerLib.EVENT_BUS.addGenericListener(RecipeSawmill.class, RecipesZT_Processing::addSawmillRecipes);
		HammerLib.EVENT_BUS.addGenericListener(RecipeFluidCentrifuge.class, RecipesZT_Processing::addFluidCentrifugeRecipes);
		HammerLib.EVENT_BUS.addGenericListener(RecipeWasteProcessor.class, RecipesZT_Processing::addWasteProcessingRecipes);
	}
	
	@Override
	public boolean isModuleActivated()
	{
		return wasEnabled;
	}
}