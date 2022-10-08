package org.zeith.tech.core.mixins.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import org.spongepowered.asm.mixin.*;
import org.zeith.tech.api.compat.jei.ITieredRecipeType;
import org.zeith.tech.api.enums.TechTier;

import java.util.Optional;

@Mixin(RecipeType.class)
@Implements({
		@Interface(iface = ITieredRecipeType.class, prefix = "itr$")
})
public class JeiRecipeTypeMixin
{
	private TechTier techTier;
	
	public Optional<TechTier> itr$getMaxZeithTechTier()
	{
		return Optional.ofNullable(techTier);
	}
	
	public void itr$setMaxTier(TechTier tier)
	{
		this.techTier = tier;
	}
}