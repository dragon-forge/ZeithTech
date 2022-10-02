package org.zeith.tech.api.events.recipe;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.zeith.tech.api.recipes.RecipeHammering;

@Cancelable
public class HammerHitEvent
		extends PlayerEvent
{
	private final ItemStack hammer;
	private final BlockHitResult hit;
	private final ItemEntity entity;
	private final RecipeHammering recipe;
	
	public HammerHitEvent(Player player, ItemStack hammer, BlockHitResult hit, RecipeHammering recipe, ItemEntity entity)
	{
		super(player);
		this.hammer = hammer;
		this.hit = hit;
		this.recipe = recipe;
		this.entity = entity;
	}
	
	public ItemStack getHammer()
	{
		return hammer;
	}
	
	public BlockHitResult getHit()
	{
		return hit;
	}
	
	public RecipeHammering getRecipe()
	{
		return recipe;
	}
	
	public ItemEntity getItem()
	{
		return entity;
	}
}