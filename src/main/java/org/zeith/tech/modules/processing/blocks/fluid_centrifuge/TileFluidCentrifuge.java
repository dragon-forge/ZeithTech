package org.zeith.tech.modules.processing.blocks.fluid_centrifuge;

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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.hammerlib.util.physics.FrictionRotator;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.recipes.processing.RecipeFluidCentrifuge;
import org.zeith.tech.api.tile.IFluidPipe;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.tile.slots.*;
import org.zeith.tech.api.utils.InventoryHelper;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.*;
import org.zeith.tech.utils.SerializableFluidTank;
import org.zeith.tech.utils.SidedInventory;
import org.zeith.tech.utils.fluid.FluidSmoothing;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class TileFluidCentrifuge
		extends TileBaseMachine<TileFluidCentrifuge>
		implements ITileSlotProvider
{
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.ITEM, SidedConfigTyped.FLUID))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.DISABLE)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.DISABLE)
			.setDefaults(SidedConfigTyped.FLUID, SideConfig.DISABLE)
			.setFor(SidedConfigTyped.FLUID, RelativeDirection.FRONT, SideConfig.PULL)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.LEFT, SideConfig.PULL)
			.setFor(SidedConfigTyped.ENERGY, RelativeDirection.DOWN, SideConfig.PULL)
			.setFor(SidedConfigTyped.ITEM, RelativeDirection.RIGHT, SideConfig.PUSH)
			.setFor(SidedConfigTyped.FLUID, RelativeDirection.BACK, SideConfig.PUSH);
	
	@NBTSerializable("Items")
	public final SidedInventory inventory = new SidedInventory(1, sidedConfig.createItemAccess(new int[0], new int[] { 0 }));
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.EXTRA_LOW_VOLTAGE, EnumEnergyManagerKind.CONSUMER);
	
	@NBTSerializable("Accumulated")
	public int accumulated;
	
	@NBTSerializable("OutSmooth")
	public final FluidSmoothing outputTank;
	
	@NBTSerializable("InputFluid")
	public final SerializableFluidTank inputFluid = new SerializableFluidTank(1000, fluid -> RecipeRegistriesZT_Processing.FLUID_CENTRIFUGE.getRecipes().stream().anyMatch(r -> r.getInput().fluid().test(fluid)));
	
	@NBTSerializable("OutputFluid")
	public final SerializableFluidTank outputFluid = new SerializableFluidTank(1000);
	
	public final FrictionRotator rotator = new FrictionRotator();
	
	public TileFluidCentrifuge(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.FLUID_CENTRIFUGE, pos, state);
		rotator.friction = 1F;
		inventory.isStackValid = (slot, stack) -> false;
		this.outputTank = new FluidSmoothing("out_display", this);
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		rotator.update();
		
		if(isOnClient() && isEnabled() && !isInterrupted())
		{
			rotator.speedupTo(45F, 3F);
			
			ZeithTechAPI.get()
					.getAudioSystem()
					.playMachineSoundLoop(this, SoundsZT_Processing.FLUID_CENTRIFUGE, SoundsZT_Processing.BASIC_MACHINE_INTERRUPT);
		}
		
		int needInput = inputFluid.getCapacity() - inputFluid.getFluidAmount();
		if(isOnServer() && needInput > 0 && atTickRate(2) && redstone.shouldWork(this))
		{
			var be = level.getBlockEntity(worldPosition.relative(getFront()));
			if(be instanceof IFluidPipe pipe)
			{
				if(atTickRate(100))
					pipe.createVacuum(inputFluid.isEmpty() ? FluidIngredient.EMPTY : FluidIngredient.ofFluids(List.of(inputFluid.getFluid())), 105);
				
				var in = pipe.extractFluidFromPipe(needInput, IFluidHandler.FluidAction.SIMULATE);
				if(inputFluid.isFluidValid(in))
				{
					var store = inputFluid.fill(in, IFluidHandler.FluidAction.EXECUTE);
					pipe.extractFluidFromPipe(store, IFluidHandler.FluidAction.EXECUTE);
				}
			}
		}
		
		outputTank.update(outputFluid.getFluid());
		
		if(isOnServer() && activeRecipe != null)
		{
			var recipe = activeRecipe;
			
			int need = recipe.getEnergy() - accumulated;
			
			int toConsume = Math.min(need, 20);
			if(redstone.shouldWork(this) && energy.consumeEnergy(toConsume))
			{
				accumulated += toConsume;
				isInterrupted.setBool(false);
			} else
				isInterrupted.setBool(true);
		}
		
		if(isOnServer() && atTickRate(2))
		{
			var recipe = getActiveRecipe();
			
			var active = recipe != null;
			
			if(active != isEnabled())
				setEnabledState(active);
			
			recipe:
			if(active)
			{
				if(recipe.getEnergy() <= accumulated)
				{
					accumulated = 0;
					var drained = inputFluid.drain(recipe.getInput().amount(), IFluidHandler.FluidAction.SIMULATE);
					if(recipe.getInput().test(drained))
					{
						var canStoreExtra = recipe.getExtra().map(out -> InventoryHelper.storeStack(inventory, IntStream.of(0), out.getMinimalResult(), true)).orElse(true);
						
						if(canStoreExtra)
						{
							var filled = outputFluid.fill(recipe.getOutput(), IFluidHandler.FluidAction.SIMULATE);
							if(filled == recipe.getOutputAmount())
							{
								outputFluid.fill(recipe.getOutput(), IFluidHandler.FluidAction.EXECUTE);
								inputFluid.drain(recipe.getInput().amount(), IFluidHandler.FluidAction.EXECUTE);
								recipe.getExtra().ifPresent(out -> InventoryHelper.storeStack(inventory, IntStream.of(0), out.assemble(level.getRandom()), false));
							}
						}
					}
				}
			}
			
			push_contents:
			{
				inventory.setItem(0, storeAnything(RelativeDirection.getAbsolute(getFront(), RelativeDirection.RIGHT), inventory.getItem(0), false));
				
				relativeFluidHandler(RelativeDirection.getAbsolute(getFront(), RelativeDirection.BACK))
						.ifPresent(handler -> FluidUtil.tryFluidTransfer(handler, outputFluid, 50, true));
			}
		}
	}
	
	public ItemStack storeAnything(Direction to, ItemStack contents, boolean simulate)
	{
		return relativeItemHandler(to).map(handler ->
		{
			ItemStack modified = contents.copy();
			
			var slots = handler.getSlots();
			for(int i = 0; i < slots; ++i)
			{
				var remaining = handler.insertItem(i, modified, simulate || isOnClient());
				if(!ItemStack.matches(contents, remaining))
				{
					if(remaining.isEmpty() || simulate)
						return ItemStack.EMPTY;
					modified.setCount(remaining.getCount());
				}
			}
			
			return modified;
		}).orElse(contents);
	}
	
	private LazyOptional<IItemHandler> relativeItemHandler(Direction to)
	{
		var be = level.getBlockEntity(worldPosition.relative(to));
		return be == null ? LazyOptional.empty() : be.getCapability(ForgeCapabilities.ITEM_HANDLER, to.getOpposite());
	}
	
	private LazyOptional<IFluidHandler> relativeFluidHandler(Direction to)
	{
		var be = level.getBlockEntity(worldPosition.relative(to));
		return be == null ? LazyOptional.empty() : be.getCapability(ForgeCapabilities.FLUID_HANDLER, to.getOpposite());
	}
	
	RecipeFluidCentrifuge activeRecipe;
	
	public RecipeFluidCentrifuge getActiveRecipe()
	{
		if(activeRecipe == null || !activeRecipe.getInput().test(inputFluid.getFluid()))
			activeRecipe = RecipeRegistriesZT_Processing.FLUID_CENTRIFUGE
					.getRecipes()
					.stream()
					.filter(r -> r.getInput().test(inputFluid.getFluid()))
					.findFirst().orElse(null);
		return activeRecipe;
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(inventory, energy.batteryInventory);
	}
	
	@Override
	public ContainerBaseMachine<TileFluidCentrifuge> openContainer(Player player, int windowId)
	{
		return null;
	}
	
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<? extends IItemHandler>[] itemHandlers = SidedInvWrapper.create(inventory, Direction.values());
	private final LazyOptional<IFluidHandler> inputFluidHandler = LazyOptional.of(FluidInput::new);
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
		if(cap == ForgeCapabilities.ITEM_HANDLER && (side == null || sidedConfig.canAccess(SidedConfigTyped.ITEM, side))) return itemHandlers[side == null ? 0 : side.ordinal()].cast();
		if(cap == ForgeCapabilities.FLUID_HANDLER)
		{
			var cfg = sidedConfig.getFor(SidedConfigTyped.FLUID, side);
			if(cfg == SideConfig.PULL) return inputFluidHandler.cast();
			if(cfg == SideConfig.PUSH) return outputFluidHandler.cast();
		}
		
		return super.getCapability(cap, side);
	}
	
	public final List<ISlot<?>> slots = ((Supplier<List<ISlot<?>>>) () ->
	{
		ImmutableList.Builder<ISlot<?>> lst = new ImmutableList.Builder<>();
		
		lst.add(energy.createSlot());
		
		lst.add(ISlot.simpleSlot(new FluidTankSlotAccess(inputFluid, SlotRole.INPUT), SlotRole.INPUT));
		lst.add(ISlot.simpleSlot(new FluidTankSlotAccess(outputFluid, SlotRole.OUTPUT), SlotRole.OUTPUT));
		
		Direction.stream()
				.flatMap(dir ->
						IntStream.of(inventory.getSlotsForFace(dir))
								.mapToObj(slot -> inventory.sidedItemAccess.canTakeItemThroughFace(slot, dir)
										? Tuples.immutable(slot, inventory.sidedItemAccess.canPlaceItemThroughFace(slot, dir) ? SlotRole.BOTH : SlotRole.OUTPUT)
										: inventory.sidedItemAccess.canPlaceItemThroughFace(slot, dir)
										? Tuples.immutable(slot, SlotRole.INPUT)
										: null)
				)
				.filter(Objects::nonNull)
				.distinct()
				.map(pair -> ISlot.simpleSlot(new ContainerItemSlotAccess(inventory, pair.a(), pair.b()), pair.b(), pair.toString(), getClass().getSimpleName()))
				.forEach(lst::add);
		
		return lst.build();
	}).get();
	
	@Override
	public List<ISlot<?>> getSlots()
	{
		return slots;
	}
	
	private class FluidInput
			implements IFluidHandler
	{
		@Override
		public int getTanks()
		{
			return inputFluid.getTanks();
		}
		
		@Override
		public @NotNull FluidStack getFluidInTank(int tank)
		{
			return inputFluid.getFluidInTank(tank);
		}
		
		@Override
		public int getTankCapacity(int tank)
		{
			return inputFluid.getTankCapacity(tank);
		}
		
		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack)
		{
			return inputFluid.isFluidValid(tank, stack);
		}
		
		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			return inputFluid.fill(resource, action);
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
	
	private class FluidOutput
			implements IFluidHandler
	{
		@Override
		public int getTanks()
		{
			return outputFluid.getTanks();
		}
		
		@Override
		public @NotNull FluidStack getFluidInTank(int tank)
		{
			return outputFluid.getFluidInTank(tank);
		}
		
		@Override
		public int getTankCapacity(int tank)
		{
			return outputFluid.getTankCapacity(tank);
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
			return outputFluid.drain(resource, action);
		}
		
		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction action)
		{
			return outputFluid.drain(maxDrain, action);
		}
	}
}