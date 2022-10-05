package org.zeith.tech.api.recipes.base;

public interface IZeithTechRecipe
{
	default <T> boolean is(Class<T> type)
	{
		return getClass().isAssignableFrom(type);
	}
}