package org.zeith.tech.api.energy;

import net.minecraftforge.energy.IEnergyStorage;

import java.util.*;
import java.util.function.Function;

public class EnergyBalancingHelper
{
	public record BalanceResult(int[] balanced, int leftover)
	{
	}
	
	public static BalanceResult balanceOut(Function<Integer, IEnergyStorage> storages, int size, int sendFE)
	{
		if(size == 1)
		{
			IEnergyStorage es = storages.apply(0);
			int rec = es != null ? es.receiveEnergy(sendFE, true) : 0;
			return new BalanceResult(new int[] { rec }, sendFE - rec);
		}
		
		final int totalFE = sendFE;
		int[] balanced = new int[size];
		
		List<IEnergyStorage> storagesLst = new ArrayList<>(size);
		List<StorageInfo> inf = new ArrayList<>(size);
		
		double avg = 0;
		int avgC = 0;
		
		for(int i = 0; i < size; ++i)
		{
			IEnergyStorage storage = storages.apply(i);
			storagesLst.add(storage);
			if(storage != null && storage.canReceive())
			{
				int rec = storage.receiveEnergy(totalFE, true);
				avg += rec;
				++avgC;
				inf.add(new StorageInfo(storage, rec));
			}
		}
		
		if(avgC > 0)
			avg /= avgC;
		
		inf.sort(Comparator.comparingInt(StorageInfo::maxReceive));
		
		for(int i = 0; i < inf.size(); i++)
		{
			StorageInfo info = inf.get(i);
			
			int idx = storagesLst.indexOf(info.storage);
			if(idx >= 0)
			{
				int rec = Math.min(info.maxReceive, sendFE);
				
				if(info.maxReceive >= avg)
				{
					int left = inf.size() - i + 1;
					rec /= left;
				}
				
				balanced[idx] = rec;
				sendFE -= rec;
			}
		}
		
		return new BalanceResult(balanced, sendFE);
	}
	
	private record StorageInfo(IEnergyStorage storage, int maxReceive)
	{
	}
}