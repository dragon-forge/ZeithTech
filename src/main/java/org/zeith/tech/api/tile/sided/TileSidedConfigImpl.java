package org.zeith.tech.api.tile.sided;

import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.utils.ISidedItemAccess;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TileSidedConfigImpl
		implements ITileSidedConfig, INBTSerializable<CompoundTag>
{
	static final SidedConfigTyped[] ALL_TYPES = SidedConfigTyped.values();
	
	public final SpecificSidedConfigManager energy, fluids, items;
	
	public TileSidedConfigImpl(DirectStorage<Direction> currentDirection, EnumSet<SidedConfigTyped> types)
	{
		this.energy = types.contains(SidedConfigTyped.ENERGY) ? new SpecificSidedConfigManager(currentDirection) : null;
		this.fluids = types.contains(SidedConfigTyped.FLUID) ? new SpecificSidedConfigManager(currentDirection) : null;
		this.items = types.contains(SidedConfigTyped.ITEM) ? new SpecificSidedConfigManager(currentDirection) : null;
	}
	
	public TileSidedConfigImpl(Supplier<Direction> currentDirection, EnumSet<SidedConfigTyped> types)
	{
		this(DirectStorage.readonly(currentDirection), types);
	}
	
	public TileSidedConfigImpl setDefaults(SidedConfigTyped type, SideConfig config)
	{
		var cfgs = getSideConfigs(type);
		if(cfgs != null) cfgs.setDefaults(config);
		return this;
	}
	
	public TileSidedConfigImpl setForAll(RelativeDirection dir, SideConfig config)
	{
		for(var t : ALL_TYPES)
		{
			var cfgs = getSideConfigs(t);
			if(cfgs != null) cfgs.setDefaults(config);
		}
		return this;
	}
	
	@Override
	public ISpecificSidedConfig getSideConfigs(SidedConfigTyped type)
	{
		return switch(type)
				{
					case ENERGY -> energy;
					case FLUID -> fluids;
					case ITEM -> items;
					default -> null;
				};
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		var nbt = new CompoundTag();
		
		if(energy != null) nbt.put("energy", energy.serializeNBT());
		if(fluids != null) nbt.put("fluids", fluids.serializeNBT());
		if(items != null) nbt.put("items", items.serializeNBT());
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		if(energy != null && nbt.contains("energy", Tag.TAG_SHORT)) energy.deserializeNBT(ShortTag.valueOf(nbt.getShort("energy")));
		if(fluids != null && nbt.contains("fluids", Tag.TAG_SHORT)) fluids.deserializeNBT(ShortTag.valueOf(nbt.getShort("fluids")));
		if(items != null && nbt.contains("items", Tag.TAG_SHORT)) items.deserializeNBT(ShortTag.valueOf(nbt.getShort("items")));
	}
	
	static final int[] EMPTY_I_ARRAY = new int[0];
	
	public ISidedItemAccess createItemAccess(int[] input, int[] output)
	{
		Arrays.sort(input);
		Arrays.sort(output);
		
		int[] allSlots = Stream.of(input, output).flatMapToInt(IntStream::of).distinct().sorted().toArray();
		
		return new ISidedItemAccess()
		{
			@Override
			public int[] getSlotsForFace(Direction face)
			{
				return switch(getAccess(SidedConfigTyped.ITEM, face))
						{
							case DISABLE -> EMPTY_I_ARRAY;
							case NONE -> allSlots;
							case PUSH -> output;
							case PULL -> input;
						};
			}
			
			@Override
			public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction face)
			{
				return Arrays.binarySearch(input, slot) >= 0;
			}
			
			@Override
			public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face)
			{
				return Arrays.binarySearch(output, slot) >= 0;
			}
		};
	}
}