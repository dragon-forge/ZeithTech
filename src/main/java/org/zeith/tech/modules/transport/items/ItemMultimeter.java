package org.zeith.tech.modules.transport.items;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.tech.api.tile.energy.IEnergyMeasurable;
import org.zeith.tech.modules.transport.container.ContainerMultimeter;

import javax.annotation.Nullable;

public class ItemMultimeter
		extends Item
{
	public ItemMultimeter(Properties props)
	{
		super(props);
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		var lvl = context.getLevel();
		var pos = context.getClickedPos();
		
		return IEnergyMeasurable.get(lvl, pos).resolve().map(measurable ->
		{
			openContainerTile(context.getPlayer(), measurable);
			return InteractionResult.SUCCESS;
		}).orElse(InteractionResult.PASS);
	}
	
	public <T extends BlockEntity & IContainerTile> void openContainerTile(Player player, IEnergyMeasurable tile)
	{
		if(player instanceof ServerPlayer && tile != null && tile.getMeasurablePosition() != null)
			NetworkHooks.openScreen((ServerPlayer) player, forTile(tile), buf -> buf.writeBlockPos(tile.getMeasurablePosition()));
	}
	
	public MenuProvider forTile(IEnergyMeasurable tile)
	{
		return new MenuProvider()
		{
			@Override
			public Component getDisplayName()
			{
				return getDescription();
			}
			
			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player)
			{
				return new ContainerMultimeter(windowId, playerInv, new ContainerMultimeter.MultimeterData(tile, player.level.isClientSide));
			}
		};
	}
}