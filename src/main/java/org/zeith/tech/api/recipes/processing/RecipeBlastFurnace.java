package org.zeith.tech.api.recipes.processing;

import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.hammerlib.api.crafting.impl.*;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.api.block.multiblock.blast_furnace.IBlastFurnaceCasingBlock;
import org.zeith.tech.api.recipes.base.BuilderWithStackResult;
import org.zeith.tech.api.recipes.base.IZeithTechRecipe;

public class RecipeBlastFurnace
		extends BaseNameableRecipe
		implements IZeithTechRecipe
{
	protected final IBlastFurnaceCasingBlock.BlastFurnaceTier tier;
	protected final Ingredient inputA, inputB;
	protected final ItemStack result;
	protected final int craftTime, neededTemperature;
	
	public RecipeBlastFurnace(ResourceLocation id, IBlastFurnaceCasingBlock.BlastFurnaceTier tier, int neededTemperature, ItemStack output, Ingredient inputA, Ingredient inputB, int craftTime)
	{
		super(id, new ItemStackResult(output), Util.make(NonNullList.create(), lst ->
		{
			lst.add(new MCIngredient(inputA));
			lst.add(new MCIngredient(inputB));
		}));
		
		this.tier = tier;
		this.neededTemperature = neededTemperature;
		this.result = output;
		this.inputA = inputA;
		this.inputB = inputB;
		this.craftTime = craftTime;
	}
	
	public IBlastFurnaceCasingBlock.BlastFurnaceTier getTier()
	{
		return tier;
	}
	
	public int getNeededTemperature()
	{
		return neededTemperature;
	}
	
	public Ingredient getInputA()
	{
		return inputA;
	}
	
	public Ingredient getInputB()
	{
		return inputB;
	}
	
	public ItemStack assemble()
	{
		return result.copy();
	}
	
	public int getCraftTime()
	{
		return craftTime;
	}
	
	public static class BlastBuilder
			extends BuilderWithStackResult<RecipeBlastFurnace, BlastBuilder>
	{
		protected Ingredient inputA = Ingredient.EMPTY, inputB = Ingredient.EMPTY;
		protected int time = 200, neededTemperature = 1536;
		protected IBlastFurnaceCasingBlock.BlastFurnaceTier tier = IBlastFurnaceCasingBlock.BlastFurnaceTier.BASIC;
		
		public BlastBuilder(IRecipeRegistrationEvent<RecipeBlastFurnace> event)
		{
			super(event);
		}
		
		public BlastBuilder tier(IBlastFurnaceCasingBlock.BlastFurnaceTier tier)
		{
			this.tier = tier;
			return this;
		}
		
		public BlastBuilder craftTime(int time)
		{
			this.time = time;
			return this;
		}
		
		public BlastBuilder minTemperature(int neededTemperature)
		{
			this.neededTemperature = neededTemperature;
			return this;
		}
		
		public BlastBuilder input(Ingredient inputItem)
		{
			this.inputA = inputItem;
			return this;
		}
		
		public BlastBuilder input(Ingredient inputItemA, Ingredient inputItemB)
		{
			this.inputA = inputItemA;
			this.inputB = inputItemB;
			return this;
		}
		
		public BlastBuilder input(Object input)
		{
			return input(RecipeHelper.fromComponent(input));
		}
		
		public BlastBuilder input(Object inputA, Object inputB)
		{
			return input(RecipeHelper.fromComponent(inputA), RecipeHelper.fromComponent(inputB));
		}
		
		@Override
		protected void validate()
		{
			super.validate();
			if(inputA.isEmpty() && inputB.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined input!");
			if(time < 1)
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a set time to perform the recipe!");
		}
		
		@Override
		protected RecipeBlastFurnace createRecipe() throws IllegalStateException
		{
			if(inputA.isEmpty())
			{
				inputA = inputB;
				inputB = Ingredient.EMPTY;
			}
			
			return new RecipeBlastFurnace(getIdentifier(), tier, neededTemperature, result, inputA, inputB, time);
		}
	}
}