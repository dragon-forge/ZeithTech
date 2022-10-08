package org.zeith.tech.modules.transport.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import org.zeith.hammerlib.net.*;
import org.zeith.tech.modules.transport.blocks.item_pipe.ItemInPipe;
import org.zeith.tech.modules.transport.blocks.item_pipe.TileItemPipe;

@MainThreaded
public class PacketUpdateItemInPipe
		implements IPacket
{
	ItemInPipe item;
	BlockPos pipe;
	
	public static void send(TileItemPipe current, ItemInPipe item)
	{
		if(!current.isOnServer()) return;
		var pos = current.getPosition();
		var pkt = new PacketUpdateItemInPipe();
		pkt.pipe = pos;
		pkt.item = item;
		Network.sendToTracking(current.getLevel().getChunkAt(pos), pkt);
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pipe);
		buf.writeNbt(item.serializeNBT());
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		pipe = buf.readBlockPos();
		item = new ItemInPipe(buf.readNbt());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		Level world = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).orElse(null);
		if(world.getBlockEntity(pipe) instanceof TileItemPipe ourPipe)
		{
			var item = ourPipe.contents.byId(this.item.itemId).orElse(null);
			if(item != null)
				item.deserializeNBT(this.item.serializeNBT());
			else
				ourPipe.contents.addNow(this.item);
		}
	}
}