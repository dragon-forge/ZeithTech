package org.zeith.tech.api.tile.energy;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class EnergyMeasurableWrapper
		implements IEnergyMeasurable
{
	private final List<EnergyMeasureListener> measureListeners = new ArrayList<>(1);
	
	public final Supplier<BlockPos> position;
	
	private float generated, transferred, consumed;
	
	private final DoubleSupplier loadFactor;
	
	public EnergyMeasurableWrapper(Supplier<BlockPos> position, DoubleSupplier loadFactor)
	{
		this.position = position;
		this.loadFactor = loadFactor;
	}
	
	@Override
	public float getLoad()
	{
		return (float) loadFactor.getAsDouble();
	}
	
	public void update()
	{
		synchronized(measureListeners)
		{
			measureListeners.removeIf(listener ->
			{
				listener.notifyGenerate(generated);
				listener.notifyTransfer(transferred);
				listener.notifyUsage(consumed);
				return listener.invalid();
			});
		}
		
		generated = transferred = consumed = 0;
	}
	
	public void onEnergyGenerated(float FE)
	{
		generated += FE;
	}
	
	public void onEnergyTransfer(float FE)
	{
		transferred += FE;
	}
	
	public void onEnergyConsumed(float FE)
	{
		consumed += FE;
	}
	
	@Override
	public void addListener(EnergyMeasureListener listener)
	{
		synchronized(measureListeners)
		{
			measureListeners.add(listener);
		}
	}
	
	@Override
	public BlockPos getMeasurablePosition()
	{
		return position.get();
	}
}