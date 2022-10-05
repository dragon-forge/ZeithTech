package org.zeith.tech.modules.transport.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.hammerlib.net.Network;
import org.zeith.tech.api.tile.energy.IEnergyMeasurable;
import org.zeith.tech.modules.transport.container.ContainerMultimeter;
import org.zeith.tech.modules.transport.net.PacketUpdateMultimeterLoad;

import javax.annotation.Nullable;

public class ItemMultimeter
		extends Item
{
	private static float lastClientLoad = 0;
	
	public ItemMultimeter(Properties props)
	{
		super(props);
	}
	
	public static float getLoadFromLook(Player player)
	{
		return 0.25F + lastClientLoad * 0.5F;
	}
	
	public static void handleClient(PacketUpdateMultimeterLoad pkt)
	{
		lastClientLoad = pkt.getLoad();
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean inHand)
	{
		if(entity instanceof ServerPlayer player)
		{
			if(inHand)
			{
				if(level.getGameTime() % 2L == 0L)
				{
					double d0 = player.getReachDistance();
					var hitResult = player.pick(d0, 1F, false);
					
					float load = 0;
					
					if(hitResult instanceof BlockHitResult blockHit)
					{
						var measurable = IEnergyMeasurable.get(level, blockHit.getBlockPos())
								.resolve()
								.orElse(null);
						
						if(measurable != null)
							load = measurable.getLoad();
						else
						{
							var be = level.getBlockEntity(blockHit.getBlockPos());
							if(be != null)
							{
								var energy = be.getCapability(ForgeCapabilities.ENERGY).resolve().orElse(null);
								if(energy != null && energy.getMaxEnergyStored() > 0)
								{
									load = energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
								}
							}
						}
					}
					
					Network.sendTo(new PacketUpdateMultimeterLoad(load), player);
					
					stack.getOrCreateTag().putBoolean("InHand", true);
				}
			} else
			{
				CompoundTag tags = stack.getTag();
				if(tags != null && tags.contains("InHand") && tags.getBoolean("InHand"))
				{
					stack.removeTagKey("InHand");
					Network.sendTo(new PacketUpdateMultimeterLoad(0F), player);
				}
			}
		}
		
		super.inventoryTick(stack, level, entity, slot, inHand);
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