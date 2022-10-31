package org.zeith.tech.modules.shared.blocks.multiblock_part;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.tile.multiblock.IMultiblockTile;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.TilesZT;

import java.util.Objects;
import java.util.Optional;

public class TileMultiBlockPart
		extends TileSyncableTickable
{
	public static final ModelProperty<BlockState> SUB_STATE = new ModelProperty<>();
	
	@NBTSerializable("Origin")
	public BlockPos origin = BlockPos.ZERO;
	
	public BlockState subState;
	
	private int stateId;
	public PropertyInt subStateSynced = new PropertyInt(DirectStorage.create(v ->
	{
		stateId = v;
		subState = Block.stateById(v);
		
		requestModelDataUpdate();
		
		if(hasLevel())
		{
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
			level.blockUpdated(worldPosition, getBlockState().getBlock());
			Block.updateFromNeighbourShapes(getBlockState(), level, worldPosition);
		}
	}, () -> stateId));
	
	public TileMultiBlockPart(BlockPos pos, BlockState state)
	{
		super(TilesZT.MULTIBLOCK_PART, pos, state);
		dispatcher.registerProperty("state", subStateSynced);
	}
	
	@Override
	public void update()
	{
		if(isOnServer() && atTickRate(40) && origin != null && level.isLoaded(origin) && findMultiBlock().isEmpty())
			unwrap(level, worldPosition);
		if(isOnServer() && subState != null && atTickRate(2))
			subStateSynced.setInt(Block.getId(subState));
	}
	
	public Optional<IMultiblockTile> findMultiBlock()
	{
		return Optional.ofNullable(origin)
				.map(level::getBlockEntity)
				.flatMap(t -> Cast.optionally(t, IMultiblockTile.class))
				.filter(IMultiblockTile::isMultiblockValid);
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
		requestModelDataUpdate();
		super.readNBT(nbt);
	}
	
	public void setSubState(BlockState repaired)
	{
		if(!Objects.equals(subState, repaired))
		{
			subState = repaired;
			requestModelDataUpdate();
			sync();
		}
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		return findMultiBlock()
				.map(tile -> tile.getCapability(worldPosition, cap, side))
				.orElseGet(() -> super.getCapability(cap, side));
	}
	
	@Override
	public @NotNull ModelData getModelData()
	{
		return ModelData.builder()
				.with(SUB_STATE, subState)
				.build();
	}
	
	public static BlockState getPartState(Level level, BlockPos pos)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart p && p.subState != null)
			return p.subState;
		return level.getBlockState(pos);
	}
	
	public static void unwrap(Level level, BlockPos pos)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part)
		{
			level.removeBlockEntity(pos);
			level.setBlockAndUpdate(pos, part.subState);
		}
	}
	
	public static void wrap(Level level, BlockPos rel, BlockPos origin)
	{
		// Skip empty blocks
		if(level.isEmptyBlock(rel)) return;
		
		var state = getPartState(level, rel);
		
		level.setBlockAndUpdate(rel, BlocksZT.MULTIBLOCK_PART.defaultBlockState());
		
		TileMultiBlockPart part;
		if(level.getBlockEntity(rel) instanceof TileMultiBlockPart p) part = p;
		else level.setBlockEntity(part = new TileMultiBlockPart(rel, BlocksZT.MULTIBLOCK_PART.defaultBlockState()));
		
		part.origin = origin.immutable();
		part.subState = state;
		part.sync();
	}
}