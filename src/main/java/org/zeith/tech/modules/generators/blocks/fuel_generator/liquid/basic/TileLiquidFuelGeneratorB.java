package org.zeith.tech.modules.generators.blocks.fuel_generator.liquid.basic;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import org.zeith.hammerlib.net.properties.PropertyFloat;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.recipes.processing.RecipeLiquidFuel;
import org.zeith.tech.api.tile.IFluidPipe;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.tile.slots.*;
import org.zeith.tech.modules.generators.init.SoundsZT_Generators;
import org.zeith.tech.modules.generators.init.TilesZT_Generators;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.shared.init.RecipeRegistriesZT;
import org.zeith.tech.modules.shared.init.SoundsZT;
import org.zeith.tech.utils.SerializableFluidTank;
import org.zeith.tech.utils.fluid.FluidHelperZT;
import org.zeith.tech.utils.fluid.FluidSmoothing;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

public class TileLiquidFuelGeneratorB
		extends TileBaseMachine<TileLiquidFuelGeneratorB>
		implements ITileSlotProvider
{
	@NBTSerializable("TankContents")
	public final SerializableFluidTank storage = new SerializableFluidTank(5 * FluidType.BUCKET_VOLUME, fluid ->
			ZeithTechAPI.get().getRecipeRegistries().liquidFuel().getRecipes().stream().anyMatch(r -> r.test(fluid)))
			.withColor(0xFFF3BA);
	
	public final FluidSmoothing tankSmooth;
	
	@NBTSerializable("Items")
	public final SimpleInventory inventory = new SimpleInventory(2);
	
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
			.setForAll(RelativeDirection.FRONT, SideConfig.DISABLE)
			.setFor(SidedConfigTyped.FLUID, RelativeDirection.BACK, SideConfig.DISABLE)
			.setFor(SidedConfigTyped.FLUID, RelativeDirection.RIGHT, SideConfig.DISABLE)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.BACK, SideConfig.PUSH);
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.LOW_VOLTAGE, EnumEnergyManagerKind.GENERATOR);
	
	public final PropertyInt fuelTicksTotal = new PropertyInt(DirectStorage.create(i -> _fuelTicksTotal = i, () -> _fuelTicksTotal));
	public final PropertyInt energyStored = new PropertyInt(DirectStorage.create(energy.fe::setEnergyStored, energy.fe::getEnergyStored));
	public final PropertyFloat fuelTicksLeft = new PropertyFloat(DirectStorage.create(i -> _fuelTicksLeft = i, () -> _fuelTicksLeft));
	
	@NBTSerializable("Generation")
	public int currentGenPerTick = 40;
	
	public TileLiquidFuelGeneratorB(BlockPos pos, BlockState state)
	{
		super(TilesZT_Generators.BASIC_LIQUID_FUEL_GENERATOR, pos, state);
		this.tankSmooth = new FluidSmoothing("display", this);
		
		inventory.stackSizeLimit = 1;
		
		// Let in only fluid containers that have a valid fuel in them!
		this.inventory.isStackValid = (idx, stack) -> idx == 0 && FluidHelperZT.anyFluidMatches(stack, storage::isFluidValid);
	}
	
	public TileLiquidFuelGeneratorB setCurrentGenPerTick(int currentGenPerTick)
	{
		this.currentGenPerTick = currentGenPerTick;
		return this;
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		tankSmooth.update(storage.getFluid());
		
		if(isOnServer())
		{
			var works = redstone.shouldWork(this);
			var input = inventory.getItem(0);
			
			if(_fuelTicksLeft <= 0 && (storage.isEmpty() || storage.getFluidAmount() < 100))
			{
				setEnabledState(false);
			}
			
			deposit_fuel_from_item:
			if(!input.isEmpty() && input.getCount() == 1)
			{
				var result = FluidUtil.tryEmptyContainer(input, storage, 1000, null, true);
				
				if(result.isSuccess())
					inventory.setItem(0, result.getResult());
				else if(inventory.getItem(1).isEmpty() && FluidHelperZT.isFluidContainerEmpty(input))
				{
					inventory.setItem(1, inventory.getItem(0));
					inventory.setItem(0, ItemStack.EMPTY);
				}
			}
			
			int needInput = storage.getCapacity() - storage.getFluidAmount();
			if(needInput > 0 && atTickRate(2))
			{
				for(Direction value : Direction.values())
					if(sidedConfig.getFor(SidedConfigTyped.FLUID, value) == SideConfig.PULL)
					{
						var be = level.getBlockEntity(worldPosition.relative(value));
						if(be instanceof IFluidPipe pipe)
						{
							if(atTickRate(100))
								pipe.createVacuum(storage.isEmpty() ? FluidIngredient.join(
										RecipeRegistriesZT.LIQUID_FUEL
												.getRecipes()
												.stream()
												.map(RecipeLiquidFuel::ingredient)
												.toArray(FluidIngredient[]::new)
								) : FluidIngredient.ofFluids(List.of(storage.getFluid())), 105);
							
							var in = pipe.extractFluidFromPipe(needInput, IFluidHandler.FluidAction.SIMULATE);
							if(storage.isFluidValid(in))
							{
								var store = storage.fill(in, IFluidHandler.FluidAction.EXECUTE);
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
			ZeithTechAPI.get()
					.getAudioSystem()
					.playMachineSoundLoop(this, SoundsZT_Generators.BASIC_LIQUID_FUEL_GENERATOR, SoundsZT.BASIC_MACHINE_INTERRUPT);
	}
	
	public void consumeFuel()
	{
		if(storage.isEmpty() || storage.getFluidAmount() < 100)
			return;
		
		int duration = ZeithTechAPI.get().getRecipeRegistries()
				.liquidFuel()
				.getRecipes()
				.stream()
				.filter(r -> r.test(storage.getFluid()))
				.mapToInt(RecipeLiquidFuel::burnTime)
				.max()
				.orElse(0);
		
		if(duration > 0 && storage.drain(100, IFluidHandler.FluidAction.SIMULATE).getAmount() == 100)
		{
			storage.drain(100, IFluidHandler.FluidAction.EXECUTE);
			_fuelTicksLeft = _fuelTicksTotal = duration;
			setEnabledState(true);
		}
	}
	
	@Override
	public boolean isInterrupted()
	{
		return false;
	}
	
	@Override
	public ContainerBaseMachine<TileLiquidFuelGeneratorB> openContainer(Player player, int windowId)
	{
		return new ContainerLiquidFuelGeneratorB(this, player, windowId);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(inventory, energy.batteryInventory);
	}
	
	private final LazyOptional<IFluidHandler> outputFluidHandler = LazyOptional.of(FluidInput::new);
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	private final LazyOptional<RedstoneControl> redstoneCap = LazyOptional.of(() -> redstone);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.FLUID_HANDLER && (side == null || sidedConfig.canAccess(SidedConfigTyped.FLUID, side))) return outputFluidHandler.cast();
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
		
		return lst.build();
	}).get();
	
	@Override
	public List<ISlot<?>> getSlots()
	{
		return slots;
	}
	
	class FluidInput
			implements IFluidHandler
	{
		@Override
		public int getTanks()
		{
			return storage.getTanks();
		}
		
		@Override
		public @NotNull FluidStack getFluidInTank(int tank)
		{
			return storage.getFluidInTank(tank);
		}
		
		@Override
		public int getTankCapacity(int tank)
		{
			return storage.getTankCapacity(tank);
		}
		
		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack)
		{
			return storage.isFluidValid(tank, stack);
		}
		
		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			return storage.fill(resource, action);
		}
		
		@Override
		public @NotNull FluidStack drain(FluidStack resource, FluidAction action)
		{
			return FluidStack.EMPTY;
		}
		
		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction action)
		{
			return FluidStack.EMPTY;
		}
	}
}