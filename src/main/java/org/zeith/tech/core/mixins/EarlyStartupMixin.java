package org.zeith.tech.core.mixins;

import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Tiers;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.hammerlib.util.java.Cast;

@Mixin(Bootstrap.class)
public class EarlyStartupMixin
{
	@Shadow
	@Final
	private static Logger LOGGER;
	
	@Inject(
			method = "bootStrap",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/core/Registry;keySet()Ljava/util/Set;",
					shift = At.Shift.BEFORE,
					by = 1
			)
	)
	private static void initRegistries(CallbackInfo ci)
	{
		TiersAccessor netherite = Cast.cast(Tiers.NETHERITE);
		netherite.setUses(netherite.getUses() + 512);
		netherite.setEnchantmentValue(netherite.getEnchantmentValue() + 5);
		netherite.setDamage(netherite.getDamage() + 2F);
		netherite.setSpeed(netherite.getSpeed() + 3F);
		netherite.setLevel(5);
		LOGGER.info("ZeithTech has patched netherite tier!");
	}
}