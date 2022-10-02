package org.zeith.tech.modules.transport.blocks.item_pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.tech.modules.transport.blocks.base.traversable.EndpointData;
import org.zeith.tech.modules.transport.blocks.base.traversable.TraversablePath;

import java.util.Stack;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemInPipe
		implements INBTSerializable<CompoundTag>
{
	public Stack<Direction> path = new Stack<>();
	
	public Direction from;
	
	public EndpointData endpoint;
	
	public UUID itemId = UUID.randomUUID();
	
	public ItemStack contents = ItemStack.EMPTY;
	
	public BlockPos prevPos = BlockPos.ZERO;
	public BlockPos currentPos = BlockPos.ZERO;
	public BlockPos nextPos = BlockPos.ZERO;
	
	public float speedMultiplied = 1F;
	
	public float prevPipeProgress, currentPipeProgress, currentSpeed;
	
	public ItemInPipe()
	{
	}
	
	public ItemInPipe(CompoundTag nbt)
	{
		deserializeNBT(nbt);
	}
	
	public boolean update(TileItemPipe currentPipe)
	{
		currentSpeed = currentPipe.getSpeed();
		prevPipeProgress = currentPipeProgress;
		
		if((currentPipeProgress = Math.min(currentPipeProgress + currentSpeed, 1F)) >= 1)
		{
			this.prevPos = currentPos;
			var nextDst = path.isEmpty() ? endpoint.dir() : path.get(0);
			
			if(currentPipe.transferItem(this, nextDst))
			{
				currentPipeProgress = 0;
				prevPipeProgress = 0;
				
				if(!path.isEmpty()) path.remove(0);
				currentPos = currentPipe.getPosition().relative(nextDst);
				
				var nextDst2 = path.isEmpty() ? endpoint.dir() : path.get(0);
				this.nextPos = currentPos.relative(nextDst2);
			} else
			{
				// If we were unable to move the item to the nextFace
				
				ItemStack remaining = currentPipe.insertItemIntoPipe(getContents(), nextDst, false);
				
				if(!remaining.isEmpty())
				{
					Vec3 pos = getCurrentPosition(1F);
					Containers.dropItemStack(currentPipe.getLevel(), pos.x, pos.y, pos.z, remaining);
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public Vec3 getCurrentPosition(float partialTicks)
	{
		float progress = Math.min(prevPipeProgress + (currentPipeProgress - prevPipeProgress) * partialTicks, 1F);
		if(progress <= 0.5F)
			return Vec3.atCenterOf(prevPos).lerp(Vec3.atCenterOf(currentPos), 0.5F + progress);
		return Vec3.atCenterOf(currentPos).lerp(Vec3.atCenterOf(nextPos), progress - 0.5F);
	}
	
	public ItemStack getContents()
	{
		return contents;
	}
	
	public ItemInPipe setContents(ItemStack contents)
	{
		if(contents.isEmpty()) contents = ItemStack.EMPTY;
		this.contents = contents;
		return this;
	}
	
	static final Direction[] DIRECTIONS = Direction.values();
	
	@Override
	public CompoundTag serializeNBT()
	{
		var nbt = new CompoundTag();
		
		nbt.putLong("PrevPos", prevPos.asLong());
		nbt.putLong("CurPos", currentPos.asLong());
		nbt.putLong("NextPos", nextPos.asLong());
		nbt.putFloat("Move", currentPipeProgress);
		nbt.putFloat("PMove", prevPipeProgress);
		nbt.put("Item", contents.serializeNBT());
		nbt.putUUID("Id", itemId);
		nbt.put("Endpoint", endpoint.serializeNBT());
		nbt.putByte("From", (byte) from.ordinal());
		nbt.putFloat("SpMul", speedMultiplied);
		
		nbt.putByteArray("ToDo", path.stream().map(Direction::ordinal).map(Integer::byteValue).collect(Collectors.toList()));
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		prevPos = BlockPos.of(nbt.getLong("PrevPos"));
		currentPos = BlockPos.of(nbt.getLong("CurPos"));
		nextPos = BlockPos.of(nbt.getLong("NextPos"));
		currentPipeProgress = nbt.getFloat("Move");
		prevPipeProgress = nbt.getFloat("PMove");
		contents = ItemStack.of(nbt.getCompound("Item"));
		itemId = nbt.getUUID("Id");
		endpoint = new EndpointData(nbt.getCompound("Endpoint"));
		from = DIRECTIONS[nbt.getByte("From")];
		speedMultiplied = nbt.getFloat("SpMul");
		
		path.clear();
		for(var b : nbt.getByteArray("ToDo"))
			path.add(DIRECTIONS[b]);
	}
	
	public boolean applyPath(Direction from, TraversablePath<ItemInPipe> path)
	{
		this.path.clear();
		
		var prev = path.get(0);
		for(int i = 1; i < path.size(); ++i)
		{
			var cur = path.get(i);
			{
				Direction dir = prev.getTo(cur);
				if(dir == null)
				{
					this.path.clear();
					return false;
				}
				this.path.push(dir);
			}
			prev = cur;
		}
		
		var first = path.get(0);
		
		this.prevPos = first.getPosition().relative(from);
		this.currentPos = first.getPosition();
		
		var to = path.size() > 1 ? first.getTo(path.get(1)) : path.endpoint.dir();
		this.nextPos = this.currentPos.relative(to != null ? to : from.getOpposite());
		
		this.from = from;
		this.endpoint = path.endpoint;
		
		return true;
	}
	
	public static Consumer<ItemInPipe> multiplySpeed(float speed)
	{
		return iip -> iip.speedMultiplied *= speed;
	}
}