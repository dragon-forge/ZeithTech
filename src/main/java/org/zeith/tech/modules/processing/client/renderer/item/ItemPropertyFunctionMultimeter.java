package org.zeith.tech.modules.processing.client.renderer.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.zeith.tech.modules.transport.items.ItemMultimeter;

import java.util.*;
import java.util.function.Function;

public class ItemPropertyFunctionMultimeter
		implements ClampedItemPropertyFunction
{
	private final Map<UUID, CompassWobble> wobble = new HashMap<>();
	private final Function<UUID, CompassWobble> wobbleGen = uuid -> new CompassWobble();
	
	public ItemPropertyFunctionMultimeter()
	{
	}
	
	private CompassWobble wobble(ItemStack stack)
	{
		var tag = stack.getOrCreateTag();
		return wobble.computeIfAbsent(tag.contains("Identity") ? tag.getUUID("Identity") : ItemMultimeter.ZERO_ID, wobbleGen);
	}
	
	@Override
	public float unclampedCall(ItemStack stack, ClientLevel level, LivingEntity owner, int hash)
	{
		Entity entity = owner != null ? owner : stack.getEntityRepresentation();
		if(entity == null)
			return 0.0F;
		else
		{
			level = this.tryFetchLevelIfMissing(entity, level);
			return level == null ? 0.0F : this.getCompassRotation(stack, level, hash, entity);
		}
	}
	
	private float getCompassRotation(ItemStack stack, ClientLevel level, int hash, Entity entity)
	{
		long i = level.getGameTime();
		return this.getRotationTowardsCompassTarget(entity, stack, i);
	}
	
	private float getRotationTowardsCompassTarget(Entity entity, ItemStack item, long gameTime)
	{
		float currentRotation = 0;
		
		if(entity instanceof Player player && player.isLocalPlayer())
		{
			var wobble = this.wobble(item);
			
			if(wobble.shouldUpdate(gameTime))
				wobble.update(gameTime, ItemMultimeter.getLoadFromLook(item));
			
			double finalRotation = currentRotation + wobble.rotation;
			
			return Mth.clamp((float) finalRotation, 0.0F, 1.0F);
		}
		
		return 0F;
	}
	
	private ClientLevel tryFetchLevelIfMissing(Entity owner, ClientLevel level)
	{
		return level == null && owner.level instanceof ClientLevel ? (ClientLevel) owner.level : level;
	}
	
	static class CompassWobble
	{
		double rotation;
		private double deltaRotation;
		private long lastUpdateTick;
		
		boolean shouldUpdate(long time)
		{
			return this.lastUpdateTick != time;
		}
		
		void update(long time, double next)
		{
			this.lastUpdateTick = time;
			double step = next - this.rotation;
			this.deltaRotation += step * 0.1D;
			this.deltaRotation *= 0.8D;
			this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
		}
	}
}