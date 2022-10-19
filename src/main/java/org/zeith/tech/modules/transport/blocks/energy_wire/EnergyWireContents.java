package org.zeith.tech.modules.transport.blocks.energy_wire;

import net.minecraft.core.Direction;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyWireContents
		implements INBTSerializable<ListTag>, IEnergyStorage
{
	public final float[] energy = new float[6];
	
	public int max;
	
	public void add(Direction to, float fe)
	{
		energy[to.ordinal()] += fe;
	}
	
	public int emit(TileEnergyWire wire)
	{
		max = wire.getWireProps().tier().maxTransfer();
		
		int emit = 0;
		
		for(Direction dir : BlockEnergyWire.DIRECTIONS)
		{
			var fe = energy[dir.ordinal()];
			if(fe >= 1.0F)
			{
				int sent = wire.emitToDirect(dir, (int) fe, false);
				fe -= sent;
				emit += sent;
				energy[dir.ordinal()] = fe;
			}
		}
		
		return emit;
	}
	
	@Override
	public ListTag serializeNBT()
	{
		var lst = new ListTag();
		for(float i : energy) lst.add(FloatTag.valueOf(i));
		return lst;
	}
	
	@Override
	public void deserializeNBT(ListTag nbt)
	{
		var l = Math.min(nbt.size(), energy.length);
		for(int i = 0; i < l; ++i)
			energy[i] = nbt.getFloat(i);
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return 0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		int extracted = 0;
		for(int i = 0; i < energy.length && maxExtract > 0; i++)
		{
			if(energy[i] > 0)
			{
				var te = Math.min(maxExtract, energy[i]);
				
				extracted += te;
				maxExtract -= te;
				
				if(!simulate)
					energy[i] -= te;
			}
		}
		return extracted;
	}
	
	@Override
	public int getEnergyStored()
	{
		return (int) (energy[0] + energy[1] + energy[2] + energy[3] + energy[4] + energy[5]);
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return max * 6;
	}
	
	@Override
	public boolean canExtract()
	{
		return true;
	}
	
	@Override
	public boolean canReceive()
	{
		return false;
	}
}