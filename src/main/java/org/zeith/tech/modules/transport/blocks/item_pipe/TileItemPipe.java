package org.zeith.tech.modules.transport.blocks.item_pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.enums.SideConfig;
import org.zeith.tech.api.tile.IHasPriority;
import org.zeith.tech.api.tile.facade.FacadeData;
import org.zeith.tech.api.tile.sided.SideConfig6;
import org.zeith.tech.modules.transport.blocks.base.traversable.*;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;
import org.zeith.tech.modules.transport.net.PacketMoveItemInPipe;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TileItemPipe
		extends TileSyncableTickable
		implements ITraversable<ItemInPipe>
{
	@NBTSerializable("Contents")
	public final ItemPipeContents contents = new ItemPipeContents();
	
	@NBTSerializable("Sides")
	public final SideConfig6 sideConfigs = new SideConfig6(SideConfig.NONE);
	
	@NBTSerializable("Facades")
	public final FacadeData facades = new FacadeData(() -> getPipeBlock().getShapeWithNoFacades(getBlockState()).toAabbs(), this::facadesUpdated);
	
	@NBTSerializable("Speed")
	public float speed;
	
	public TileItemPipe(BlockPos pos, BlockState state)
	{
		this(TilesZT_Transport.ITEM_PIPE, pos, state);
	}
	
	public TileItemPipe(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		this.speed = getPipeBlock().getPipeSpeed();
	}
	
	public BlockItemPipe getPipeBlock()
	{
		var state = getBlockState();
		if(state.getBlock() instanceof BlockItemPipe pipe)
			return pipe;
		return BlocksZT_Transport.COPPER_ITEM_PIPE;
	}
	
	public ItemStack insertItemIntoPipe(ItemStack stack, Direction from, boolean simulate)
	{
		return insertItemIntoPipe(stack, from, simulate, ItemInPipe.multiplySpeed(1F));
	}
	
	public ItemStack insertItemIntoPipe(ItemStack stack, Direction from, boolean simulate, Consumer<ItemInPipe> visitor)
	{
		ItemInPipe item = new ItemInPipe().setContents(stack);
		
		// Guaranteed to be unique.
		while(contents.byId(item.itemId).isPresent())
			item.itemId = UUID.randomUUID();
		
		var path = TraversableHelper.findClosestPath(this, from, item).orElse(null);
		
		if(path != null && item.applyPath(from, path))
		{
			if(!simulate)
			{
				visitor.accept(item);
				contents.add(item);
			}
			return ItemStack.EMPTY;
		}
		
		return stack;
	}
	
	public ItemStack insertItemIntoPipe(ItemStack stack, Direction from, boolean simulate, BlockPos endpointPos)
	{
		return insertItemIntoPipe(stack, from, simulate, ItemInPipe.multiplySpeed(1F), endpointPos);
	}
	
	public ItemStack insertItemIntoPipe(ItemStack stack, Direction from, boolean simulate, Consumer<ItemInPipe> visitor, BlockPos endpointPos)
	{
		ItemInPipe item = new ItemInPipe().setContents(stack);
		
		// Guaranteed to be unique.
		while(contents.byId(item.itemId).isPresent())
			item.itemId = UUID.randomUUID();
		
		var path = TraversableHelper.findClosestPath(this, from, item, endpointPos).orElse(null);
		
		if(path != null && item.applyPath(from, path))
		{
			if(!simulate)
			{
				visitor.accept(item);
				contents.add(item);
			}
			return ItemStack.EMPTY;
		}
		
		return stack;
	}
	
	public boolean transferItem(ItemInPipe item, Direction to)
	{
		if(level.getBlockEntity(worldPosition.relative(to)) instanceof TileItemPipe pipe)
		{
			item.prevPipeProgress = item.currentPipeProgress = 0;
			item.currentPos = pipe.getPosition();
			item.prevPos = getPosition();
			pipe.contents.addNow(item);
			
			PacketMoveItemInPipe.send(this, item, to);
			
			for(Direction dir : BlockItemPipe.DIRECTIONS)
				if(dir != to && level.getBlockEntity(worldPosition.relative(dir)) instanceof TileItemPipe rp)
					rp.contents.removeById(item.itemId);
			
			if(to.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
				item.update(pipe);
			
			return true;
		}
		
		return storeAnything(to, item, false);
	}
	
	public void facadesUpdated()
	{
		if(hasLevel())
		{
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
			level.blockUpdated(worldPosition, getBlockState().getBlock());
			Block.updateFromNeighbourShapes(getBlockState(), level, worldPosition);
		}
		
		if(isOnServer())
			sync();
		
		if(isOnClient())
			requestModelDataUpdate();
	}
	
	@Override
	public void update()
	{
		contents.update(this);
	}
	
	private final net.minecraftforge.common.util.LazyOptional<?>[] sidedItemHandlers =
			Direction.stream()
					.map(dir -> LazyOptional.of(() -> new PipeItemHandler(this, dir)))
					.toArray(LazyOptional[]::new);
	
	private final LazyOptional<FacadeData> facadesCap = LazyOptional.of(() -> facades);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(side != null && cap == ForgeCapabilities.ITEM_HANDLER && sideConfigs.get(side.ordinal()) != SideConfig.DISABLE) return sidedItemHandlers[side.ordinal()].cast();
		if(cap == ZeithTechCapabilities.FACADES) return facadesCap.cast();
		return super.getCapability(cap, side);
	}
	
	@Override
	public @NotNull ModelData getModelData()
	{
		return facades.attach(ModelData.builder(), this)
				.build();
	}
	
	@Override
	public Optional<? extends ITraversable<ItemInPipe>> getRelativeTraversable(Direction side, ItemInPipe ignored)
	{
		return Cast.optionally(level.getBlockEntity(worldPosition.relative(side)), TileItemPipe.class)
				.filter(pipe -> connectsTo(side, pipe));
	}
	
	public int getPriority(Direction dir)
	{
		return Cast.optionally(level.getBlockEntity(worldPosition.relative(dir)), IHasPriority.class)
				.map(p -> p.getPriorityForFace(dir.getOpposite(), ForgeCapabilities.ITEM_HANDLER))
				.orElse(0);
	}
	
	@Override // All inventory handlers which can store the contents at least partially will be added.
	public List<EndpointData> getEndpoints(ItemInPipe contents)
	{
		return Stream.of(BlockItemPipe.DIRECTIONS)
				.filter(dir -> storeAnything(dir, contents, true))
				.map(dir -> new EndpointData(worldPosition, dir, getPriority(dir), true))
				.toList();
	}
	
	public boolean storeAnything(Direction to, ItemInPipe contents, boolean simulate)
	{
		return relativeItemHandler(to).map(handler ->
		{
			boolean modified = false;
			
			var slots = handler.getSlots();
			for(int i = 0; i < slots; ++i)
			{
				var remaining = handler.insertItem(i, contents.getContents(), simulate || isOnClient());
				if(!ItemStack.matches(contents.getContents(), remaining))
				{
					// If we are not simulating, perform the insert and update the remaining items.
					if(!simulate && isOnServer()) contents.setContents(remaining);
					modified = true;
					if(remaining.isEmpty() || simulate)
						return true;
				}
			}
			
			return modified;
		}).orElse(false);
	}
	
	public boolean doesConnectTo(Direction to)
	{
		return sideConfigs.get(to.ordinal()) != SideConfig.DISABLE
				&& (getRelativeTraversable(to, null).isPresent() || relativeItemHandler(to).isPresent());
	}
	
	private LazyOptional<IItemHandler> relativeItemHandler(Direction to)
	{
		var be = level.getBlockEntity(worldPosition.relative(to));
		return be == null || be instanceof TileItemPipe
				? LazyOptional.empty() // Either there is no block entity, or the block entity is a pipe
				: be.getCapability(ForgeCapabilities.ITEM_HANDLER, to.getOpposite());
	}
	
	private boolean connectsTo(Direction to, TileItemPipe pipe)
	{
		return sideConfigs.get(to.ordinal()) != SideConfig.DISABLE
				&& pipe.sideConfigs.get(to.getOpposite().ordinal()) != SideConfig.DISABLE;
	}
	
	@Override
	public BlockPos getPosition()
	{
		return worldPosition;
	}
	
	public float getSpeed()
	{
		return speed;
	}
}