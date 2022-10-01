package org.zeith.tech.modules.world.items;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import org.zeith.tech.modules.world.entity.BoatZT;
import org.zeith.tech.modules.world.entity.ChestBoatZT;

import java.util.List;
import java.util.function.Predicate;

public class ItemBoat
		extends Item
{
	private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
	private final BoatZT.Type type;
	private final boolean hasChest;
	
	public ItemBoat(boolean chest, BoatZT.Type type, Item.Properties props)
	{
		super(props);
		this.hasChest = chest;
		this.type = type;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level lvl, Player player, InteractionHand hand)
	{
		ItemStack held = player.getItemInHand(hand);
		HitResult res = getPlayerPOVHitResult(lvl, player, ClipContext.Fluid.ANY);
		
		if(res.getType() == HitResult.Type.MISS)
			return InteractionResultHolder.pass(held);
		else
		{
			Vec3 vec3 = player.getViewVector(1.0F);
			double d0 = 5.0D;
			List<Entity> list = lvl.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(d0)).inflate(1.0D), ENTITY_PREDICATE);
			if(!list.isEmpty())
			{
				Vec3 vec31 = player.getEyePosition();
				
				for(Entity entity : list)
				{
					AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
					if(aabb.contains(vec31))
					{
						return InteractionResultHolder.pass(held);
					}
				}
			}
			
			if(res.getType() == HitResult.Type.BLOCK)
			{
				BoatZT boat = this.getBoat(lvl, res);
				boat.setType(this.type);
				boat.setYRot(player.getYRot());
				if(!lvl.noCollision(boat, boat.getBoundingBox()))
				{
					return InteractionResultHolder.fail(held);
				} else
				{
					if(!lvl.isClientSide)
					{
						lvl.addFreshEntity(boat);
						lvl.gameEvent(player, GameEvent.ENTITY_PLACE, res.getLocation());
						if(!player.getAbilities().instabuild)
						{
							held.shrink(1);
						}
					}
					
					player.awardStat(Stats.ITEM_USED.get(this));
					return InteractionResultHolder.sidedSuccess(held, lvl.isClientSide());
				}
			} else
			{
				return InteractionResultHolder.pass(held);
			}
		}
	}
	
	private BoatZT getBoat(Level lvl, HitResult hr)
	{
		var pos = hr.getLocation();
		return this.hasChest
				? new ChestBoatZT(lvl, pos.x, pos.y, pos.z)
				: new BoatZT(lvl, pos.x, pos.y, pos.z);
	}
}