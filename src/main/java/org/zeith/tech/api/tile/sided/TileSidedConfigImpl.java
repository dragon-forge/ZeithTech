package org.zeith.tech.api.tile.sided;

import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.enums.SideConfig;
import org.zeith.tech.api.enums.SidedConfigTyped;

import java.util.EnumSet;
import java.util.function.Supplier;

public class TileSidedConfigImpl
		implements ITileSidedConfig, INBTSerializable<CompoundTag>
{
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
}