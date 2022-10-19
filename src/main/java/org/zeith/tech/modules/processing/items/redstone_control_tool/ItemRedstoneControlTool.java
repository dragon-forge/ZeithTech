package org.zeith.tech.modules.processing.items.redstone_control_tool;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.tile.RedstoneControl;

public class ItemRedstoneControlTool
		extends Item
{
	public ItemRedstoneControlTool(Properties props)
	{
		super(props);
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		var lvl = context.getLevel();
		var pos = context.getClickedPos();
		var be = lvl.getBlockEntity(pos);
		
		if(be != null)
			return be.getCapability(ZeithTechCapabilities.REDSTONE_CONTROL)
					.resolve()
					.map(control ->
					{
						openRedstoneControl(context.getPlayer(), pos, control, be instanceof Nameable n ? n.getDisplayName() : be.getBlockState().getBlock().getName());
						return InteractionResult.SUCCESS;
					}).orElse(InteractionResult.PASS);
		
		return InteractionResult.PASS;
	}
	
	public <T extends BlockEntity & IContainerTile> void openRedstoneControl(Player player, BlockPos pos, RedstoneControl tile, Component tileName)
	{
		if(player instanceof ServerPlayer && tile != null && pos != null)
			NetworkHooks.openScreen((ServerPlayer) player, forMeasurable(pos, tile, tileName), buf -> buf.writeBlockPos(pos));
	}
	
	public MenuProvider forMeasurable(BlockPos pos, RedstoneControl tile, Component tileName)
	{
		return new SimpleMenuProvider((windowId, playerInv, player) -> new ContainerRedstoneControl(windowId, playerInv, new ContainerRedstoneControl.RedstoneModeData(tile, pos)), tileName);
	}
}