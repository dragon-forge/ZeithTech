package org.zeith.tech.modules.shared.blocks.multiblock_part;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import org.zeith.tech.api.block.ZeithTechStateProperties;
import org.zeith.tech.api.tile.multiblock.IMultiblockTile;
import org.zeith.tech.api.utils.BlockUpdateEmitter;
import org.zeith.tech.api.utils.CodecHelper;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.TilesZT;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class TileMultiBlockPart
		extends TileSyncableTickable
{
	public static final ModelProperty<BlockState> SUB_STATE = new ModelProperty<>();
	
	@NBTSerializable("Origin")
	public BlockPos origin = BlockPos.ZERO;
	
	public BlockState subState;
	public CompoundTag subTileData;
	
	private int stateId;
	public PropertyInt subStateSynced = new PropertyInt(DirectStorage.create(v ->
	{
		stateId = v;
		subState = Block.stateById(v);
		
		requestModelDataUpdate();
		BlockUpdateEmitter.blockUpdated(level, worldPosition);
	}, () -> stateId));
	
	public TileMultiBlockPart(BlockPos pos, BlockState state)
	{
		super(TilesZT.MULTIBLOCK_PART, pos, state);
		dispatcher.registerProperty("state", subStateSynced);
	}
	
	@Override
	public void update()
	{
		if(isOnServer() && atTickRate(40) && getOrigin() != null && level.isLoaded(getOrigin()) && findMultiBlock().isEmpty())
			unwrap(level, worldPosition);
		if(isOnServer() && subState != null && atTickRate(2))
			subStateSynced.setInt(Block.getId(subState));
	}
	
	public Optional<IMultiblockTile> findMultiBlock()
	{
		return Optional.ofNullable(getOrigin())
				.map(level::getBlockEntity)
				.flatMap(t -> Cast.optionally(t, IMultiblockTile.class))
				.filter(IMultiblockTile::isMultiblockValid);
	}
	
	@Override
	public CompoundTag writeNBT(CompoundTag nbt)
	{
		nbt = super.writeNBT(nbt);
		nbt.put("SubTile", subTileData);
		nbt.put("Sub", CodecHelper.encodeCompound(BlockState.CODEC, subState));
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundTag nbt)
	{
		this.subTileData = nbt.getCompound("SubTile");
		this.subState = CodecHelper.decodeCompound(BlockState.CODEC, nbt.get("Sub"));
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
	
	public void setVisible(boolean visible)
	{
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ZeithTechStateProperties.VISIBLE, visible));
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
	
	public BlockPos getOrigin()
	{
		return origin != null ? worldPosition.offset(origin) : null;
	}
	
	public void setOrigin(BlockPos origin)
	{
		this.origin = origin != null ? origin.subtract(worldPosition) : null;
	}
	
	public static BlockState getPartState(Level level, BlockPos pos)
	{
		if(level == null) return Blocks.AIR.defaultBlockState();
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
	
	public static void wrap(Level level, BlockPos rel, BlockPos origin, boolean visible)
	{
		// Skip empty blocks
		if(level.isEmptyBlock(rel)) return;
		
		var state = getPartState(level, rel);
		
		var tileData = Optional.ofNullable(level.getBlockEntity(rel))
				.filter(((Predicate<BlockEntity>) TileMultiBlockPart.class::isInstance).negate())
				.map(BlockEntity::serializeNBT)
				.orElseGet(CompoundTag::new);
		
		level.setBlockAndUpdate(rel, BlocksZT.MULTIBLOCK_PART.defaultBlockState().setValue(ZeithTechStateProperties.VISIBLE, visible));
		
		TileMultiBlockPart part;
		if(level.getBlockEntity(rel) instanceof TileMultiBlockPart p) part = p;
		else level.setBlockEntity(part = new TileMultiBlockPart(rel, BlocksZT.MULTIBLOCK_PART.defaultBlockState()));
		
		part.setOrigin(origin.immutable());
		part.subTileData = tileData;
		part.setSubState(state);
		part.sync();
	}
}