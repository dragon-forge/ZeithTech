package org.zeith.tech.modules.transport.blocks.energy_wire;

import net.minecraft.core.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.energy.EnergyBalancingHelper;
import org.zeith.tech.modules.transport.blocks.base.traversable.ITraversable;
import org.zeith.tech.modules.transport.blocks.base.traversable.TraversableHelper;
import org.zeith.tech.utils.FEChargeWithLosses;

import java.util.function.Function;

public record WireEnergyHandler(Direction from, TileEnergyWire wire)
		implements IEnergyStorage
{
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		maxReceive = Math.min(maxReceive, wire.getWireProps().tier().maxFE());
		
		var charge = new FEChargeWithLosses(maxReceive);
		var paths = TraversableHelper.findAllPaths(wire, from, charge);
		
		Function<Integer, IEnergyStorage> targets = i ->
		{
			var path = paths.get(i);
			return Cast.optionally(path.lastElement(), TileEnergyWire.class)
					.flatMap(w -> w.relativeEnergyHandler(path.endpoint.dir()).resolve())
					.orElse(null);
		};
		
		var receives = EnergyBalancingHelper.balanceOut(targets, paths.size(), charge.getFE());
		
		if(!simulate)
		{
			int[] sendToPath = receives.balanced();
			for(int i = 0; i < sendToPath.length; ++i)
			{
				var path = paths.get(i);
				float loss = 0F;
				
				for(int j = 0; j < path.size(); j++)
				{
					ITraversable<FEChargeWithLosses> component = path.get(j);
					
					if(component instanceof TileEnergyWire rem)
					{
						rem.energyPassed += sendToPath[i];
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