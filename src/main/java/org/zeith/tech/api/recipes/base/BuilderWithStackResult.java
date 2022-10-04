package org.zeith.tech.api.recipes.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.zeith.hammerlib.api.crafting.IGeneralRecipe;
import org.zeith.hammerlib.api.crafting.building.GeneralRecipeBuilder;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;

public abstract class BuilderWithStackResult<R extends IGeneralRecipe, B extends BuilderWithStackResult<R, B>>
		extends GeneralRecipeBuilder<R, B>
{
	protected ItemStack result = ItemStack.EMPTY;
	
	public BuilderWithStackResult(IRecipeRegistrationEvent<R> registrar)
	{
		super(registrar);
	}
	
	public B result(ItemStack stack)
	{
		this.result = stack;
		return (B) this;
	}
	
	public B result(ItemLike provider)
	{
		this.result = new ItemStack(provider);
		return (B) this;
	}
	
	public B result(ItemLike provider, int count)
	{
		this.result = new ItemStack(provider, count);
		return (B) this;
	}
	
	@Override
	protected ResourceLocation generateId()
	{
		return registrar.nextId(result.getItem());
	}
	
	@Override
	protected void validate() throws IllegalStateException
	{
		if(result.isEmpty())
			throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined result!");
	}
}