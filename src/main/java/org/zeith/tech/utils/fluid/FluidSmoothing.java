package org.zeith.tech.utils.fluid;

import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import org.zeith.hammerlib.api.io.IAutoNBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyFluidStack;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.modules.transport.blocks.fluid_pipe.PipeFluidHandler;

import java.util.Arrays;

public class FluidSmoothing
		implements IAutoNBTSerializable
{
	public final TileSyncableTickable tile;
	
	private FluidStack _displayFluid = FluidStack.EMPTY, _prevDisplayFluid = FluidStack.EMPTY;
	
	private FluidStack _syncFluid = FluidStack.EMPTY;
	
	private final PropertyFluidStack displayFluid;
	
	public FluidSmoothing(String base, TileSyncableTickable tile)
	{
		this.tile = tile;
		
		displayFluid = new PropertyFluidStack(DirectStorage.create(v ->
		{
			if(tile.isOnClient())
			{
				clientSyncTicks = syncTickRate;
				_prevDisplayFluid = getClientAverage(1F);
				_displayFluid = v.copy();
				clientSyncTicks = 0;
				hasReceivedFluid = true;
			}
			_syncFluid = v;
		}, () -> _syncFluid));
		
		var dispatcher = tile.getProperties();
		dispatcher.registerProperty(base, displayFluid);
	}
	
	boolean hasReceivedFluid;
	final int syncTickRate = 5;
	int clientSyncTicks = 0;
	FluidStack[] combined = new FluidStack[syncTickRate];
	
	{
		Arrays.fill(combined, FluidStack.EMPTY);
	}
	
	public void update(FluidStack currentContent)
	{
		if(tile.isOnServer())
		{
			System.arraycopy(combined, 0, combined, 1, combined.length - 1);
			combined[0] = currentContent.copy();
			
			if(!hasReceivedFluid)
			{
				displayFluid.set(currentContent);
				displayFluid.markChanged(true);
				tile.syncProperties();
				hasReceivedFluid = true;
			} else if(tile.atTickRate(syncTickRate))
			{
				displayFluid.set(getServerAverage());
				displayFluid.markChanged(true);
				tile.syncProperties();
			}
		}
		
		if(tile.isOnClient())
		{
			if(!hasReceivedFluid && !currentContent.isEmpty() && _syncFluid.isEmpty())
			{
				clientSyncTicks = 0;
				_displayFluid = currentContent.copy();
				_prevDisplayFluid = _displayFluid;
				hasReceivedFluid = true;
			}
			
			++clientSyncTicks;
			if(clientSyncTicks >= syncTickRate)
				_prevDisplayFluid = _displayFluid.copy();
		}
	}
	
	public FluidStack getClientAverage(float partial)
	{
		var ofs = _prevDisplayFluid;
		var fs = _displayFluid;
		
		float progress = Mth.clamp((clientSyncTicks + partial) / syncTickRate, 0F, 1F);

//		ZeithTech.LOG.info(ofs.getDisplayName().getString() + " x " + ofs.getAmount() + " --" + progress + "--> " + fs.getDisplayName().getString() + " x " + fs.getAmount());
		
		var prevEmpty = ofs.isEmpty();
		var nowEmpty = fs.isEmpty();
		
		if(prevEmpty && !nowEmpty) // New fluid appears
			return PipeFluidHandler.withAmount(fs, Math.round(fs.getAmount() * progress));
		
		if(!prevEmpty && nowEmpty) // Fluid fully drained
			return PipeFluidHandler.withAmount(ofs, Math.round(ofs.getAmount() * (1 - progress)));
		
		return fs.isEmpty()
				? FluidStack.EMPTY
				: PipeFluidHandler.withAmount(fs, Math.round(Mth.lerp(progress, ofs.getAmount(), fs.getAmount())));
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