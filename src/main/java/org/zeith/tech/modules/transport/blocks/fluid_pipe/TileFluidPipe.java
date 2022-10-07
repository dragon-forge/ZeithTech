package org.zeith.tech.modules.transport.blocks.fluid_pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyBool;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.block.ZeithTechStateProperties;
import org.zeith.tech.api.enums.SideConfig;
import org.zeith.tech.api.tile.IHasPriority;
import org.zeith.tech.api.tile.sided.SideConfig6;
import org.zeith.tech.core.net.properties.PropertyFluidStack;
import org.zeith.tech.core.net.properties.PropertyIntArray;
import org.zeith.tech.modules.transport.blocks.base.traversable.*;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;
import org.zeith.tech.utils.SerializableFluidTank;

import java.util.*;
import java.util.stream.Stream;

public class TileFluidPipe
		extends TileSyncableTickable
		implements ITraversable<FluidStack>
{
	public static final EnumSet<Direction> ALL_FLOW_DIRECTIONS = EnumSet.allOf(Direction.class);
	public static final EnumSet<Direction> NO_FLOW_DIRECTIONS = EnumSet.noneOf(Direction.class);
	
	@NBTSerializable("Sides")
	public final SideConfig6 sideConfigs = new SideConfig6(SideConfig.NONE);
	
	@NBTSerializable("Contents")
	public final SerializableFluidTank tank;
	
	/// START NETWORK SYNC VARS
	@NBTSerializable("Display")
	private FluidStack _displayFluid = FluidStack.EMPTY;
	@NBTSerializable("PrevDisplay")
	private FluidStack _prevDisplayFluid = FluidStack.EMPTY;
	private final PropertyFluidStack prevDisplayFluid = new PropertyFluidStack(DirectStorage.create(v -> _prevDisplayFluid = v, () -> _prevDisplayFluid));
	private final PropertyFluidStack displayFluid = new PropertyFluidStack(DirectStorage.create(v ->
	{
		_displayFluid = v;
		if(isOnClient())
			clientSyncTicks = 0;
	}, () -> _displayFluid));
	public final PropertyBool isBurning = new PropertyBool();
	public final PropertyIntArray sendingDirections = new PropertyIntArray();
	/// END NETWORK SYNC VARS
	
	@NBTSerializable("Pulls")
	public boolean shouldPullFromContainers;
	
	public final int transfer, maxTemperature;
	
	@NBTSerializable("BurnTime")
	public int burnTime;
	
	public EnumSet<Direction> flowDirections = ALL_FLOW_DIRECTIONS;
	
	public TileFluidPipe(BlockPos pos, BlockState state)
	{
		this(TilesZT_Transport.FLUID_PIPE, pos, state);
	}
	
	public TileFluidPipe(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		
		dispatcher.registerProperty("display", displayFluid);
		dispatcher.registerProperty("displayPrev", prevDisplayFluid);
		dispatcher.registerProperty("emitter", sendingDirections);
		dispatcher.registerProperty("burning", isBurning);
		
		var props = getPipeBlock().properties;
		
		this.tank = new SerializableFluidTank(props.storageVolume());
		this.transfer = props.transferVolume();
		this.maxTemperature = props.maxTemperature();
	}
	
	public BlockFluidPipe getPipeBlock()
	{
		var state = getBlockState();
		if(state.getBlock() instanceof BlockFluidPipe pipe)
			return pipe;
		return BlocksZT_Transport.WOODEN_FLUID_PIPE;
	}
	
	final int syncTickRate = 5;
	int clientSyncTicks = 0;
	FluidStack[] combined = new FluidStack[syncTickRate];
	
	{
		Arrays.fill(combined, FluidStack.EMPTY);
	}
	
	@Override
	public void update()
	{
		if(atTickRate(20) && isOnServer())
		{
			if(!tank.isEmpty())
			{
				var lst = TraversableHelper.findAllPaths(this, null, tank.getFluid())
						.stream()
						.map(p -> p.size() > 1 ? getTo(p.get(1)) : null)
						.filter(Objects::nonNull)
						.distinct()
						.toList();
				
				if(lst.isEmpty())
					this.flowDirections = NO_FLOW_DIRECTIONS;
				else
					this.flowDirections = EnumSet.copyOf(lst);
			} else this.flowDirections = NO_FLOW_DIRECTIONS;
			
			sendingDirections.set(this.flowDirections.stream().mapToInt(Direction::ordinal).toArray());
		}
		
		System.arraycopy(combined, 0, combined, 1, combined.length - 1);
		combined[0] = tank.getFluid().copy();
		
		sync:
		if(atTickRate(syncTickRate) && isOnServer())
		{
			prevDisplayFluid.set(displayFluid.get());
			FluidStack fluid = getServerAverage();
			displayFluid.set(fluid);
			if(!fluid.isEmpty())
				setLightLevel(Math.round(fluid.getAmount() * 5F / (float) tank.getCapacity()));
			else
				setLightLevel(0);
			
			if(!tank.isEmpty())
			{
				var fs = tank.getFluid();
				var temp = fs.getFluid().getFluidType().getTemperature(fs);
				if(temp > maxTemperature)
				{
					isBurning.setBool(true);
					
					var add = 15;
					
					var fsWorld = level.getFluidState(worldPosition);
					if(!fs.isEmpty())
					{
						var tempWorld = fsWorld.getType().getFluidType().getTemperature(fsWorld, level, worldPosition);
						if(tempWorld < maxTemperature)
							add = 5;
					}
					
					if((burnTime += add) >= 150)
					{
						level.levelEvent(1502, worldPosition, 0);
						level.removeBlock(worldPosition, false);
					}
				}
			} else
			{
				isBurning.setBool(false);
				if(burnTime > 0)
				{
					var fs = level.getFluidState(worldPosition);
					if(fs.isEmpty())
						burnTime--;
					else
					{
						var temp = fs.getType().getFluidType().getTemperature(fs, level, worldPosition);
						if(temp < maxTemperature)
							burnTime = Math.max(burnTime - 10, 0);
					}
				}
			}
		}
		
		emit:
		if(!tank.isEmpty() && atTickRate(6) && isOnServer())
		{
			Direction[] directions = BlockFluidPipe.DIRECTIONS;
			
			for(int i = 0; i < directions.length && !tank.isEmpty(); i++)
			{
				Direction to = directions[i];
				tank.drain(storeAnything(to, tank.getFluid(), false), IFluidHandler.FluidAction.EXECUTE);
			}
			
			if(tank.isEmpty())
				break emit;
			
			if(this.flowDirections.isEmpty())
			{
				if(atTickRate(100) && !tank.isEmpty())
					balanceOut(TraversableHelper.allTraversables(this, tank.getFluid(), true)
							.stream()
							.map(t -> Cast.cast(t, TileFluidPipe.class))
							.filter(Objects::nonNull)
							.toArray(TileFluidPipe[]::new), false);
				else
					balanceOut(ALL_FLOW_DIRECTIONS.stream()
							.flatMap(dir -> getRelativeTraversable(dir, tank.getFluid()).stream())
							.toArray(TileFluidPipe[]::new), true);
			} else
				sendTo(this.flowDirections.stream()
						.flatMap(dir -> getRelativeTraversable(dir, tank.getFluid()).stream())
						.toArray(TileFluidPipe[]::new));
		}
		
		if(isOnClient())
		{
			++clientSyncTicks;
			
			if(isBurning.getBoolean() && level != null)
			{
				VoxelShape voxelshape = getBlockState().getShape(level, worldPosition);
				
				double step = 0.25D;
				
				voxelshape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
				{
					double dX = Math.min(1.0D, maxX - minX);
					double dY = Math.min(1.0D, maxY - minY);
					double dZ = Math.min(1.0D, maxZ - minZ);
					
					int i = Math.max(2, Mth.ceil(dX / step));
					int j = Math.max(2, Mth.ceil(dY / step));
					int k = Math.max(2, Mth.ceil(dZ / step));
					
					for(int x = 0; x < i; ++x)
					{
						for(int y = 0; y < j; ++y)
						{
							for(int z = 0; z < k; ++z)
							{
								double speedX = ((double) x + 0.5D) / (double) i;
								double speedY = ((double) y + 0.5D) / (double) j;
								double speedZ = ((double) z + 0.5D) / (double) k;
								
								double d7 = speedX * dX + minX;
								double d8 = speedY * dY + minY;
								double d9 = speedZ * dZ + minZ;
								
								var props = ParticleTypes.SMOKE;
								
								int hash = Objects.hash(x, y, z);
								
								if((ticksExisted + hash) % 3 == 0)
									level.addParticle(props, (double) worldPosition.getX() + d7, (double) worldPosition.getY() + d8, (double) worldPosition.getZ() + d9, (speedX - 0.5D) / 15, (speedY - 0.5D) / 15, (speedZ - 0.5D) / 15);
							}
						}
					}
					
				});
			}
		}
	}
	
	public void setLightLevel(int light)
	{
		light = Mth.clamp(light, 0, 15);
		var prop = ZeithTechStateProperties.LIGHT_LEVEL;
		var state = getBlockState();
		if(state.hasProperty(prop) && !Objects.equals(state.getValue(prop), light))
			level.setBlockAndUpdate(worldPosition, state.setValue(prop, light));
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
	
	private FluidStack getServerAverage()
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
	
	public boolean balanceOut(TileFluidPipe[] pipes, boolean addSelf)
	{
		if(addSelf)
			pipes = ArrayUtils.addFirst(pipes, this);
		
		FluidStack result = new FluidStack(Fluids.EMPTY, 0);
		
		// Accumulate fluids, if there are different fluids, fail the balancing.
		for(TileFluidPipe pipe : pipes)
		{
			if(pipe.tank.isEmpty())
				continue;
			var pipeFluid = pipe.tank.getFluid();
			if(!result.isEmpty() && !pipeFluid.isFluidEqual(result))
				return false;
			if(result.isEmpty())
				result = pipeFluid.copy();
			else
				result.grow(pipeFluid.getAmount());
		}
		
		int per = result.getAmount() / pipes.length;
		int left = result.getAmount() - per * pipes.length;
		
		for(TileFluidPipe pipe : pipes)
		{
			pipe.tank.setFluid(FluidStack.EMPTY);
			
			if(left > 0)
				left -= pipe.tank.fill(PipeFluidHandler.withAmount(result, left), IFluidHandler.FluidAction.EXECUTE);
			
			pipe.tank.fill(PipeFluidHandler.withAmount(result, per), IFluidHandler.FluidAction.EXECUTE);
		}
		
		return true;
	}
	
	public void sendTo(TileFluidPipe[] pipes)
	{
		int totalAccept = 0;
		int[] sent = new int[pipes.length];
		
		for(int i = 0; i < pipes.length; i++)
		{
			TileFluidPipe pipe = pipes[i];
			totalAccept += sent[i] = pipe.tank.fill(tank.getFluid(), IFluidHandler.FluidAction.SIMULATE);
		}
		
		if(totalAccept > tank.getFluidAmount())
		{
			double coef = tank.getFluidAmount() / (double) totalAccept;
			
			totalAccept = 0;
			
			for(int i = 0; i < pipes.length; i++)
			{
				TileFluidPipe pipe = pipes[i];
				sent[i] *= coef;
				totalAccept += pipe.tank.fill(PipeFluidHandler.limit(tank.getFluid(), sent[i]), IFluidHandler.FluidAction.EXECUTE);
			}
			
			tank.drain(totalAccept, IFluidHandler.FluidAction.EXECUTE);
		} else
		{
			totalAccept = 0;
			
			for(int i = 0; i < pipes.length; i++)
			{
				TileFluidPipe pipe = pipes[i];
				totalAccept += pipe.tank.fill(PipeFluidHandler.limit(tank.getFluid(), sent[i]), IFluidHandler.FluidAction.EXECUTE);
			}
			
			tank.drain(totalAccept, IFluidHandler.FluidAction.EXECUTE);
		}
	}
	
	@Override
	public Optional<TileFluidPipe> getRelativeTraversable(Direction side, FluidStack contents)
	{
		return Cast.optionally(level.getBlockEntity(worldPosition.relative(side)), TileFluidPipe.class)
				.filter(pipe -> connectsTo(side, pipe))
				.filter(pipe -> pipe.fluidsCompatible(contents));
	}
	
	public boolean fluidsCompatible(FluidStack contents)
	{
		return !contents.isEmpty() && (tank.isEmpty() || tank.getFluid().isFluidEqual(contents));
	}
	
	public int getPriority(Direction dir)
	{
		return Cast.optionally(level.getBlockEntity(worldPosition.relative(dir)), IHasPriority.class)
				.map(p -> p.getPriorityForFace(dir.getOpposite(), ForgeCapabilities.FLUID_HANDLER))
				.orElse(0);
	}
	
	@Override // All fluid handlers that can accept any amount of fluid will be added.
	public List<EndpointData> getEndpoints(FluidStack contents)
	{
		return Stream.of(BlockFluidPipe.DIRECTIONS)
				.filter(dir -> storeAnything(dir, contents, true) > 0)
				.map(dir -> new EndpointData(worldPosition, dir, getPriority(dir), true))
				.toList();
	}
	
	@Override
	public BlockPos getPosition()
	{
		return worldPosition;
	}
	
	public int storeAnything(Direction to, FluidStack contents, boolean simulate)
	{
		return relativeFluidHandler(to).map(handler ->
				handler.fill(contents,
						simulate || isOnClient()
								? IFluidHandler.FluidAction.SIMULATE
								: IFluidHandler.FluidAction.EXECUTE
				)
		).orElse(0);
	}
	
	public boolean doesConnectTo(Direction to)
	{
		return level.getBlockEntity(worldPosition.relative(to)) instanceof TileFluidPipe
				|| (sideConfigs.get(to.ordinal()) != SideConfig.DISABLE && relativeFluidHandler(to).isPresent());
	}
	
	private LazyOptional<IFluidHandler> relativeFluidHandler(Direction to)
	{
		var be = level.getBlockEntity(worldPosition.relative(to));
		return be == null || be instanceof TileFluidPipe
				? LazyOptional.empty() // Either there is no block entity, or the block entity is a pipe
				: be.getCapability(ForgeCapabilities.FLUID_HANDLER, to.getOpposite());
	}
	
	private boolean connectsTo(Direction to, TileFluidPipe pipe)
	{
		return sideConfigs.get(to.ordinal()) != SideConfig.DISABLE
				&& pipe.sideConfigs.get(to.getOpposite().ordinal()) != SideConfig.DISABLE;
	}
	
	private final net.minecraftforge.common.util.LazyOptional<?>[] sidedFluidHandlers =
			Direction.stream()
					.map(dir -> LazyOptional.of(() -> new PipeFluidHandler(this, dir)))
					.toArray(LazyOptional[]::new);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(side != null && cap == ForgeCapabilities.FLUID_HANDLER && sideConfigs.get(side.ordinal()) != SideConfig.DISABLE)
			return sidedFluidHandlers[side.ordinal()].cast();
		
		return super.getCapability(cap, side);
	}
}