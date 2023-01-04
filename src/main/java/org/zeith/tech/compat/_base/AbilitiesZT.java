package org.zeith.tech.compat._base;

import org.zeith.hammerlib.compat.base.Ability;
import org.zeith.tech.compat._base.abils.IFacadeObtainer;
import org.zeith.tech.compat._base.abils.IRecipeLifecycleListener;

public class AbilitiesZT
{
	public static final Ability<IRecipeLifecycleListener> RECIPE_LIFECYCLE_LISTENER_ABILITY = new Ability<>(IRecipeLifecycleListener.class);
	public static final Ability<IFacadeObtainer> FACADE_OBTAINER_ABILITY = new Ability<>(IFacadeObtainer.class);
}