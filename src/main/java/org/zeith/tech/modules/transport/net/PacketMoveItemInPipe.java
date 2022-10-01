package org.zeith.tech.modules.transport.net;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import org.zeith.hammerlib.net.*;
import org.zeith.tech.modules.transport.blocks.item_pipe.ItemInPipe;
import org.zeith.tech.modules.transport.blocks.item_pipe.TileItemPipe;

import java.util.UUID;

@MainThreaded
public class PacketMoveItemInPipe
		implements IPacket
{
	BlockPos pipe;
	UUID itemId;
	Direction to;
	
	public static void send(TileItemPipe current, ItemInPipe item, Direction to)
	{
		if(!current.isOnServer()) return;
		var pos = current.getPosition();
		var pkt = new PacketMoveItemInPipe();
		pkt.pipe = pos;
		pkt.itemId = item.itemId;
		pkt.to = to;
		Network.sendToTracking(current.getLevel().getChunkAt(pos), pkt);
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pipe);
		buf.writeUUID(itemId);
		buf.writeEnum(to);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		pipe = buf.readBlockPos();
		itemId = buf.readUUID();
		to = buf.readEnum(Direction.class);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		Level world = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).orElse(null);
		if(world.getBlockEntity(pipe) instanceof TileItemPipe ourPipe)
		{
			var item = ourPipe.contents.byId(itemId);
			item.ifPresent(it ->
			{
				ourPipe.transferItem(it, to);
			});
		}
	}
}