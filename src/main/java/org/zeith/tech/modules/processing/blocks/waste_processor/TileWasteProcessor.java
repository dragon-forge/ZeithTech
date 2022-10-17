package org.zeith.tech.modules.processing.blocks.waste_processor;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.hammerlib.util.physics.FrictionRotator;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.misc.Tuple2;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.processing.RecipeWasteProcessor;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.tile.slots.*;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.fluid.MultiTankHandler;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.*;
import org.zeith.tech.utils.*;
import org.zeith.tech.utils.fluid.FluidSmoothing;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class TileWasteProcessor
		extends TileBaseMachine<TileWasteProcessor>
		implements ITileSlotProvider
{
	public static final ResourceLocation WASTE_PROCESSOR_GUI_TEXTURE = new ResourceLocation(ZeithTech.MOD_ID, "textures/processing/gui/waste_processor.png");
	
	public final FrictionRotator rotator = new FrictionRotator();
	
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.ITEM, SidedConfigTyped.FLUID))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.PULL)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.NONE)
			.setDefaults(SidedConfigTyped.FLUID, SideConfig.NONE)
			.setForAll(RelativeDirection.UP, SideConfig.DISABLE);
	
	@NBTSerializable("InputA")
	public final SerializableFluidTank input_a = new SerializableFluidTank(5000, this::canFitFluidA);
	
	@NBTSerializable("InputB")
	public final SerializableFluidTank input_b = new SerializableFluidTank(5000, this::canFitFluidB);
	
	@NBTSerializable("Items")
	public final SidedInventory inventory = new SidedInventory(4, sidedConfig.createItemAccess(new int[] { 0 }, new int[] {
			1,
			2,
			3
	}));
	
	@NBTSerializable("OutputA")
	public final SerializableFluidTank output_a = new SerializableFluidTank(5000);
	
	@NBTSerializable("OutputB")
	public final SerializableFluidTank output_b = new SerializableFluidTank(5000);
	
	public final FluidSmoothing tank1, tank2, tank3, tank4;
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(20000, 128, 0);
	
	@NBTSerializable("Progress")
	public int _progress;
	
	@NBTSerializable("MaxProgress")
	public int _maxProgress = 200;
	
	public final PropertyInt energyStored = new PropertyInt(DirectStorage.create(energy.fe::setEnergyStored, energy.fe::getEnergyStored));
	public final PropertyInt progress = new PropertyInt(DirectStorage.create(i -> _progress = i, () -> _progress));
	public final PropertyInt maxProgress = new PropertyInt(DirectStorage.create(i -> _maxProgress = i, () -> _maxProgress));
	
	public TileWasteProcessor(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.WASTE_PROCESSOR, pos, state);
		
		this.tank1 = new FluidSmoothing("tank1", this);
		this.tank2 = new FluidSmoothing("tank2", this);
		this.tank3 = new FluidSmoothing("tank3", this);
		this.tank4 = new FluidSmoothing("tank4", this);
		
		this.rotator.friction = 1F;
		this.inventory.isStackValid = (slot, stack) -> slot == 0 && canInputItem(stack);
	}
	
	public boolean canFitFluidA(FluidStack stack)
	{
		return RecipeRegistriesZT_Processing.WASTE_PROCESSING.getRecipes()
				.stream()
				.anyMatch(recipe -> recipe.getInputA().fluid().test(stack));
	}
	
	public boolean canFitFluidB(FluidStack stack)
	{
		return !input_a.isEmpty() &&
				RecipeRegistriesZT_Processing.WASTE_PROCESSING.getRecipes()
						.stream()
						.anyMatch(recipe -> recipe.getInputA().fluid().test(input_a.getFluid()) && recipe.getInputB().fluid().test(stack));
	}
	
	public boolean canInputItem(ItemStack stack)
	{
		if(input_a.isEmpty() && input_b.isEmpty())
		{
			return RecipeRegistriesZT_Processing.WASTE_PROCESSING.getRecipes()
					.stream()
					.anyMatch(recipe ->
							recipe.getInputA().isEmpty()
									&& recipe.getInputB().isEmpty()
									&& recipe.getInputItem().test(stack)
					);
		} else if(input_b.isEmpty())
		{
			return RecipeRegistriesZT_Processing.WASTE_PROCESSING.getRecipes()
					.stream()
					.anyMatch(recipe ->
							recipe.getInputA().test(input_a.getFluid())
									&& recipe.getInputB().isEmpty()
									&& recipe.getInputItem().test(stack)
					);
		} else
		{
			return RecipeRegistriesZT_Processing.WASTE_PROCESSING.getRecipes()
					.stream()
					.anyMatch(recipe ->
							recipe.getInputA().test(input_a.getFluid())
									&& recipe.getInputB().test(input_b.getFluid())
									&& recipe.getInputItem().test(stack)
					);
		}
	}
	
	protected int getConsumptionPerTick()
	{
		return 40;
	}
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		
		tank1.update(input_a.getFluid());
		tank2.update(input_b.getFluid());
		tank3.update(output_a.getFluid());
		tank4.update(output_b.getFluid());
		
		if(isOnClient())
		{
			this.rotator.update();
			if(isEnabled())
				this.rotator.speedupTo(100, 5);
		}
		
		if(isOnServer())
		{
			getActiveRecipe().ifPresentOrElse(recipe ->
			{
				maxProgress.setInt(recipe.getTime());
				
				if(_progress < _maxProgress && isOnServer()
						&& (_progress > 0 || storeRecipeResult(recipe, true)))
				{
					if(energy.consumeEnergy(getConsumptionPerTick()))
					{
						_progress += 1;
						isInterrupted.setBool(false);
					} else
					{
						isInterrupted.setBool(true);
					}
				}
				
				var enable = _progress > 0;
				if(isEnabled() != enable)
					setEnabledState(enable);
				
				if(_progress >= _maxProgress && storeRecipeResult(recipe, false))
				{
					if(!recipe.getInputItem().isEmpty())
						inventory.getItem(0).shrink(1);
					
					if(!recipe.getInputA().isEmpty())
						input_a.drain(recipe.getInputA().amount(), IFluidHandler.FluidAction.EXECUTE);
					
					if(!recipe.getInputB().isEmpty())
						input_b.drain(recipe.getInputB().amount(), IFluidHandler.FluidAction.EXECUTE);
					
					_progress = 0;
					
					sync();
				}
			}, () ->
			{
				if(_progress > 0)
					_progress = Math.max(0, _progress - 2);
				else if(isEnabled())
					setEnabledState(false);
			});
		}
		
		if(isOnClient() && isEnabled() && !isInterrupted())
			ZeithTechAPI.get()
					.getAudioSystem()
					.playMachineSoundLoop(this, SoundsZT_Processing.WASTE_PROCESSOR, SoundsZT_Processing.BASIC_MACHINE_INTERRUPT);
	}
	
	protected boolean storeRecipeResult(RecipeWasteProcessor recipe, boolean simulate)
	{
		var fa = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
		
		var oa = recipe.getOutputA();
		var ob = recipe.getOutputB();
		
		if(output_a.fill(oa, fa) != oa.getAmount())
			return false;
		
		var byproducts = recipe.getByproduct();
		if(simulate && !byproducts.stream().allMatch(e -> storeExtra(e, true)))
			return false;
		else if(!simulate && !byproducts.isEmpty())
		{
			var extra = byproducts.get(level.getRandom().nextInt(byproducts.size()));
			if(!storeExtra(extra, false))
				return false;
		}
		
		if(ob.isEmpty())
			return true;
		
		return output_b.fill(ob, fa) == ob.getAmount();
	}
	
	public boolean storeExtra(ExtraOutput extra, boolean simulate)
	{
		if(extra != null)
		{
			if(simulate && !storeExtra(extra.getMinimalResult(), true))
				return false;
			
			if(!simulate && level.getRandom().nextFloat() <= extra.chance())
				storeExtra(extra.assemble(level.getRandom()), false);
		}
		
		return true;
	}
	
	public boolean storeExtra(ItemStack stacks, boolean simulate)
	{
		return InventoryHelper.storeStack(inventory, IntStream.range(1, inventory.getSlots()), stacks, simulate);
	}
	
	private ResourceLocation lastRecipeId;
	
	public Optional<RecipeWasteProcessor> getActiveRecipe()
	{
		if(lastRecipeId != null)
		{
			var r = RecipeRegistriesZT_Processing.WASTE_PROCESSING.getRecipe(lastRecipeId);
			if(r != null && matches(r)) return Optional.of(r);
			lastRecipeId = null;
		}
		
		if(atTickRate(5))
		{
			var r = RecipeRegistriesZT_Processing.WASTE_PROCESSING.getRecipes()
					.stream()
					.filter(this::matches)
					.findFirst();
			
			r.map(RecipeWasteProcessor::getRecipeName).ifPresentOrElse(id -> lastRecipeId = id, () -> lastRecipeId = null);
			
			return r;
		}
		
		return Optional.empty();
	}
	
	public boolean matches(RecipeWasteProcessor recipe)
	{
		var inA = recipe.getInputA();
		var inB = recipe.getInputB();
		
		return ((inA.isEmpty() && input_a.isEmpty()) || inA.test(input_a.getFluid()))
				&& ((inB.isEmpty() && input_b.isEmpty()) || inB.test(input_b.getFluid()))
				&& recipe.getInputItem().test(inventory.getItem(0));
	}
	
	@Override
	public ContainerBaseMachine<TileWasteProcessor> openContainer(Player player, int windowId)
	{
		return new ContainerWasteProcessor(this, player, windowId);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(inventory, energy.batteryInventory);
	}
	
	public final List<ISlot<?>> slots = ((Supplier<List<ISlot<?>>>) () ->
	{
		ImmutableList.Builder<ISlot<?>> lst = new ImmutableList.Builder<>();
		
		lst.add(energy.createSlot());
		
		lst.add(ISlot.simpleSlot(new FluidTankSlotAccess(input_a, SlotRole.BOTH), SlotRole.BOTH));
		lst.add(ISlot.simpleSlot(new FluidTankSlotAccess(input_b, SlotRole.BOTH), SlotRole.BOTH));
		
		lst.add(ISlot.simpleSlot(new FluidTankSlotAccess(output_a, SlotRole.OUTPUT), SlotRole.OUTPUT));
		lst.add(ISlot.simpleSlot(new FluidTankSlotAccess(output_b, SlotRole.OUTPUT), SlotRole.OUTPUT));
		
		Direction.stream()
				.flatMap(dir ->
						IntStream.of(inventory.getSlotsForFace(dir))
								.mapToObj(slot -> inventory.sidedItemAccess.canTakeItemThroughFace(slot, dir)
										? new Tuple2<>(slot, inventory.sidedItemAccess.canPlaceItemThroughFace(slot, dir) ? SlotRole.BOTH : SlotRole.OUTPUT)
										: inventory.sidedItemAccess.canPlaceItemThroughFace(slot, dir)
										? new Tuple2<>(slot, SlotRole.INPUT)
										: null)
				)
				.filter(Objects::nonNull)
				.distinct()
				.map(pair -> ISlot.simpleSlot(new ContainerItemSlotAccess(inventory, pair.first(), pair.second()), pair.second(), pair.toString(), getClass().getSimpleName()))
				.forEach(lst::add);
		
		return lst.build();
	}).get();
	
	@Override
	public List<ISlot<?>> getSlots()
	{
		return slots;
	}
	
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<? extends IItemHandler>[] itemHandlers = SidedInvWrapper.create(inventory, Direction.values());
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	private final LazyOptional<IFluidHandler> outputFluidHandler = LazyOptional.of(() -> new MultiTankHandler(
			new IFluidTank[] {
					input_a,
					input_b,
					output_a,
					output_b
			},
			new int[] {
					0,
					1
			},
			new int[] {
					2,
					3
			}
	));
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.FLUID_HANDLER && (side == null || sidedConfig.canAccess(SidedConfigTyped.FLUID, side)))
			return outputFluidHandler.cast();
		if(cap == ForgeCapabilities.ENERGY && (side == null || sidedConfig.canAccess(SidedConfigTyped.ENERGY, side)))
			return energyCap.cast();
		if(cap == ZeithTechCapabilities.SIDED_CONFIG)
			return sidedConfigCap.cast();
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE)
			return energy.measurableCap.cast();
		if(cap == ForgeCapabilities.ITEM_HANDLER && (side == null || sidedConfig.canAccess(SidedConfigTyped.ITEM, side)))
			return itemHandlers[side == null ? 0 : side.ordinal()].cast();
		return super.getCapability(cap, side);
	}
}