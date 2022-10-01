package org.zeith.tech.modules.transport.init;

import net.minecraft.world.damagesource.DamageSource;
import org.zeith.tech.ZeithTech;

public interface DamageSourcesZT_Transport
{
	DamageSource ELECTROCUTION = new DamageSource(ZeithTech.MOD_ID + ".electrocution").setScalesWithDifficulty();
	DamageSource ELECTROCUTION_PEACEFUL = new DamageSource(ZeithTech.MOD_ID + ".electrocution");
}