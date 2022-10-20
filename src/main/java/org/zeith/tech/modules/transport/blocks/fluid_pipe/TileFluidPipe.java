package org.zeith.tech.modules.transport.blocks.fluid_pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyBool;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.fluid.FluidHelper;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.block.ZeithTechStateProperties;
import org.zeith.tech.api.enums.SideConfig;
import org.zeith.tech.api.tile.IFluidPipe;
import org.zeith.tech.api.tile.IHasPriority;
import org.zeith.tech.api.tile.facade.FacadeData;
import org.zeith.tech.api.tile.sided.SideConfig6;
import org.zeith.tech.core.net.properties.PropertyIntArray;
import org.zeith.tech.modules.transport.blocks.base.traversable.*;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;
import org.zeith.tech.utils.SerializableFluidIngredient;
import org.zeith.tech.utils.SerializableFluidTank;
import org.zeith.tech.utils.fluid.FluidSmoothing;

import java.util.*;
import java.util.stream.Stream;

public class TileFluidPipe
		extends TileSyncableTickable
		implements ITraversable<FluidStack>, IFluidPipe
{
	public static final EnumSet<Direction> ALL_FLOW_DIRECTIONS = EnumSet.allOf(Direction.class);
	public static final EnumSet<Direction> NO_FLOW_DIRECTIONS = EnumSet.noneOf(Direction.class);
	
	@NBTSerializable("Sides")
	public final SideConfig6 sideConfigs = new SideConfig6(SideConfig.NONE);
	
	@NBTSerializable("Facades")
	public final FacadeData facades = new FacadeData(() -> getPipeBlock().getShapeWithNoFacades(getBlockState()).toAabbs(), this::facadesUpdated);
	
	@NBTSerializable("Contents")
	public final SerializableFluidTank tank;
	
	/// START NETWORK SYNC VARS
	@NBTSerializable("Smooth")
	public final FluidSmoothing fluidSmoothing;
	public final PropertyBool isBurning = new PropertyBool();
	public final PropertyIntArray sendingDirections = new PropertyIntArray();
	/// END NETWORK SYNC VARS
	
	@NBTSerializable("VaccumTime")
	public int vacuumTicks;
	
	@NBTSerializable("VaccumFluid")
	public final SerializableFluidIngredient vacuumFluid = new SerializableFluidIngredient();
	
	@NBTSerializable("SkipTicks")
	public int skipTicks;
	
	public final int maxTransfer, maxTemperature;
	
	@NBTSerializable("BurnTime")
	public int burnTime;
	
	public EnumSet<Direction> flowDirections = ALL_FLOW_DIRECTIONS;
	
	@NBTSerializable("VacuumRootTicks")
	public int vacuumRootTicks;
	
	@NBTSerializable("VacuumPriority")
	public int vacuumPriority;
	
	public TileFluidPipe(BlockPos pos, BlockState state)
	{
		this(TilesZT_Transport.FLUID_PIPE, pos, state);
	}
	
	public TileFluidPipe(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		
		fluidSmoothing = new FluidSmoothing("display", this);
		
		dispatcher.registerProperty("emitter", sendingDirections);
		dispatcher.registerProperty("burning", isBurning);
		
		var props = getPipeBlock().properties;
		
		this.tank = new SerializableFluidTank(props.storageVolume());
		this.maxTransfer = props.transferVolume();
		this.maxTemperature = props.maxTemperature();
	}
	
	public BlockFluidPipe getPipeBlock()
	{
		var state = getBlockState();
		if(state.getBlock() instanceof BlockFluidPipe pipe)
			return pipe;
		return BlocksZT_Transport.WOODEN_FLUID_PIPE;
	}
	
	final List<ITraversable<FluidStack>> preferred = new ArrayList<>();
	
	public void facadesUpdated()
	{
		if(isOnServer())
			sync();
		if(isOnClient())
		{
			requestModelDataUpdate();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}
	
	@Override
	public void update()
	{
		if(skipTicks > 0)
		{
			--skipTicks;
			return;
		}
		
		findTargets:
		if(atTickRate(20) && isOnServer())
		{
			if(!tank.isEmpty())
			{
				var paths = TraversableHelper.findAllPaths(this, null, tank.getFluid());
				
				var lst = paths
						.stream()
						.map(p -> p.size() > 1 ? getTo(p.get(1)) : null)
						.filter(Objects::nonNull)
						.distinct()
						.toList();
				
				if(lst.isEmpty())
					this.flowDirections = NO_FLOW_DIRECTIONS;
				else
				{
					preferred.clear();
					paths.stream()
							.filter(p -> (p.size() > 1 ? getTo(p.get(1)) : null) != null)
							.map(p -> p.get(1))
							.distinct()
							.sorted(Comparator.comparingInt(i -> i instanceof TileFluidPipe p ? -p.vacuumPriority : 0))
							.forEach(preferred::add);
					
					this.flowDirections = EnumSet.copyOf(lst);
				}
			} else this.flowDirections = NO_FLOW_DIRECTIONS;
			
			sendingDirections.set(this.flowDirections.stream().mapToInt(Direction::ordinal).toArray());
		}
		
		smooth:
		fluidSmoothing.update(tank.getFluid());
		
		sync:
		if(atTickRate(5) && isOnServer())
		{
			FluidStack fluid = fluidSmoothing.getServerAverage();
			if(!fluid.isEmpty())
				setLightLevel(Math.round(fluid.getAmount() * (fluid.getFluid().getFluidType().getLightLevel(fluid) / 15F) * 6F / (float) tank.getCapacity()));
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
			
			if(vacuumTicks <= 0)
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
			{
				sendTo(Stream.concat(
						this.preferred.stream().map(t -> Cast.cast(t, TileFluidPipe.class))
								.filter(Objects::nonNull)
								.limit(1L),
						this.flowDirections.stream()
								.flatMap(dir -> getRelativeTraversable(dir, tank.getFluid()).stream())
				).toArray(TileFluidPipe[]::new));
			}
		}
		
		smokes:
		if(isOnClient())
		{
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
		
		vacuum:
		if(vacuumTicks > 0 && isOnServer())
		{
			--vacuumTicks;
			
			for(Direction dir : BlockFluidPipe.DIRECTIONS)
			{
				relativeFluidHandler(dir)
						.ifPresent(remote ->
								getCapability(ForgeCapabilities.FLUID_HANDLER, dir).ifPresent(pipe ->
										{
											if(vacuumFluid.isEmpty())
												FluidUtil.tryFluidTransfer(pipe, remote, maxTransfer, true);
											else
												for(var fs : vacuumFluid.ingredient.getValues())
													if(!FluidUtil.tryFluidTransfer(pipe, remote, FluidHelper.withAmount(fs, maxTransfer), true).isEmpty())
														break;
										}
								)
						);
			}
		} else vacuumFluid.empty();
		
		if(vacuumRootTicks > 0)
			--vacuumRootTicks;
		else
		{
			for(Direction dir : BlockFluidPipe.DIRECTIONS)
				if(level.getBlockEntity(worldPosition.relative(dir)) instanceof TileFluidPipe pipe)
					this.vacuumPriority = Math.max(pipe.vacuumPriority, vacuumPriority);
			if(vacuumPriority > 0)
				--vacuumPriority;
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
		return fluidSmoothing.getClientAverage(partial);
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
			pipe.tank.fill(PipeFluidHandler.withAmount(result, per), IFluidHandler.FluidAction.EXECUTE);
		}
		
		if(!preferred.isEmpty())
		{
			for(var rp : preferred)
				if(rp instanceof TileFluidPipe pipe)
				{
					if(left > 0)
						left -= pipe.tank.fill(PipeFluidHandler.withAmount(result, left), IFluidHandler.FluidAction.EXECUTE);
					if(left <= 0)
						break;
				}
		}
		
		if(left > 0)
			tank.fill(PipeFluidHandler.withAmount(result, left), IFluidHandler.FluidAction.EXECUTE);
		
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
			
			final int acceptCapacity = Math.min(tank.getFluidAmount(), totalAccept);
			totalAccept = 0;
			
			for(int i = 0; i < pipes.length; i++)
			{
				TileFluidPipe pipe = pipes[i];
				sent[i] *= coef;
				totalAccept += pipe.tank.fill(PipeFluidHandler.limit(tank.getFluid(), sent[i]), IFluidHandler.FluidAction.EXECUTE);
			}
			
			for(int i = level.random.nextInt(pipes.length); i < pipes.length && totalAccept < acceptCapacity; i++)
			{
				TileFluidPipe pipe = pipes[i];
				int send = acceptCapacity - totalAccept;
				totalAccept += pipe.tank.fill(PipeFluidHandler.limit(tank.getFluid(), send), IFluidHandler.FluidAction.EXECUTE);
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
				.filter(pipe -> connectsTo(side, pipe) && (contents.isEmpty() || pipe.fluidsCompatible(contents)));
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
		return sideConfigs.get(to.ordinal()) != SideConfig.DISABLE &&
				(level.getBlockEntity(worldPosition.relative(to)) instanceof TileFluidPipe || relativeFluidHandler(to).isPresent());
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
	
	private final LazyOptional<FacadeData> facadesCap = LazyOptional.of(() -> facades);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(side != null && cap == ForgeCapabilities.FLUID_HANDLER && sideConfigs.get(side.ordinal()) != SideConfig.DISABLE) return sidedFluidHandlers[side.ordinal()].cast();
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
	public void createVacuum(FluidIngredient fluid, int ticks)
	{
		vacuumRootTicks = ticks;
		
		var ts = TraversableHelper.allTraversables(this, FluidStack.EMPTY, true);
		vacuumPriority = ts.size(); // this pipe is the endpoint
		
		ts.stream()
				.filter(TileFluidPipe.class::isInstance)
				.forEach(t ->
				{
					((TileFluidPipe) t).vacuumTicks = Math.max(vacuumTicks, ticks);
					((TileFluidPipe) t).vacuumFluid.ingredient = fluid;
				});
	}
	
	@Override
	public FluidStack extractFluidFromPipe(int amount, IFluidHandler.FluidAction action)
	{
		return tank.drain(amount, action);
	}
}