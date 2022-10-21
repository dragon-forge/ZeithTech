package org.zeith.tech.modules.processing.init;

import net.minecraft.world.damagesource.DamageSource;
import org.zeith.tech.core.ZeithTech;

public interface DamageTypesZT_Processing
{
	DamageSource SULFURIC_ACID = new DamageSource(ZeithTech.MOD_ID + ".sulfuric_acid").bypassArmor();
}