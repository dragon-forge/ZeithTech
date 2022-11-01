package org.zeith.tech.modules.processing.blocks.fluid_pump;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.Network;
import org.zeith.hammerlib.net.packets.SyncTileEntityPacket;
import org.zeith.hammerlib.util.mcf.BlockPosList;
import org.zeith.hammerlib.util.physics.FrictionRotator;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.utils.InventoryHelper;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.SoundsZT_Processing;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.utils.SerializableFluidTank;
import org.zeith.tech.utils.fluid.FluidSmoothing;

import java.util.*;
import java.util.stream.IntStream;

public class TileFluidPump
		extends TileBaseMachine<TileFluidPump>
{
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.FLUID))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.PULL)
			.setDefaults(SidedConfigTyped.FLUID, SideConfig.DISABLE)
			.setFor(SidedConfigTyped.FLUID, RelativeDirection.UP, SideConfig.PUSH)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.UP, SideConfig.DISABLE)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.DOWN, SideConfig.DISABLE);
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.EXTRA_LOW_VOLTAGE, EnumEnergyManagerKind.CONSUMER);
	
	@NBTSerializable("OutSmooth")
	public final FluidSmoothing tankSmooth;
	
	@NBTSerializable("OutputFluid")
	public final SerializableFluidTank fluidTank = new SerializableFluidTank(5000);
	
	@NBTSerializable("Positions")
	public final BlockPosList discoveredPositions = new BlockPosList();
	
	@NBTSerializable("Lock")
	public final FluidPumpLock lock = new FluidPumpLock();
	
	@NBTSerializable("Items")
	public final SimpleInventory inventory = new SimpleInventory(2);
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	public int cooldownTime = 40;
	
	@NBTSerializable("Cooldown")
	public int cooldown = cooldownTime;
	
	public final FrictionRotator rotator = new FrictionRotator();
	
	@NBTSerializable("ActiveY")
	public int currentY;
	
	@NBTSerializable("DonePumping")
	public boolean isDone;
	
	@NBTSerializable("PumpingAt")
	public long donePumpingXZ;
	
	public TileFluidPump(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.FLUID_PUMP, pos, state);
		this.rotator.friction = 1F;
		this.tankSmooth = new FluidSmoothing("out_display", this);
		inventory.isStackValid = (slot, stack) -> stack.is(TagsZT.Items.MINING_PIPE);
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		
		if(isDone && atTickRate(20) && donePumpingXZ != BlockPos.asLong(worldPosition.getX(), 0, worldPosition.getZ()))
		{
			currentY = worldPosition.getY() - 1;
			isDone = false;
		}
		
		if(isOnClient() && isEnabled() && !isInterrupted())
		{
			rotator.speedupTo(30F, 3F);
			
			ZeithTechAPI.get()
					.getAudioSystem()
					.playMachineSoundLoop(this, SoundsZT_Processing.FLUID_PUMP, SoundsZT_Processing.BASIC_MACHINE_INTERRUPT);
		}
		
		rotator.update();
		tankSmooth.update(fluidTank.getFluid());
		
		if(isOnServer() && level instanceof ServerLevel srv)
		{
			// Process cooldown by using energy
			if(cooldown > 0)
			{
				if(energy.consumeEnergy(20))
				{
					if(!discoveredPositions.isEmpty())
						isInterrupted.setBool(false);
					--cooldown;
				} else isInterrupted.setBool(true);
			}
			
			if(!lock.isLocked() && cooldown <= 0)
			{
				for(var y = worldPosition.getY() - 1; y >= level.getMinBuildHeight(); --y)
				{
					var pos = new BlockPos(worldPosition.getX(), y, worldPosition.getZ());
					
					// Mining pipes are okay to go through
					if(level.getBlockState(pos).is(TagsZT.Blocks.MINING_PIPE)) continue;
					
					if(level.isEmptyBlock(pos))
					{
						for(ItemStack it : inventory)
							if(it.is(TagsZT.Items.MINING_PIPE))
							{
								var block = Block.byItem(it.getItem());
								if(block != Blocks.AIR)
								{
									it.split(1);
									level.setBlockAndUpdate(pos, block.defaultBlockState());
									cooldown += cooldownTime;
									break;
								}
							}
						break;
					}
					
					var fs = level.getFluidState(pos);
					if(fs.isEmpty() || !lock.lock(fs))
						// We can't find a fluid... / Could not lock to a fluid (it is probably not a source block)
						return;
					currentY = pos.getY();
					discoveredPositions.add(pos);
					discoverMoreFluid();
					setEnabledState(true);
					break;
				}
				
				if(isEnabled())
					setEnabledState(false);
			} else if(lock.isLocked() && fluidTank.getFluidAmount() < fluidTank.getCapacity())
			{
				var work = redstone.shouldWork(this);
				
				if(isEnabled() != work)
					setEnabledState(work);
				
				// Discover fluids
				if(atTickRate(100) && work)
					discoverMoreFluid();
				
				if(cooldown <= 0 && work)
				{
					var update = discoveredPositions.removeIf(position ->
					{
						// NEVER EVER do anything with unloaded blocks!
						if(!level.isLoaded(position)) return false;
						
						// Don't pump anything below the currently active Y level!
						if(position.getY() < currentY) return false;
						
						var fs = level.getFluidState(position);
						
						if(lock.test(fs))
						{
							if(cooldown <= 0)
							{
								var fill = fluidTank.fill(new FluidStack(fs.getType(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
								if(fill == FluidType.BUCKET_VOLUME)
								{
									if(FluidUtil.tryPickUpFluid(new ItemStack(Items.BUCKET), null, level, position, Direction.UP).isSuccess())
									{
										fluidTank.fill(new FluidStack(fs.getType(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
										sync();
									}
									cooldown += cooldownTime;
									return true;
								}
							}
							
							return false;
						}
						
						// Remove empty positions, and those which don't match the lock.
						return true;
					});
					
					if(update)
						discoverMoreFluid();
					
					if(discoveredPositions.isEmpty())
						isInterrupted.setBool(true);
					else
					{
						var qpos = new BlockPos(worldPosition.getX(), currentY, worldPosition.getZ());
						
						if(level.isEmptyBlock(qpos))
							for(ItemStack it : inventory)
								if(it.is(TagsZT.Items.MINING_PIPE))
								{
									var block = Block.byItem(it.getItem());
									if(block != Blocks.AIR)
									{
										it.split(1);
										level.setBlockAndUpdate(qpos, block.defaultBlockState());
										cooldown += cooldownTime;
										isInterrupted.setBool(false);
										break;
									}
								}
						
						// If all fluids that have been located, were pumped out (no fluids left on any levels that have been processed, then go down by a block!
						if(discoveredPositions.stream().noneMatch(p -> p.getY() >= currentY) && cooldown <= 0)
						{
							if(level.getBlockState(qpos).is(TagsZT.Blocks.MINING_PIPE))
								--currentY;
							else
							{
								var fs = level.getFluidState(qpos);
								if(!fs.isEmpty())
								{
									discoveredPositions.add(qpos);
									discoverMoreFluid();
									isInterrupted.setBool(false);
								} else
									isInterrupted.setBool(true);
							}
						}
					}
				}
			}
			
			if(isDone && currentY < worldPosition.getY() && atTickRate(10))
			{
				var qpos = new BlockPos(worldPosition.getX(), currentY, worldPosition.getZ());
				
				var state = level.getBlockState(qpos);
				
				if(state.is(TagsZT.Blocks.MINING_PIPE))
				{
					var blockDrops = InventoryHelper.getBlockDropsAt(srv, qpos);
					
					// Create copy of all drops!
					List<ItemStack> blockDropsCopy = new ArrayList<>(blockDrops);
					blockDropsCopy.replaceAll(ItemStack::copy);
					
					if(storeAll(blockDrops, true))
					{
						storeAll(blockDropsCopy, false);
						level.destroyBlock(qpos, false);
						++currentY;
					}
				} else
				{
					++currentY;
				}
			}
			
			if(atTickRate(2) && !fluidTank.isEmpty())
				relativeFluidHandler(Direction.UP)
						.ifPresent(handler -> FluidUtil.tryFluidTransfer(handler, fluidTank, 100, true));
		}
	}
	
	public boolean storeAll(List<ItemStack> stacks, boolean simulate)
	{
		return InventoryHelper.storeAllStacks(inventory, IntStream.range(0, inventory.getSlots()), stacks, simulate);
	}
	
	private static final Direction[] DIRECTIONS = Direction.values();
	
	public void discoverMoreFluid()
	{
		if(discoveredPositions.isEmpty())
		{
			for(var y = worldPosition.getY() - 1; y >= level.getMinBuildHeight(); --y)
			{
				var pos = new BlockPos(worldPosition.getX(), y, worldPosition.getZ());
				if(level.isEmptyBlock(pos)) continue;
				var fs = level.getFluidState(pos);
				if(fs.isEmpty() || !lock.test(fs))
					// We can't find a fluid... / Could not lock to a fluid (it is probably not a source block)
					return;
				discoveredPositions.add(pos);
				break;
			}
		}
		
		if(discoveredPositions.size() > 32 * 1024)
			return;
		
		int limit = 1024;
		for(int i = 0; i < discoveredPositions.size() && limit > 0; ++i)
		{
			var cpos = discoveredPositions.get(i);
			for(Direction dir : DIRECTIONS)
			{
				var pos = cpos.relative(dir);
				if(!level.isLoaded(pos) || discoveredPositions.contains(pos)) continue;
				var fluid = level.getFluidState(pos);
				if(!fluid.isEmpty() && lock.test(fluid))
				{
					discoveredPositions.add(pos);
					--limit;
				}
			}
		}
		
		discoveredPositions.sort(Comparator.comparingDouble(worldPosition::distSqr));
	}
	
	@Override
	public CompoundTag getUpdateTag()
	{
		var tag = super.getUpdateTag();
		tag.remove("Positions");
		return tag;
	}
	
	private LazyOptional<IFluidHandler> relativeFluidHandler(Direction to)
	{
		var be = level.getBlockEntity(worldPosition.relative(to));
		return be == null ? LazyOptional.empty() : be.getCapability(ForgeCapabilities.FLUID_HANDLER, to.getOpposite());
	}
	
	@Override
	public ContainerBaseMachine<TileFluidPump> openContainer(Player player, int windowId)
	{
		Network.sendTo(new SyncTileEntityPacket(this, true), player);
		return new ContainerFluidPump(this, player, windowId);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(inventory, energy.batteryInventory);
	}
	
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<IFluidHandler> outputFluidHandler = LazyOptional.of(FluidOutput::new);
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	private final LazyOptional<RedstoneControl> redstoneCap = LazyOptional.of(() -> redstone);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY && (side == null || sidedConfig.canAccess(SidedConfigTyped.ENERGY, side))) return energyCap.cast();
		if(cap == ZeithTechCapabilities.REDSTONE_CONTROL) return redstoneCap.cast();
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE) return energy.measurableCap.cast();
		if(cap == ZeithTechCapabilities.SIDED_CONFIG) return sidedConfigCap.cast();
		if(cap == ForgeCapabilities.FLUID_HANDLER)
		{
			var cfg = sidedConfig.getFor(SidedConfigTyped.FLUID, side);
			if(cfg == SideConfig.PUSH) return outputFluidHandler.cast();
		}
		
		return super.getCapability(cap, side);
	}
	
	private class FluidOutput
			implements IFluidHandler
	{
		@Override
		public int getTanks()
		{
			return fluidTank.getTanks();
		}
		
		@Override
		public @NotNull FluidStack getFluidInTank(int tank)
		{
			return fluidTank.getFluidInTank(tank);
		}
		
		@Override
		public int getTankCapacity(int tank)
		{
			return fluidTank.getTankCapacity(tank);
		}
		
		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack)
		{
			return false;
		}
		
		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			return 0;
		}
		
		@Override
		public @NotNull FluidStack drain(FluidStack resource, FluidAction action)
		{
			return fluidTank.drain(resource, action);
		}
		
		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction action)
		{
			return fluidTank.drain(maxDrain, action);
		}
	}
}