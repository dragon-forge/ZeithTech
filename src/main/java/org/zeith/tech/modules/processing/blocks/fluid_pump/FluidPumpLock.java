package org.zeith.tech.modules.processing.blocks.fluid_pump;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidPumpLock
		implements INBTSerializable<CompoundTag>
{
	public Fluid type = Fluids.EMPTY;
	
	public boolean isLocked()
	{
		return type != Fluids.EMPTY;
	}
	
	public boolean lock(FluidState state)
	{
		if(!isLocked() && state.isSource())
		{
			this.type = state.getType();
			return true;
		}
		
		return false;
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		var nbt = new CompoundTag();
		nbt.putString("Type", ForgeRegistries.FLUIDS.getKey(type).toString());
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		type = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("Type")));
	}
	
	public boolean test(FluidState fluid)
	{
		return isLocked() && fluid.isSourceOfType(type);
	}
}