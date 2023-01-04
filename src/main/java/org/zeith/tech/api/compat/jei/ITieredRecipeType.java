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
	/**
	 * Returns the maximum {@link TechTier} that this recipe type can handle.
	 *
	 * @return an {@link Optional} containing the maximum {@link TechTier}, or an empty optional if no maximum has been set
	 */
	Optional<TechTier> getMaxZeithTechTier();
	
	/**
	 * Sets the maximum {@link TechTier} that this recipe type can handle.
	 *
	 * @param tier
	 * 		the maximum {@link TechTier} to set
	 */
	void setMaxTier(TechTier tier);
	
	/**
	 * Returns whether this recipe type can handle the specified {@link TechTier}.
	 *
	 * @param tier
	 * 		the {@link TechTier} to check
	 *
	 * @return {@code true} if the recipe type can handle the specified {@link TechTier}, {@code false} otherwise
	 */
	default boolean canHandle(TechTier tier)
	{
		return getMaxZeithTechTier().map(tier::isOrLower)
				.orElse(true);
	}
	
	
	/**
	 * Returns an {@link Optional} containing an {@link ITieredRecipeType} if the specified {@link RecipeType} is an instance of {@link ITieredRecipeType}, or an empty optional otherwise.
	 *
	 * @param type
	 * 		the {@link RecipeType} to check
	 *
	 * @return an {@link Optional} containing an {@link ITieredRecipeType} if the specified {@link RecipeType} is an instance of {@link ITieredRecipeType}, or an empty optional otherwise
	 */
	@SuppressWarnings("ConstantConditions")
	static Optional<ITieredRecipeType> get(RecipeType<?> type)
	{
		return ITieredRecipeType.class.isInstance(type) ? Optional.of(ITieredRecipeType.class.cast(type)) : Optional.empty();
	}
	
	/**
	 * Sets the maximum {@link TechTier} that the specified {@link RecipeType} can handle.
	 *
	 * @param type
	 * 		the {@link RecipeType} to set the maximum {@link TechTier} for
	 * @param tier
	 * 		the maximum {@link TechTier} to set
	 * @param <T>
	 * 		the type of recipes for the {@link RecipeType}
	 *
	 * @return the specified {@link RecipeType}
	 */
	static <T> RecipeType<T> setMaxTier(RecipeType<T> type, TechTier tier)
	{
		get(type).ifPresent(t -> t.setMaxTier(tier));
		return type;
	}
}