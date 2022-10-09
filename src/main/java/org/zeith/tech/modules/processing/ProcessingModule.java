package org.zeith.tech.modules.processing;

import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredientStack;
import org.zeith.tech.api.modules.IModuleProcessing;
import org.zeith.tech.api.recipes.processing.*;
import org.zeith.tech.core.IInternalCode;
import org.zeith.tech.modules.processing.init.FluidsZT_Processing;
import org.zeith.tech.modules.processing.init.RecipesZT_Processing;
import org.zeith.tech.modules.processing.proxy.ClientProcessingProxyZT;
import org.zeith.tech.modules.processing.proxy.CommonProcessingProxyZT;
import org.zeith.tech.utils.LegacyEventBus;

import java.util.ArrayList;
import java.util.List;

public class ProcessingModule
		implements IModuleProcessing, IInternalCode
{
	public static final CommonProcessingProxyZT PROXY = DistExecutor.unsafeRunForDist(() -> ClientProcessingProxyZT::new, () -> CommonProcessingProxyZT::new);
	
	private boolean wasEnabled = false;
	
	@Override
	public void construct(LegacyEventBus bus)
	{
		PROXY.subEvents(bus);
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
	}
	
	@Override
	public boolean isModuleActivated()
	{
		return wasEnabled;
	}
	
	private final List<FluidIngredientStack> liquidFuelBurnTime = new ArrayList<>();
	
	@Override
	public synchronized void setLiquidFuelBurnTime(FluidIngredient fluid, int burnTimeInTicks)
	{
		liquidFuelBurnTime.add(new FluidIngredientStack(fluid, burnTimeInTicks));
	}
	
	@Override
	public synchronized int getLiquidFuelBurnTime(FluidStack fluid)
	{
		return liquidFuelBurnTime.stream()
				.filter(s -> s.fluid().test(fluid))
				.mapToInt(FluidIngredientStack::amount)
				.max()
				.orElse(0);
	}
	
	@Override
	public List<Tuple<FluidIngredient, Integer>> getLiquidFuelBurnTimes()
	{
		return liquidFuelBurnTime.stream()
				.map(s -> new Tuple<>(s.fluid(), s.amount()))
				.toList();
	}
}