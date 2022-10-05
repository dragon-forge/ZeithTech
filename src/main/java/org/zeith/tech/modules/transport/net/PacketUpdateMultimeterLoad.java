package org.zeith.tech.modules.transport.net;

import net.minecraft.network.FriendlyByteBuf;
import org.zeith.hammerlib.net.IPacket;
import org.zeith.hammerlib.net.PacketContext;
import org.zeith.tech.modules.transport.items.ItemMultimeter;

public class PacketUpdateMultimeterLoad
		implements IPacket
{
	float load;
	
	public PacketUpdateMultimeterLoad(float load)
	{
		this.load = load;
	}
	
	public PacketUpdateMultimeterLoad()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeFloat(load);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		load = buf.readFloat();
	}
	
	public float getLoad()
	{
		return load;
	}
	
	@Override
	public void clientExecute(PacketContext ctx)
	{
		ItemMultimeter.handleClient(this);
	}
}