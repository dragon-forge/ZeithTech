package org.zeith.tech.modules.transport.blocks.energy_wire;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.energy.EnergyBalancingHelper;
import org.zeith.tech.modules.transport.blocks.base.traversable.ITraversable;
import org.zeith.tech.modules.transport.blocks.base.traversable.TraversableHelper;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public record WireEnergyHandler(Direction from, TileEnergyWire wire)
		implements IEnergyStorage
{
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		maxReceive = Math.min(maxReceive, wire.getWireProps().tier().maxFE());
		if(maxReceive <= 0)
			return 0;
		
		var charge = new FECharge(maxReceive);
		var paths = TraversableHelper.findAllPaths(wire, from, charge);
		
		Function<Integer, IEnergyStorage> targets = i ->
		{
			var path = paths.get(i);
			return Cast.optionally(path.lastElement(), TileEnergyWire.class)
					.flatMap(w -> w.relativeEnergyHandler(path.endpoint.dir()).resolve())
					.orElse(null);
		};
		
		Map<Integer, Float> lossMap = new HashMap<>();
		BiFunction<Integer, Integer, Float> lossByPath = (i, sendToPath) ->
		{
			if(lossMap.containsKey(i)) return lossMap.get(i);
			var path = paths.get(i);
			float loss = 0F;
			for(var comp : path)
				if(comp instanceof TileEnergyWire rem)
					loss += rem.getWireProps().energyLoss();
			lossMap.put(i, loss);
			return loss;
		};
		
		var receives = EnergyBalancingHelper.balanceOut(targets, lossByPath, paths.size(), charge.FE);
		
		if(!simulate)
		{
			Set<BlockPos> lost = new HashSet<>();
			
			int[] sendToPath = receives.balanced();
			for(int i = 0; i < sendToPath.length; ++i)
			{
				var path = paths.get(i);
				float loss = 0F;
				for(int j = 0; j < path.size() && sendToPath[i] > 0; j++)
				{
					ITraversable<FECharge> component = path.get(j);
					if(component instanceof TileEnergyWire rem)
					{
						rem.energyPassed += sendToPath[i] - loss;
						
						if(lost.add(rem.getPosition()))
							loss += rem.getWireProps().energyLoss();
						
						float rec = Math.max(0F, sendToPath[i] - loss);
						if(j == path.size() - 1 && rec > 0)
							rem.emitTo(path.endpoint.dir(), rec);
					}
				}
			}
		}
		
		return maxReceive - receives.leftover();
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return wire.contents.extractEnergy(maxExtract, simulate);
	}
	
	@Override
	public int getEnergyStored()
	{
		return wire.contents.getEnergyStored();
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return wire.contents.getMaxEnergyStored();
	}
	
	@Override
	public boolean canExtract()
	{
		return true;
	}
	
	@Override
	public boolean canReceive()
	{
		return true;
	}
}