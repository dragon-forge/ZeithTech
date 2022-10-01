package org.zeith.tech.modules.processing.blocks.machine_assembler.basic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.IPacket;
import org.zeith.hammerlib.net.PacketContext;

public class PktMABStartCrafting
		implements IPacket
{
	protected BlockPos pos;
	
	public PktMABStartCrafting(BlockPos pos)
	{
		this.pos = pos;
	}
	
	public PktMABStartCrafting()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		pos = buf.readBlockPos();
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		var player = ctx.getSender();
		
		if(player.containerMenu instanceof ContainerMachineAssemblerB menu && menu.tile != null)
			if(menu.tile.performCraftOperation())
				ctx.withReply(this);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		// We started a craft successfully. Play a sound, maybe.
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
	}
}