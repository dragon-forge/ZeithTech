package org.zeith.tech.api.tile.energy;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;
import org.zeith.tech.api.capabilities.ZeithTechCapabilities;

import java.util.function.BooleanSupplier;

@AutoRegisterCapability
public interface IEnergyMeasurable
{
	void addListener(EnergyMeasureListener listener);
	
	static LazyOptional<IEnergyMeasurable> get(Level level, BlockPos pos)
	{
		var be = level.getBlockEntity(pos);
		if(be instanceof IEnergyMeasurable m) return LazyOptional.of(() -> m);
		return be != null ? be.getCapability(ZeithTechCapabilities.ENERGY_MEASURABLE) : LazyOptional.empty();
	}
	
	BlockPos getMeasurablePosition();
	
	record EnergyMeasureListener(FloatConsumer generate, FloatConsumer transfer, FloatConsumer usage, BooleanSupplier stillValid)
	{
		public void notifyGenerate(float FE)
		{
			if(generate != null)
				generate.accept(FE);
		}
		
		public void notifyTransfer(float FE)
		{
			if(transfer != null)
				transfer.accept(FE);
		}
		
		public void notifyUsage(float FE)
		{
			if(usage != null)
				usage.accept(FE);
		}
		
		public boolean invalid()
		{
			return !stillValid.getAsBoolean();
		}
	}
}