package org.zeith.tech.modules.processing.client.renderer.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.zeith.tech.modules.transport.items.ItemMultimeter;

public class ItemPropertyFunctionMultimeter
		implements ClampedItemPropertyFunction
{
	public static final int DEFAULT_ROTATION = 0;
	private final CompassWobble wobble = new CompassWobble();
	
	public ItemPropertyFunctionMultimeter()
	{
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
		return this.getRotationTowardsCompassTarget(entity, i);
	}
	
	private float getRotationTowardsCompassTarget(Entity entity, long gameTime)
	{
		float currentRotation = 0;
		
		if(entity instanceof Player player)
		{
			if(player.isLocalPlayer())
			{
				if(this.wobble.shouldUpdate(gameTime))
					this.wobble.update(gameTime, ItemMultimeter.getLoadFromLook(player));
				
				double finalRotation = currentRotation + this.wobble.rotation;
				
				return Mth.clamp((float) finalRotation, 0.0F, 1.0F);
			}
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
//			step = Mth.positiveModulo(step + 0.5D, 1.0D) - 0.5D;
			
			this.deltaRotation += step * 0.1D;
			this.deltaRotation *= 0.8D;
			
			this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
		}
	}
}