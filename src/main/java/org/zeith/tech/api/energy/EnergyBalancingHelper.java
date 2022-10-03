package org.zeith.tech.api.energy;

import net.minecraftforge.energy.IEnergyStorage;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EnergyBalancingHelper
{
	public record BalanceResult(int[] balanced, int leftover)
	{
	}
	
	public static BalanceResult balanceOut(Function<Integer, IEnergyStorage> storages, BiFunction<Integer, Integer, Float> needExtra, int size, int sendFE)
	{
		// TODO: I NEED HELP WITH BALANCING OUT ENERGY, WHILE BEING AS EFFICIENT AS POSSBLE, SEND HELP!!!
		
		if(size == 1)
		{
			IEnergyStorage es = storages.apply(0);
			int rec = es != null ? es.receiveEnergy(sendFE, true) : 0;
			if(rec > 0)
				rec += Math.ceil(needExtra.apply(0, rec));
			rec = Math.min(rec, sendFE);
			return new BalanceResult(new int[] { rec }, sendFE - rec);
		}
		
		final int totalFE = sendFE;
		int[] balanced = new int[size];
		
		List<IEnergyStorage> storagesLst = new ArrayList<>(size);
		List<StorageInfo> inf = new ArrayList<>(size);
		
		for(int i = 0; i < size; ++i)
		{
			IEnergyStorage storage = storages.apply(i);
			storagesLst.add(storage);
			if(storage != null && storage.canReceive())
			{
				int rec = storage.receiveEnergy(totalFE, true);
				inf.add(new StorageInfo(storage, rec));
			}
		}
		
		inf.sort(Comparator.comparingInt(StorageInfo::maxReceive));
		
		for(int i = 0; i < inf.size() && sendFE > 0; i++)
		{
			StorageInfo info = inf.get(i);
			
			int idx = storagesLst.indexOf(info.storage);
			if(idx >= 0)
			{
				int rec = info.maxReceive / inf.size();
				if(rec > 0)
					balanced[idx] = Math.min(sendFE, (int) Math.ceil(rec + needExtra.apply(idx, rec)));
				sendFE -= balanced[idx];
			}
		}
		
		for(int i = inf.size() - 1; i >= 0 && sendFE > 0; i--)
		{
			StorageInfo info = inf.get(i);
			
			int idx = storagesLst.indexOf(info.storage);
			if(idx >= 0)
			{
				int rec = info.maxReceive;
				if(rec > 0)
				{
					int ob = balanced[idx];
					balanced[idx] = Math.min(rec, balanced[idx] + Math.min(rec, sendFE));
					sendFE -= balanced[idx] - ob;
				}
			}
		}
		
		return new BalanceResult(balanced, sendFE);
	}
	
	private record StorageInfo(IEnergyStorage storage, int maxReceive)
	{
	}
}