package org.zeith.tech.modules.shared.blocks.multiblock_part;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.tech.modules.shared.init.TilesZT;

public class TileMultiBlockPart
		extends TileSyncableTickable
{
	@NBTSerializable("Origin")
	public BlockPos origin = BlockPos.ZERO;
	
	public BlockState subState;
	
	public TileMultiBlockPart(BlockPos pos, BlockState state)
	{
		super(TilesZT.MULTIBLOCK_PART, pos, state);
	}
	
	@Override
	public CompoundTag writeNBT(CompoundTag nbt)
	{
		nbt = super.writeNBT(nbt);
		nbt.put("Sub", BlockState.CODEC.encode(subState, NbtOps.INSTANCE, new CompoundTag()).get().left().orElse(new CompoundTag()));
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundTag nbt)
	{
		this.subState = BlockState.CODEC.decode(NbtOps.INSTANCE, nbt.getCompound("Sub")).result().orElseThrow().getFirst();
		super.readNBT(nbt);
	}
}