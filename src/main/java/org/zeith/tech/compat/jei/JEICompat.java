package org.zeith.tech.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraftforge.fml.LogicalSide;
import org.zeith.hammerlib.compat.base.Ability;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.LogicalSidePredictor;
import org.zeith.tech.api.compat.jei.ITieredRecipeType;
import org.zeith.tech.api.recipes.base.ITieredRecipe;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;
import org.zeith.tech.compat._base.BaseCompatZT;
import org.zeith.tech.compat._base.abils.IRecipeLifecycleListener;

import java.util.List;
import java.util.Optional;

import static org.zeith.tech.compat.jei.JeiZT.*;

@BaseCompat.LoadCompat(
		modid = "jei",
		compatType = BaseCompatZT.class
)
public class JEICompat
		extends BaseCompatZT
		implements IRecipeLifecycleListener
{
	@Override
	public <R> Optional<R> getAbility(Ability<R> ability)
	{
		return ability.findIn(this);
	}
	
	@Override
	public <T extends IZeithTechRecipe> void onRecipeRegistered(T recipe)
	{
		IRecipeLifecycleListener.super.onRecipeRegistered(recipe);
		
		if(LogicalSidePredictor.getCurrentLogicalSide() == LogicalSide.CLIENT)
		{
			for(RecipeType<?> type : jeiRecipeTypes)
			{
				if(recipe.is(type.getRecipeClass()))
				{
					RecipeType<T> type1 = Cast.cast(type);
					
					if(recipe instanceof ITieredRecipe tiered && !ITieredRecipeType.get(type)
							.map(itrt -> itrt.canHandle(tiered.getMinTier()))
							.orElse(true))
						continue;
					
					jeiRuntime.getRecipeManager().addRecipes(type1, List.of(recipe));
				}
			}
		}
	}
	
	@Override
	public <T extends IZeithTechRecipe> void onRecipeDeRegistered(T recipe)
	{
		IRecipeLifecycleListener.super.onRecipeDeRegistered(recipe);
		
		if(LogicalSidePredictor.getCurrentLogicalSide() == LogicalSide.CLIENT)
		{
			for(RecipeType<?> type : jeiRecipeTypes)
			{
				if(recipe.is(type.getRecipeClass()))
				{
					RecipeType<T> type1 = Cast.cast(type);
					
					jeiRuntime.getRecipeManager().hideRecipes(type1, List.of(recipe));
				}
			}
		}
	}
}