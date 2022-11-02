package org.zeith.tech.core.net;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.*;
import org.zeith.tech.modules.shared.init.ParticlesZT;

@MainThreaded
public class PacketSpawnHydrateParticles
		implements IPacket
{
	BlockPos source, dest;
	
	public PacketSpawnHydrateParticles(BlockPos source, BlockPos dest)
	{
		this.source = source;
		this.dest = dest;
	}
	
	public PacketSpawnHydrateParticles()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(source);
		buf.writeBlockPos(dest);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		source = buf.readBlockPos();
		dest = buf.readBlockPos();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		Vec3 start = Vec3.atCenterOf(source);
		Vec3 end = Vec3.atCenterOf(dest);
		
		var level = Minecraft.getInstance().level;
		
		if(level != null)
			level.addParticle(ParticlesZT.WATER, start.x, start.y, start.z, end.x, end.y, end.z);
	}
}