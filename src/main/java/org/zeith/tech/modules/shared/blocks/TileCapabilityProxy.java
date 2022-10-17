package org.zeith.tech.modules.shared.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.tiles.TileSyncable;
import org.zeith.tech.modules.shared.init.TilesZT;

public class TileCapabilityProxy
		extends TileSyncable
{
	@NBTSerializable("Position")
	public BlockPos position;
	
	public TileCapabilityProxy(BlockPos pos, BlockState state)
	{
		super(TilesZT.CAPABILITY_PROXY, pos, state);
	}
	
	public TileCapabilityProxy setPosition(BlockPos position)
	{
		this.position = position;
		return this;
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(position != null && level.isLoaded(position))
		{
			var be = level.getBlockEntity(position);
			if(be != null)
			{
				var c = be.getCapability(cap, side);
				if(c.isPresent()) return c;
			}
		}
		
		return super.getCapability(cap, side);
	}
}