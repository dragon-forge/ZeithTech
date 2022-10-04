package org.zeith.tech.api.recipes.base;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.zeith.hammerlib.api.crafting.INameableRecipe;
import org.zeith.hammerlib.api.crafting.NamespacedRecipeRegistry;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.tile.ITieredTile;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface IUnaryRecipe
{
	default TechTier getTier()
	{
		return TechTier.BASIC;
	}
	
	Ingredient getInput();
	
	default int getInputCount()
	{
		return 1;
	}
	
	ItemStack assemble(BlockEntity tile);
	
	int getCraftTime();
	
	default boolean matches(BlockEntity tile, ItemStack input)
	{
		return getInput().test(input)
				&& input.getCount() >= getInputCount();
	}
	
	default boolean isTierGoodEnough(TechTier tier)
	{
		return tier.isOrHigher(getTier());
	}
	
	default boolean techTierMatches(BlockEntity tile)
	{
		return ITieredTile.get(tile)
				.map(getTier()::isOrLower)
				.orElse(false);
	}
	
	interface IUnaryRecipeProvider<T extends IUnaryRecipe>
	{
		Stream<T> recipes();
		
		default Optional<T> findMatching(BlockEntity tile, ItemStack input)
		{
			return recipes()
					.filter(i -> i.matches(tile, input))
					.findFirst();
		}
		
		static <R extends IUnaryRecipe> IUnaryRecipeProvider<R> custom(Supplier<Stream<R>> recipes)
		{
			return recipes::get;
		}
		
		static <R extends IUnaryRecipe & INameableRecipe> IUnaryRecipeProvider<R> fromRecipeRegistry(NamespacedRecipeRegistry<R> registry)
		{
			return () -> registry.getRecipes().stream();
		}
	}
}