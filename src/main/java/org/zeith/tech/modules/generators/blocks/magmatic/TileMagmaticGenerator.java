package org.zeith.tech.modules.generators.blocks.magmatic;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
import org.zeith.hammerlib.net.properties.PropertyFloat;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.tile.IFluidPipe;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.tile.slots.*;
import org.zeith.tech.core.fluid.MultiTankHandler;
import org.zeith.tech.modules.generators.init.TilesZT_Generators;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.SoundsZT_Processing;
import org.zeith.tech.utils.SerializableFluidTank;
import org.zeith.tech.utils.fluid.FluidHelperZT;
import org.zeith.tech.utils.fluid.FluidSmoothing;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TileMagmaticGenerator
		extends TileBaseMachine<TileMagmaticGenerator>
		implements ITileSlotProvider
{
	public static final List<FluidIngredient> MATCHING_MAGMA = new ArrayList<>(List.of(FluidIngredient.ofTags(List.of(FluidTags.LAVA))));
	public static final List<FluidIngredient> MATCHING_COOLANT = new ArrayList<>(List.of(FluidIngredient.ofTags(List.of(FluidTags.WATER))));
	
	@NBTSerializable("Tank1Contents")
	public final SerializableFluidTank storage = new SerializableFluidTank(FluidType.BUCKET_VOLUME, fluid ->
			MATCHING_MAGMA.stream().anyMatch(r -> r.test(fluid)))
			.withColor(0xFFD5BA);
	
	@NBTSerializable("Tank2Contents")
	public final SerializableFluidTank coolant = new SerializableFluidTank(10 * FluidType.BUCKET_VOLUME, fluid ->
			MATCHING_COOLANT.stream().anyMatch(r -> r.test(fluid)))
			.withColor(0xBAFFFF);
	
	@NBTSerializable("Items")
	public final SimpleInventory inventory = new SimpleInventory(2);
	
	public final FluidSmoothing storageSmooth, coolantSmooth;
	
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	@NBTSerializable("FuelTotal")
	private int _fuelTicksTotal;
	
	@NBTSerializable("FuelLeft")
	private float _fuelTicksLeft;
	
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.FLUID))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.NONE)
			.setDefaults(SidedConfigTyped.FLUID, SideConfig.PULL)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.DISABLE)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.BACK, SideConfig.PUSH)
			.setForAll(RelativeDirection.UP, SideConfig.DISABLE)
			.setForAll(RelativeDirection.FRONT, SideConfig.DISABLE);
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.LOW_VOLTAGE, EnumEnergyManagerKind.GENERATOR);
	
	public final PropertyInt fuelTicksTotal = new PropertyInt(DirectStorage.create(i -> _fuelTicksTotal = i, () -> _fuelTicksTotal));
	public final PropertyInt energyStored = new PropertyInt(DirectStorage.create(energy.fe::setEnergyStored, energy.fe::getEnergyStored));
	public final PropertyFloat fuelTicksLeft = new PropertyFloat(DirectStorage.create(i -> _fuelTicksLeft = i, () -> _fuelTicksLeft));
	
	@NBTSerializable("Generation")
	public int currentGenPerTick = 40;
	
	public TileMagmaticGenerator(BlockPos pos, BlockState state)
	{
		super(TilesZT_Generators.MAGMATIC_GENERATOR, pos, state);
		this.storageSmooth = new FluidSmoothing("display", this);
		this.coolantSmooth = new FluidSmoothing("coolant", this);
		
		inventory.stackSizeLimit = 1;
		
		// Let in only fluid containers that have a valid fuel in them!
		this.inventory.isStackValid = (idx, stack) -> idx == 0 && (FluidHelperZT.anyFluidMatches(stack, storage::isFluidValid) || FluidHelperZT.anyFluidMatches(stack, coolant::isFluidValid));
	}
	
	public TileMagmaticGenerator setCurrentGenPerTick(int currentGenPerTick)
	{
		this.currentGenPerTick = currentGenPerTick;
		return this;
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		storageSmooth.update(storage.getFluid());
		coolantSmooth.update(coolant.getFluid());
		
		if(isOnServer())
		{
			var works = redstone.shouldWork(this);
			var input = inventory.getItem(0);
			
			deposit_fuel_from_item:
			if(!input.isEmpty() && input.getCount() == 1)
			{
				var result = FluidUtil.tryEmptyContainer(input, fluidHandler.resolve().orElseThrow(), 1000, null, true);
				
				if(result.isSuccess())
					inventory.setItem(0, result.getResult());
				else if(inventory.getItem(1).isEmpty() && FluidHelperZT.isFluidContainerEmpty(input))
				{
					inventory.setItem(1, inventory.getItem(0));
					inventory.setItem(0, ItemStack.EMPTY);
				}
			}
			
			int needInput = Math.max(storage.getCapacity() - storage.getFluidAmount(),
					coolant.getCapacity() - coolant.getFluidAmount());
			if(needInput > 0 && atTickRate(2))
			{
				for(Direction value : Direction.values())
					if(sidedConfig.getFor(SidedConfigTyped.FLUID, value) == SideConfig.PULL)
					{
						var be = level.getBlockEntity(worldPosition.relative(value));
						if(be instanceof IFluidPipe pipe)
						{
							if(atTickRate(100) && works)
							{
								List<FluidStack> fi = new ArrayList<>(2);
								int full = 0;
								
								if(!storage.isEmpty())
								{
									if(storage.getFluidAmount() < storage.getCapacity())
										fi.add(storage.getFluid());
									else ++full;
								}
								
								if(!coolant.isEmpty())
								{
									if(coolant.getFluidAmount() < coolant.getCapacity())
										fi.add(coolant.getFluid());
									else ++full;
								}
								
								if(full < 2)
									pipe.createVacuum(fi.isEmpty() ? FluidIngredient.join(
											Stream.concat(MATCHING_MAGMA.stream(), MATCHING_COOLANT.stream())
													.toArray(FluidIngredient[]::new)
									) : FluidIngredient.ofFluids(fi), 105);
							}
							
							var in = pipe.extractFluidFromPipe(needInput, IFluidHandler.FluidAction.SIMULATE);
							if(storage.isFluidValid(in))
							{
								var store = storage.fill(in, IFluidHandler.FluidAction.EXECUTE);
								pipe.extractFluidFromPipe(store, IFluidHandler.FluidAction.EXECUTE);
							} else if(coolant.isFluidValid(in))
							{
								var store = coolant.fill(in, IFluidHandler.FluidAction.EXECUTE);
								pipe.extractFluidFromPipe(store, IFluidHandler.FluidAction.EXECUTE);
							}
						}
					}
			}
			
			if(works && energy.fe.getEnergyTillFull() > 0 && _fuelTicksLeft <= 0)
				consumeFuel();
			
			if(_fuelTicksLeft > 0)
			{
				if(!isEnabled())
					setEnabledState(true);
				
				float take = Math.min(1, _fuelTicksLeft);
				if(works && energy.generateAnyEnergy(Math.round(currentGenPerTick * take)))
					_fuelTicksLeft -= take;
				else
					_fuelTicksLeft -= 0.05F;
				
				if(_fuelTicksLeft <= 0)
				{
					if(energy.fe.getEnergyTillFull() > 0 && works)
						consumeFuel();
					if(_fuelTicksLeft <= 0)
						setEnabledState(false);
				}
			}
		}
		
		if(isOnClient() && isEnabled() && !isInterrupted())
		{
			ZeithTechAPI.get()
					.getAudioSystem()
					.playMachineSoundLoop(this, SoundsZT_Processing.MAGMATIC_GENERATOR, SoundsZT_Processing.BASIC_MACHINE_INTERRUPT);
			
			var rng = level.getRandom();
			
			if(rng.nextInt(3) == 0)
			{
				double xCenter = (double) worldPosition.getX() + 0.5;
				double yMin = (double) worldPosition.getY() + 0.1875;
				double zCenter = (double) worldPosition.getZ() + 0.5;
				
				Direction front = getFront();
				
				Direction.Axis axis = front.getAxis();
				double d3 = 0.52D;
				
				double y = rng.nextDouble() * 5.0D / 16.0D;
				
				double faceSpread = rng.nextDouble() * 0.5D - 0.25D;
				double x = axis == Direction.Axis.X ? (double) front.getStepX() * d3 : faceSpread;
				double z = axis == Direction.Axis.Z ? (double) front.getStepZ() * d3 : faceSpread;
				
				Vec3 dir = Vec3.atLowerCornerOf(front.getNormal()).scale(0.025F);
				
				level.addParticle(ParticleTypes.SMOKE, xCenter + x, yMin + y, zCenter + z, dir.x, dir.y, dir.z);
			}
		}
	}
	
	public void consumeFuel()
	{
		if(storage.isEmpty() || storage.getFluidAmount() < 10)
			return;
		
		if(storage.drain(10, IFluidHandler.FluidAction.SIMULATE).getAmount() == 10 && coolant.drain(100, IFluidHandler.FluidAction.SIMULATE).getAmount() == 100)
		{
			storage.drain(10, IFluidHandler.FluidAction.EXECUTE);
			coolant.drain(100, IFluidHandler.FluidAction.EXECUTE);
			_fuelTicksLeft = _fuelTicksTotal = 50;
			setEnabledState(true);
		}
	}
	
	@Override
	public ContainerBaseMachine<TileMagmaticGenerator> openContainer(Player player, int windowId)
	{
		return new ContainerMagmaticGenerator(this, player, windowId);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(energy.batteryInventory);
	}
	
	private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> new MultiTankHandler(new IFluidTank[] {
			storage,
			coolant
	}, new int[] {
			0,
			1
	}, new int[0]));
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	private final LazyOptional<RedstoneControl> redstoneCap = LazyOptional.of(() -> redstone);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.FLUID_HANDLER && (side == null || sidedConfig.canAccess(SidedConfigTyped.FLUID, side))) return fluidHandler.cast();
		if(cap == ForgeCapabilities.ENERGY && (side == null || sidedConfig.canAccess(SidedConfigTyped.ENERGY, side))) return energyCap.cast();
		if(cap == ZeithTechCapabilities.REDSTONE_CONTROL) return redstoneCap.cast();
		if(cap == ZeithTechCapabilities.SIDED_CONFIG) return sidedConfigCap.cast();
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE) return energy.measurableCap.cast();
		return super.getCapability(cap, side);
	}
	
	public final List<ISlot<?>> slots = ((Supplier<List<ISlot<?>>>) () ->
	{
		ImmutableList.Builder<ISlot<?>> lst = new ImmutableList.Builder<>();
		
		lst.add(energy.createSlot());
		lst.add(ISlot.simpleSlot(new FluidTankSlotAccess(storage, SlotRole.INPUT), SlotRole.INPUT));
		lst.add(ISlot.simpleSlot(new FluidTankSlotAccess(coolant, SlotRole.INPUT), SlotRole.INPUT));
		
		return lst.build();
	}).get();
	
	@Override
	public List<ISlot<?>> getSlots()
	{
		return slots;
	}
}
