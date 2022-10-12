package org.zeith.tech.modules.shared.blocks.aux_io_port;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.api.wrench.IWrenchable;
import org.zeith.hammerlib.tiles.TileSyncable;
import org.zeith.tech.api.misc.RecursionGuard;
import org.zeith.tech.api.tile.slots.*;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.init.TilesZT;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TileAuxiliaryIOPort
		extends TileSyncable
		implements ITileSlotProvider, IWrenchable
{
	public static final ModelProperty<Map<Direction, IOSlotPurpose>> IO_PORT_DATA = new ModelProperty<>();
	
	public final Map<Direction, UUID> slotMap = new HashMap<>();
	
	public final Map<Direction, IOSlotPurpose> purposes = new HashMap<>();
	
	public TileAuxiliaryIOPort(BlockPos pos, BlockState state)
	{
		super(TilesZT.AUXILIARY_IO_PORT, pos, state);
	}
	
	@Override
	public CompoundTag writeNBT(CompoundTag nbt)
	{
		nbt.merge(super.writeNBT(nbt));
		
		CompoundTag sides = new CompoundTag();
		for(Direction dir : slotMap.keySet())
			sides.putUUID(dir.getSerializedName(), slotMap.get(dir));
		nbt.put("Sides", sides);
		
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundTag nbt)
	{
		super.readNBT(nbt);
		
		slotMap.clear();
		var sides = nbt.getCompound("Sides");
		for(var key : sides.getAllKeys())
		{
			var dir = Direction.byName(key);
			if(dir == null)
			{
				ZeithTech.LOG.warn("Found unknown direction key in TileAuxiliaryIOPort: " + key + "; Ignoring.");
				continue;
			}
			slotMap.put(dir, sides.getUUID(key));
		}
		
		if(isOnClient())
		{
			requestModelDataUpdate();
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockAuxiliaryIOPort.ALT, !getBlockState().getValue(BlockAuxiliaryIOPort.ALT)));
		}
	}
	
	@Override
	public @NotNull ModelData getModelData()
	{
		resolveSlots();
		return ModelData.builder()
				.with(IO_PORT_DATA, purposes)
				.build();
	}
	
	public synchronized void resolveSlots()
	{
		purposes.clear();
		
		if(slotMap.isEmpty())
			return;
		
		var slots = getSlots();
		
		for(Direction dir : Direction.values())
		{
			if(!slotMap.containsKey(dir)) continue;
			
			var uuid = slotMap.get(dir);
			if(uuid == null)
			{
				slotMap.remove(dir);
				continue;
			}
			
			var slot = findSlot(slots, uuid).orElse(null);
			if(slot == null)
			{
				slotMap.remove(dir);
				continue;
			}
			
			purposes.put(dir, new IOSlotPurpose(slot.getType(), slot.getRole(), slot.getColor()));
		}
	}
	
	private final AtomicBoolean recursion = new AtomicBoolean();
	
	@Override
	public List<ISlot<?>> getSlots()
	{
		try(var guard = new RecursionGuard(recursion))
		{
			return guard.callOrDefault(
					() -> ITileSlotProvider.getSlotsAt(level, worldPosition.above(), Direction.DOWN).map(ITileSlotProvider::getSlots).orElse(List.of()),
					List.of()
			);
		}
	}
	
	public Optional<ISlot<?>> findSlot(List<ISlot<?>> slots, UUID uuid)
	{
		return slots.stream().filter(s -> s.getUniqueIdentifier().equals(uuid)).findFirst();
	}
	
	public OptionalInt findSlotIndex(List<ISlot<?>> slots, UUID uuid)
	{
		return findSlot(slots, uuid)
				.map(slots::indexOf)
				.filter(i -> i >= 0)
				.map(OptionalInt::of)
				.orElse(OptionalInt.empty());
	}
	
	@Override
	public boolean onWrenchUsed(UseOnContext context)
	{
		var slots = getSlots();
		var side = context.getClickedFace();
		var backwards = context.getPlayer().isShiftKeyDown();
		
		if(slots.isEmpty())
		{
			if(slotMap.containsKey(side))
			{
				slotMap.remove(side);
				syncUpdateTagHLPipeline();
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockAuxiliaryIOPort.ALT, !getBlockState().getValue(BlockAuxiliaryIOPort.ALT)));
				return true;
			}
			
			return false;
		}
		
		if(!slotMap.containsKey(side))
		{
			if(isOnServer())
			{
				slotMap.put(side, slots.get(backwards ? slots.size() - 1 : 0).getUniqueIdentifier());
				syncUpdateTagHLPipeline();
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockAuxiliaryIOPort.ALT, !getBlockState().getValue(BlockAuxiliaryIOPort.ALT)));
			}
			
			return true;
		}
		
		if(isOnServer())
		{
			int next = findSlotIndex(slots, slotMap.get(side))
					.orElse(-1) + (backwards ? -1 : 1);
			
			if(next < 0 || next >= slots.size())
			{
				slotMap.remove(side);
				syncUpdateTagHLPipeline();
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockAuxiliaryIOPort.ALT, !getBlockState().getValue(BlockAuxiliaryIOPort.ALT)));
				return true;
			}
			
			slotMap.put(side, slots.get(next).getUniqueIdentifier());
			syncUpdateTagHLPipeline();
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockAuxiliaryIOPort.ALT, !getBlockState().getValue(BlockAuxiliaryIOPort.ALT)));
		}
		
		return true;
	}
	
	public record IOSlotPurpose(SlotType<?> type, SlotRole role, Color color)
	{
		public ResourceLocation getTexture()
		{
			return type.getTextures().forRole(role);
		}
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		var slots = getSlots();
		
		if(side == null)
			return Direction.stream().map(dir -> getRecursiveCapability(slots, cap, dir))
					.filter(LazyOptional::isPresent)
					.findFirst()
					.orElseGet(() -> super.getCapability(cap, null));
		
		var slot = getRecursiveCapability(slots, cap, side);
		if(slot.isPresent()) return slot;
		
		return super.getCapability(cap, side);
	}
	
	protected <T> LazyOptional<T> getRecursiveCapability(List<ISlot<?>> slots, Capability<T> cap, Direction side)
	{
		if(slotMap.containsKey(side))
			return findSlot(slots, slotMap.get(side))
					.flatMap(s -> s.getSlotAccess(s.getType()))
					.map(a -> a.getCapability(cap))
					.orElse(LazyOptional.empty());
		return LazyOptional.empty();
	}
}