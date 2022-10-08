package org.zeith.tech.utils.fluid;

import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import org.zeith.hammerlib.api.io.IAutoNBTSerializable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyBool;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.core.net.properties.PropertyFluidStack;
import org.zeith.tech.core.net.properties.PropertyIntArray;
import org.zeith.tech.modules.transport.blocks.fluid_pipe.PipeFluidHandler;

import java.util.Arrays;

public class FluidSmoothing
		implements IAutoNBTSerializable
{
	public final TileSyncableTickable tile;
	
	@NBTSerializable("Display")
	private FluidStack _displayFluid = FluidStack.EMPTY;
	@NBTSerializable("PrevDisplay")
	private FluidStack _prevDisplayFluid = FluidStack.EMPTY;
	private final PropertyFluidStack prevDisplayFluid = new PropertyFluidStack(DirectStorage.create(v -> _prevDisplayFluid = v, () -> _prevDisplayFluid));
	private final PropertyFluidStack displayFluid;
	public final PropertyBool isBurning = new PropertyBool();
	public final PropertyIntArray sendingDirections = new PropertyIntArray();
	
	public FluidSmoothing(String base, TileSyncableTickable tile)
	{
		this.tile = tile;
		
		displayFluid = new PropertyFluidStack(DirectStorage.create(v ->
		{
			_displayFluid = v;
			if(tile.isOnClient())
				clientSyncTicks = 0;
		}, () -> _displayFluid));
		
		var dispatcher = tile.getProperties();
		dispatcher.registerProperty(base, displayFluid);
		dispatcher.registerProperty(base + "_prev", prevDisplayFluid);
	}
	
	final int syncTickRate = 5;
	int clientSyncTicks = 0;
	FluidStack[] combined = new FluidStack[syncTickRate];
	
	{
		Arrays.fill(combined, FluidStack.EMPTY);
	}
	
	public void update(FluidStack currentContent)
	{
		System.arraycopy(combined, 0, combined, 1, combined.length - 1);
		combined[0] = currentContent.copy();
		
		sync:
		if(tile.atTickRate(syncTickRate) && tile.isOnServer())
		{
			prevDisplayFluid.set(displayFluid.get());
			FluidStack fluid = getServerAverage();
			displayFluid.set(fluid);
		}
		
		if(tile.isOnClient())
			++clientSyncTicks;
	}
	
	public FluidStack getClientAverage(float partial)
	{
		var ofs = prevDisplayFluid.get();
		var fs = displayFluid.get();
		
		float progress = Mth.clamp((clientSyncTicks + partial) / syncTickRate, 0F, 1F);
		
		if(ofs.isEmpty() && !fs.isEmpty())
			return PipeFluidHandler.withAmount(fs, Math.round(fs.getAmount() * progress));
		
		if(!ofs.isEmpty() && fs.isEmpty())
			return PipeFluidHandler.withAmount(ofs, Math.round(ofs.getAmount() * progress));
		
		if(!ofs.isFluidEqual(fs) || fs.isEmpty())
			return fs;
		
		return PipeFluidHandler.withAmount(fs, Math.round(Mth.lerp(progress, ofs.getAmount(), fs.getAmount())));
	}
	
	public FluidStack getServerAverage()
	{
		FluidStack fluid = FluidStack.EMPTY;
		
		for(FluidStack fs : combined)
		{
			if(fs.isEmpty())
				continue;
			
			if(fluid.isEmpty())
				fluid = PipeFluidHandler.withAmount(fs, fs.getAmount());
			else if(fs.isFluidEqual(fluid))
				fluid.setAmount(fluid.getAmount() + fs.getAmount());
		}
		
		if(!fluid.isEmpty())
			fluid.setAmount(fluid.getAmount() / combined.length);
		
		return fluid;
	}
}