package org.zeith.tech.modules.shared.net;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.net.*;
import org.zeith.tech.modules.shared.init.BlocksZT;

import java.util.Optional;

@MainThreaded
public class PacketSpawnMasutEntityParticles
		implements IPacket
{
	int entityId;
	int particles;
	
	public PacketSpawnMasutEntityParticles(int entityId, int particles)
	{
		this.entityId = entityId;
		this.particles = particles;
	}
	
	public PacketSpawnMasutEntityParticles()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeInt(entityId);
		buf.writeVarInt(particles);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		entityId = buf.readInt();
		particles = buf.readVarInt();
	}
	
	@Override
	public void clientExecute(PacketContext ctx)
	{
		Optional.ofNullable(Minecraft.getInstance().level)
				.map(c -> c.getEntity(entityId))
				.ifPresent(entity ->
				{
					if(entity.level.isClientSide)
					{
						BlockState blockstate = BlocksZT.MASUT.defaultBlockState();
						for(int i = 0; i < particles; ++i)
							entity.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), entity.getX(), entity.getY(), entity.getZ(), 0.0D, 0.0D, 0.0D);
					}
				});
	}
}