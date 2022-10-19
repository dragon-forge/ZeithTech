package org.zeith.tech.core.mixins;

import net.minecraft.world.item.Tiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Tiers.class)
public interface TiersAccessor
{
	@Accessor
	int getLevel();
	
	@Mutable
	@Accessor
	void setLevel(int level);
	
	@Accessor
	int getUses();
	
	@Mutable
	@Accessor
	void setUses(int uses);
	
	@Accessor
	float getSpeed();
	
	@Mutable
	@Accessor
	void setSpeed(float speed);
	
	@Accessor
	float getDamage();
	
	@Mutable
	@Accessor
	void setDamage(float damage);
	
	@Accessor
	int getEnchantmentValue();
	
	@Mutable
	@Accessor
	void setEnchantmentValue(int enchantmentValue);
}
