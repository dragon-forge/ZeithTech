package org.zeith.tech.modules.transport.blocks.fluid_pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.Cast;
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
	
	public final PropertyFluidStack displayFluid = new PropertyFluidStack();
	
	public final PropertyIntArray sendingDirections = new PropertyIntArray();
	
	public final int transfer, maxTemperature;
	
	private boolean skipTick = false;
	
	public EnumSet<Direction> flowDirections = ALL_FLOW_DIRECTIONS;
	
	public TileFluidPipe(BlockPos pos, BlockState state)
	{
		this(TilesZT_Transport.FLUID_PIPE, pos, state);
	}
	
	public TileFluidPipe(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		
		dispatcher.registerProperty("display", displayFluid);
		dispatcher.registerProperty("emitter", sendingDirections);
		
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
		
		if(atTickRate(2) && isOnServer())
			displayFluid.set(tank.getFluid().copy());
		
		emit:
		if(!tank.isEmpty() && atTickRate(5) && isOnServer())
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
				balanceOut(ALL_FLOW_DIRECTIONS.stream()
						.flatMap(dir -> getRelativeTraversable(dir, tank.getFluid()).stream())
						.toArray(TileFluidPipe[]::new));
			else
				sendTo(this.flowDirections.stream()
						.flatMap(dir -> getRelativeTraversable(dir, tank.getFluid()).stream())
						.toArray(TileFluidPipe[]::new));
		}
	}
	
	public boolean balanceOut(TileFluidPipe[] pipes)
	{
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
		skipTick = true;
		
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
				pipe.skipTick = true;
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
				pipe.skipTick = true;
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