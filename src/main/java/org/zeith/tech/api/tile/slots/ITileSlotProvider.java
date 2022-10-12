package org.zeith.tech.api.tile.slots;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.zeith.tech.api.ZeithTechCapabilities;

import java.util.*;

@AutoRegisterCapability
public interface ITileSlotProvider
{
	default Map<Slot, ISlot<?>> mapGuiSlots(List<Slot> mcSlots)
	{
		ImmutableMap.Builder<Slot, ISlot<?>> builder = ImmutableMap.builder();
		var slots = getSlots();
		
		for(var mc : mcSlots)
		{
			for(ISlot<?> slot : slots)
			{
				if(slot.getType().equals(SlotType.ITEM))
				{
					var access = slot.getSlotAccess(SlotType.ITEM).orElse(null);
					if(access instanceof ContainerItemSlotAccess cisa && mc.container == cisa.container && mc.getContainerSlot() == cisa.slot)
					{
						builder.put(mc, slot);
						break;
					}
				}
			}
		}
		
		return builder.build();
	}
	
	List<ISlot<?>> getSlots();
	
	static Optional<ITileSlotProvider> getSlotsAt(Level level, BlockPos position, Direction from)
	{
		var be = level.getBlockEntity(position);
		if(be instanceof ITileSlotProvider provider)
			return Optional.of(provider);
		return be != null
				? be.getCapability(ZeithTechCapabilities.TILE_SLOT_PROVIDER, from).resolve()
				: Optional.empty();
	}
}