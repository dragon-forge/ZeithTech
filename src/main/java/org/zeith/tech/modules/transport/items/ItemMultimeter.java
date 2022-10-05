package org.zeith.tech.modules.transport.items;

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
import java.util.*;

public class ItemMultimeter
		extends Item
{
	public static final UUID ZERO_ID = new UUID(0, 0);
	private static final Map<UUID, Float> loads = new HashMap<>();
	
	public ItemMultimeter(Properties props)
	{
		super(props);
	}
	
	public static float getLoadFromLook(ItemStack stack)
	{
		var tag = stack.getOrCreateTag();
		return 0.25F + loads.getOrDefault(tag.contains("Identity") ? tag.getUUID("Identity") : ZERO_ID, 0F) * 0.5F;
	}
	
	public static void handleClient(PacketUpdateMultimeterLoad pkt)
	{
		loads.put(pkt.getIdentity(), pkt.getLoad());
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean inHand)
	{
		var tags = stack.getOrCreateTag();
		if(!tags.contains("Identity") && level != null && !level.isClientSide)
			tags.putUUID("Identity", UUID.randomUUID());
		
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
					
					Network.sendTo(new PacketUpdateMultimeterLoad(load, stack), player);
					
					stack.getOrCreateTag().putBoolean("InHand", true);
				}
			} else
			{
				if(tags.getBoolean("InHand"))
				{
					stack.removeTagKey("InHand");
					Network.sendTo(new PacketUpdateMultimeterLoad(0F, stack), player);
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