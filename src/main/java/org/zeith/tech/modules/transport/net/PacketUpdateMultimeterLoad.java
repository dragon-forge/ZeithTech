package org.zeith.tech.modules.transport.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.net.IPacket;
import org.zeith.hammerlib.net.PacketContext;
import org.zeith.tech.modules.transport.items.ItemMultimeter;

import java.util.UUID;

public class PacketUpdateMultimeterLoad
		implements IPacket
{
	float load;
	UUID identity;
	
	public PacketUpdateMultimeterLoad(float load, ItemStack stack)
	{
		this.load = load;
		this.identity = stack.getOrCreateTag().getUUID("Identity");
	}
	
	public PacketUpdateMultimeterLoad()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeFloat(load);
		buf.writeUUID(identity);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		load = buf.readFloat();
		identity = buf.readUUID();
	}
	
	public float getLoad()
	{
		return load;
	}
	
	public UUID getIdentity()
	{
		return identity;
	}
	
	@Override
	public void clientExecute(PacketContext ctx)
	{
		ItemMultimeter.handleClient(this);
	}
}