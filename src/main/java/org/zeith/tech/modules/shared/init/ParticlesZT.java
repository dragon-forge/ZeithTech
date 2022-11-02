package org.zeith.tech.modules.shared.init;

import net.minecraft.core.particles.SimpleParticleType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

@SimplyRegister
public interface ParticlesZT
{
	@RegistryName("water")
	SimpleParticleType WATER = new SimpleParticleType(false);
}