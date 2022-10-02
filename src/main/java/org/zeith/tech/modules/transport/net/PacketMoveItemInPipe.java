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

@MainThreaded
public class PacketMoveItemInPipe
		implements IPacket
{
	ItemInPipe item;
	BlockPos pipe;
	Direction to;
	
	public static void send(TileItemPipe current, ItemInPipe item, Direction to)
	{
		if(!current.isOnServer()) return;
		var pos = current.getPosition();
		var pkt = new PacketMoveItemInPipe();
		pkt.pipe = pos;
		pkt.to = to;
		pkt.item = item;
		Network.sendToTracking(current.getLevel().getChunkAt(pos), pkt);
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pipe);
		buf.writeEnum(to);
		buf.writeNbt(item.serializeNBT());
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		pipe = buf.readBlockPos();
		to = buf.readEnum(Direction.class);
		item = new ItemInPipe(buf.readNbt());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		Level world = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).orElse(null);
		if(world.getBlockEntity(pipe) instanceof TileItemPipe ourPipe)
		{
			item = ourPipe.contents.byId(item.itemId).orElse(item);
			ourPipe.transferItem(item, to);
			if(world.getBlockEntity(pipe.relative(to)) instanceof TileItemPipe remPipe)
			{
				item.update(remPipe);
				item.prevPipeProgress = 0;
				item.currentPipeProgress *= 0.25;
			}
		}
	}
}