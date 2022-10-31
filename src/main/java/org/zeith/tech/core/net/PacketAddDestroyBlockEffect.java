package org.zeith.tech.core.net;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.*;

import java.util.Optional;

public class PacketAddDestroyBlockEffect
		implements IPacket
{
	BlockPos pos;
	BlockState state;
	
	public static void spawn(Level level, BlockPos pos, BlockState state)
	{
		if(level != null && !level.isClientSide)
			Network.sendToTracking(level.getChunkAt(pos), new PacketAddDestroyBlockEffect(pos, state));
	}
	
	public PacketAddDestroyBlockEffect(BlockPos pos, BlockState state)
	{
		this.pos = pos;
		this.state = state;
	}
	
	public PacketAddDestroyBlockEffect()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos);
		buf.writeInt(Block.getId(state));
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		pos = buf.readBlockPos();
		state = Block.stateById(buf.readInt());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		Optional.ofNullable(Minecraft.getInstance().level)
				.ifPresent(lvl -> lvl.addDestroyBlockEffect(pos, state));
	}
}
