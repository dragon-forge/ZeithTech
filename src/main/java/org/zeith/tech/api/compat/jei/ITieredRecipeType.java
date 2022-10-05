package org.zeith.tech.api.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import org.zeith.tech.api.enums.TechTier;

import java.util.Optional;

/**
 * Allows binding {@link TechTier}s to JEI's {@link RecipeType}. (with the use of mixins inside ZeithTech)
 * Use {@link ITieredRecipeType#setMaxTier(RecipeType, TechTier)} to achieve the desired effect.
 */
public interface ITieredRecipeType
{
	Optional<TechTier> getMaxZeithTechTier();
	
	void setMaxTier(TechTier tier);
	
	default boolean canHandle(TechTier tier)
	{
		return getMaxZeithTechTier().map(tier::isOrLower)
				.orElse(true);
	}
	
	@SuppressWarnings("ConstantConditions")
	static Optional<ITieredRecipeType> get(RecipeType<?> type)
	{
		return ITieredRecipeType.class.isInstance(type) ? Optional.of(ITieredRecipeType.class.cast(type)) : Optional.empty();
	}
	
	static <T> RecipeType<T> setMaxTier(RecipeType<T> type, TechTier tier)
	{
		get(type).ifPresent(t -> t.setMaxTier(tier));
		return type;
	}
}