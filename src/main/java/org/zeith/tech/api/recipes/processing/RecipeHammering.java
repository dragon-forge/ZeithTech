package org.zeith.tech.api.recipes.processing;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammerlib.api.crafting.impl.*;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.BuilderWithStackResult;
import org.zeith.tech.api.recipes.base.IUnaryRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeHammering
		extends BaseNameableRecipe
		implements IUnaryRecipe
{
	final List<TagKey<Block>> blockHammeringTags;
	final int hitCount;
	final Ingredient input;
	final ItemStack output;
	final TechTier tier;
	final Component displayHitCount;
	
	public RecipeHammering(ResourceLocation id, Ingredient input, ItemStack output, int hitCount, List<TagKey<Block>> blockHammeringTags, TechTier tier)
	{
		super(id, new ItemStackResult(output), NonNullList.of(new MCIngredient(input)));
		this.input = input;
		this.output = output;
		this.hitCount = hitCount;
		this.blockHammeringTags = blockHammeringTags;
		this.tier = tier;
		this.displayHitCount = Component.literal(Integer.toUnsignedString(hitCount));
	}
	
	@Override
	public void onDeregistered()
	{
		ZeithTech.forCompats(c -> c.deregisterRecipe(this));
	}
	
	public int getHitCountRaw()
	{
		return hitCount;
	}
	
	public Component getHitCount()
	{
		return displayHitCount;
	}
	
	public int getHitCount(ItemStack hammer, ItemEntity targetEntity, Vec3 origin, Player player, ServerLevel level)
	{
		return hitCount;
	}
	
	public boolean canPerformHammering(BlockState state)
	{
		return getHammeringTags().stream().anyMatch(state::is);
	}
	
	public List<TagKey<Block>> getHammeringTags()
	{
		return blockHammeringTags;
	}
	
	public ItemStack getRecipeOutput()
	{
		return output.copy();
	}
	
	public boolean matches(BlockState state, ItemStack stack, TechTier tier)
	{
		return canPerformHammering(state) && input.test(stack) && isTierGoodEnough(tier);
	}
	
	public boolean isTierGoodEnough(TechTier tier)
	{
		return tier.isOrHigher(getTier());
	}
	
	@Override
	public Ingredient getInput()
	{
		return input;
	}
	
	@Override
	public ItemStack assemble(BlockEntity tile)
	{
		return output.copy();
	}
	
	@Override
	public TechTier getTier()
	{
		return tier;
	}
	
	@Override
	public int getCraftTime()
	{
		return hitCount * 50;
	}
	
	public static class Builder
			extends BuilderWithStackResult<RecipeHammering, Builder>
	{
		private Ingredient inputItem = Ingredient.EMPTY;
		private int hitCount = 4;
		private TechTier tier = TechTier.BASIC;
		
		private final List<TagKey<Block>> blockHammeringTags = new ArrayList<>();
		
		{
			blockHammeringTags.add(BlockTags.ANVIL);
		}
		
		public Builder(IRecipeRegistrationEvent<RecipeHammering> event)
		{
			super(event);
		}
		
		/**
		 * BASIC is the Iron Hammer!
		 */
		public Builder withTier(TechTier tier)
		{
			this.tier = tier;
			return this;
		}
		
		public Builder hitCount(int exact)
		{
			this.hitCount = exact;
			return this;
		}
		
		public Builder replaceBlockTarget(TagKey<Block> blockTag)
		{
			blockHammeringTags.clear();
			return addBlockTarget(blockTag);
		}
		
		public Builder replaceBlockTarget(List<TagKey<Block>> blockTags)
		{
			blockHammeringTags.clear();
			return addBlockTarget(blockTags);
		}
		
		public Builder addBlockTarget(TagKey<Block> blockTag)
		{
			blockHammeringTags.add(blockTag);
			return this;
		}
		
		public Builder addBlockTarget(List<TagKey<Block>> blockTags)
		{
			blockHammeringTags.clear();
			blockHammeringTags.addAll(blockTags);
			return this;
		}
		
		public Builder input(Ingredient inputItem)
		{
			this.inputItem = inputItem;
			return this;
		}
		
		public Builder input(Object input)
		{
			return input(RecipeHelper.fromComponent(input));
		}
		
		@Override
		protected void validate()
		{
			super.validate();
			if(inputItem == null || inputItem.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined input!");
			if(blockHammeringTags.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a processing block!");
			if(hitCount < 1)
				throw new IllegalStateException(getClass().getSimpleName() + " does not have a set amount of hits to perform hammering operation!");
		}
		
		@Override
		protected RecipeHammering createRecipe() throws IllegalStateException
		{
			return new RecipeHammering(getIdentifier(), inputItem, result, hitCount, blockHammeringTags, tier);
		}
	}
}