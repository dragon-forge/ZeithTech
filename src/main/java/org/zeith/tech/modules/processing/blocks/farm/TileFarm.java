package org.zeith.tech.modules.processing.blocks.farm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyBool;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.block.multiblock.base.MultiBlockFormer;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.misc.farm.*;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.multiblock.IMultiblockTile;
import org.zeith.tech.api.utils.CodecHelper;
import org.zeith.tech.api.utils.InventoryHelper;
import org.zeith.tech.core.fluid.MultiTankHandler;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.blocks.farm.actions.*;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;
import org.zeith.tech.modules.processing.items.ItemFarmSoC;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.utils.SerializableFluidTank;
import org.zeith.tech.utils.fluid.FluidSmoothing;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class TileFarm
		extends TileBaseMachine<TileFarm>
		implements IFarmController, IMultiblockTile
{
	public static final FluidIngredient WATER_INPUT = FluidIngredient.ofTags(List.of(FluidTags.WATER));
	
	@NBTSerializable("Fluids")
	public final SerializableFluidTank water = new SerializableFluidTank(8000, WATER_INPUT);
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.MEDIUM_VOLTAGE, EnumEnergyManagerKind.CONSUMER);
	
	public final FluidSmoothing tankSmooth;
	
	private FarmAlgorithm algorithm;
	
	@NBTSerializable("Algorithms")
	public final SimpleInventory algorithmInventory = new SimpleInventory(1);
	
	@NBTSerializable("SoilItems")
	public final SimpleInventory soilInventory = new SimpleInventory(2);
	
	@NBTSerializable("PlantItems")
	public final SimpleInventory plantInventory = new SimpleInventory(4);
	
	@NBTSerializable("FertilizerItems")
	public final SimpleInventory fertilizerInventory = new SimpleInventory(1);
	
	@NBTSerializable("ResultItems")
	public final SimpleInventory resultInventory = new SimpleInventory(6);
	
	@NBTSerializable("IsValid")
	public boolean isValid = true;
	
	@NBTSerializable("Cooldown")
	public int cooldown;
	
	@NBTSerializable("Position")
	public int positionIdx;
	
	public boolean checkNow = false;
	
	public final List<PlaceBlockAction> placeActions = new ArrayList<>();
	public final List<BreakBlockAction> breakActions = new ArrayList<>();
	public final List<TransformBlockAction> transformActions = new ArrayList<>();
	
	public final PropertyBool isValidSynced = new PropertyBool(DirectStorage.create(v -> isValid = v, () -> isValid));
	
	private final Map<BlockPos, VoxelShape> fixedShapes = new HashMap<>();
	
	public TileFarm(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.FARM, pos, state);
		
		this.tankSmooth = new FluidSmoothing("display", this);
		dispatcher.registerProperty("formed", isValidSynced);
		
		algorithmInventory.isStackValid = (slot, stack) -> stack.getItem() instanceof ItemFarmSoC soc && soc.getAlgorithm(stack) != null;
		fertilizerInventory.isStackValid = (slot, stack) -> Optional.ofNullable(getAlgorithm()).map(a -> a.categorizeItem(this, stack) == EnumFarmItemCategory.FERTILIZER).orElse(false);
		soilInventory.isStackValid = (slot, stack) -> Optional.ofNullable(getAlgorithm()).map(a -> a.categorizeItem(this, stack) == EnumFarmItemCategory.SOIL).orElse(false);
		plantInventory.isStackValid = (slot, stack) -> Optional.ofNullable(getAlgorithm()).map(a -> a.categorizeItem(this, stack) == EnumFarmItemCategory.PLANT).orElse(false);
		resultInventory.isStackValid = (slot, stack) -> false;
		
		fixedShapes.put(pos.above(), Shapes.block());
		fixedShapes.put(pos.above(), Shapes.block().move(0, 1 / 16F, 0));
		fixedShapes.put(pos.above(2), Block.box(4, 1, 4, 12, 17, 12));
		fixedShapes.put(pos.above().north(), Block.box(0, 0, 8, 16, 9, 16));
		fixedShapes.put(pos.above().north().east(), Block.box(0, 0, 8, 8, 9, 16));
		fixedShapes.put(pos.above().north().west(), Block.box(8, 0, 8, 16, 9, 16));
		fixedShapes.put(pos.above().west(), Block.box(8, 0, 0, 16, 9, 16));
		fixedShapes.put(pos.above().east(), Block.box(0, 0, 0, 8, 9, 16));
		fixedShapes.put(pos.above().south(), Block.box(0, 0, 0, 16, 9, 8));
		fixedShapes.put(pos.above().south().east(), Block.box(0, 0, 0, 8, 9, 8));
		fixedShapes.put(pos.above().south().west(), Block.box(8, 0, 0, 16, 9, 8));
	}
	
	@Override
	public void update()
	{
		tankSmooth.update(water.getFluid());
		energy.update(level, worldPosition, null);
		
		var algo = algorithmInventory.getItem(0);
		if(!algo.isEmpty() && algo.getItem() instanceof ItemFarmSoC soc) algorithm = soc.getAlgorithm(algo);
		else algorithm = null;
		
		if((checkNow || atTickRate(40)) && isOnServer() && level != null)
		{
			var former = getFormer();
			var dir = getFront();
			
			isValidSynced.setBool(former != null && former.test(level, worldPosition, dir));
			if(former != null)
			{
				if(!isValid)
					former.deform(level, worldPosition, getFront());
				else
					former.placeMultiBlock(level, worldPosition, dir, null);
			}
			
			if(former != null && isValid)
			{
				var positions = former.getPositionsFrom(worldPosition, dir);
				
				for(var pos : positions)
				{
					var state = TileMultiBlockPart.getPartState(level, pos);
					
				}
			}
			
			checkNow = false;
		}
		
		if(atTickRate(5) && level instanceof ServerLevel server)
		{
			if(!breakActions.isEmpty())
			{
				var action = breakActions.remove(0);
				
				var blockDrops = InventoryHelper.getBlockDropsAt(server, action.pos());
				
				// Create copy of all drops!
				List<ItemStack> blockDropsCopy = new ArrayList<>(blockDrops);
				blockDropsCopy.replaceAll(ItemStack::copy);
				
				if(InventoryHelper.storeAllStacks(resultInventory, IntStream.range(0, resultInventory.getSlots()), blockDrops, true))
				{
					InventoryHelper.storeAllStacks(resultInventory, IntStream.range(0, resultInventory.getSlots()), blockDropsCopy, false);
					level.destroyBlock(action.pos(), false);
				}
			} else if(!transformActions.isEmpty())
			{
				var action = transformActions.remove(0);
				
				int needWater = action.waterUsage();
				var efluid = water.drain(needWater, IFluidHandler.FluidAction.SIMULATE);
				
				if(level.getBlockState(action.pos()).equals(action.source())
						&& (needWater == 0 || (!efluid.isEmpty() && efluid.getAmount() == needWater))
						&& energy.consumeEnergy(64))
				{
					if(needWater > 0) water.drain(needWater, IFluidHandler.FluidAction.EXECUTE);
					server.setBlockAndUpdate(action.pos(), action.dest());
					
					var sound = action.dest().getSoundType();
					server.playSound(null, action.pos(), sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
				}
			} else if(!placeActions.isEmpty())
			{
				var action = placeActions.remove(0);
				
				int needWater = action.waterUsage();
				var efluid = water.drain(needWater, IFluidHandler.FluidAction.SIMULATE);
				
				if(action.canPlace(this, server) && (needWater == 0 || (!efluid.isEmpty() && efluid.getAmount() == needWater)) && energy.consumeEnergy(64))
				{
					if(needWater > 0) water.drain(needWater, IFluidHandler.FluidAction.EXECUTE);
					action.item().consumeItem(this, false);
					server.setBlockAndUpdate(action.pos(), action.state());
					
					var sound = action.state().getSoundType();
					
					server.playSound(null, action.pos(), sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
				}
			}
		}
		
		if(atTickRate(30))
		{
			var pos = getPositionFromIndex(level.random.nextInt(13 * 13));
			var fertilizer = fertilizerInventory.getItem(0);
			
			int fertilizeEnergyReq = 200;
			int fertilizeWaterReq = 100;
			
			var efluid = water.drain(fertilizeWaterReq, IFluidHandler.FluidAction.SIMULATE);
			
			if(algorithm != null && !fertilizer.isEmpty() && level instanceof ServerLevel server
					&& energy.getEnergyStored() >= fertilizeEnergyReq
					&& !efluid.isEmpty() && efluid.getAmount() == fertilizeWaterReq
					&& algorithm.tryFertilize(this, server, pos))
			{
				water.drain(fertilizeWaterReq, IFluidHandler.FluidAction.EXECUTE);
				energy.consumeEnergy(fertilizeEnergyReq);
				fertilizer.shrink(1);
			}
		}
		
		if(cooldown > 0)
		{
			if(energy.consumeEnergy(64))
				--cooldown;
			return;
		}
		
		if(algorithm == null || placeActions.size() + breakActions.size() + transformActions.size() > 10)
			return;
		
		if(isOnClient())
		{
			return;
		}
		
		var movePosition = true;
		
		var pos = getPositionFromIndex(positionIdx);
		if(hasPlatform(pos) && level instanceof ServerLevel server)
		{
			var res = algorithm.handleUpdate(this, server, pos);
			
			if(res.wait) cooldown += 10;
			movePosition = res.moveOn;
		}
		
		// Move to next position
		if(movePosition) positionIdx = (positionIdx + 1) % (13 * 13);
	}
	
	@Override
	public ContainerBaseMachine<TileFarm> openContainer(Player player, int windowId)
	{
		return new ContainerFarm(this, player, windowId);
	}
	
	public BlockPos getPositionFromIndex(int idx)
	{
		int x = (idx % 13) - 6; // [-6; 6]
		int z = (idx / 13) - 6; // [-6; 6]
		var algo = getAlgorithm();
		var y = -1;
		if(algo != null && algo.isUpsideDown()) y = 0;
		return worldPosition.offset(x, y, z);
	}
	
	@Override
	public CompoundTag writeNBT(CompoundTag nbt)
	{
		nbt = super.writeNBT(nbt);
		nbt.put("BreakQueue", CodecHelper.encodeList(BreakBlockAction.CODEC, breakActions));
		nbt.put("PlaceQueue", CodecHelper.encodeList(PlaceBlockAction.CODEC, placeActions));
		nbt.put("TransformQueue", CodecHelper.encodeList(TransformBlockAction.CODEC, transformActions));
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundTag nbt)
	{
		breakActions.clear();
		placeActions.clear();
		transformActions.clear();
		breakActions.addAll(CodecHelper.decodeList(BreakBlockAction.CODEC, nbt.get("BreakQueue")));
		placeActions.addAll(CodecHelper.decodeList(PlaceBlockAction.CODEC, nbt.get("PlaceQueue")));
		transformActions.addAll(CodecHelper.decodeList(TransformBlockAction.CODEC, nbt.get("TransformQueue")));
		super.readNBT(nbt);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(algorithmInventory);
	}
	
	public final WorldlyContainer inventory = new AutomatedContainer();
	private final LazyOptional<? extends IItemHandler>[] itemHandlers = SidedInvWrapper.create(inventory, Direction.values());
	private final LazyOptional<IFluidHandler> waterHandler = LazyOptional.of(() -> new MultiTankHandler(new IFluidTank[] { water }, new int[] { 0 }, new int[0]));
	private final LazyOptional<IEnergyStorage> energyStorageCap = LazyOptional.of(() -> energy);
	
	@Override
	public <T> LazyOptional<T> getCapability(BlockPos relativePos, Capability<T> cap, Direction side)
	{
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE) return energy.measurableCap.cast();
		
		if(cap == ForgeCapabilities.ITEM_HANDLER && TileMultiBlockPart.getPartState(level, relativePos).is(BlocksZT.FARM_ITEM_PORT))
			return itemHandlers[side == null ? 0 : side.ordinal()].cast();
		
		if(cap == ForgeCapabilities.FLUID_HANDLER && TileMultiBlockPart.getPartState(level, relativePos).is(BlocksZT.FARM_FLUID_PORT))
			return waterHandler.cast();
		
		if(cap == ForgeCapabilities.ENERGY && TileMultiBlockPart.getPartState(level, relativePos).is(BlocksZT.FARM_ENERGY_PORT))
			return energyStorageCap.cast();
		
		return LazyOptional.empty();
	}
	
	@Override
	public FarmAlgorithm getAlgorithm()
	{
		return algorithm;
	}
	
	@Override
	public Level getFarmLevel()
	{
		return level;
	}
	
	@Override
	public BlockPos getFarmPosition()
	{
		return worldPosition;
	}
	
	@Override
	public FluidTank getWaterInventory()
	{
		return water;
	}
	
	@Override
	public boolean hasPlatform(BlockPos platform)
	{
		return level.getBlockState(platform).is(BlocksZT.REINFORCED_PLANKS);
	}
	
	@Override
	public IItemHandlerModifiable getInventory(EnumFarmItemCategory category)
	{
		return switch(category)
				{
					case SOIL -> soilInventory;
					case PLANT -> plantInventory;
					case FERTILIZER -> fertilizerInventory;
					default -> null;
				};
	}
	
	@Override
	public void queueBlockPlacement(FarmItemConsumer itemConsumer, BlockPos pos, BlockState toPlace, int waterUsage, int priority)
	{
		placeActions.add(new PlaceBlockAction(itemConsumer, pos, toPlace, waterUsage, priority));
		placeActions.sort(PlaceBlockAction.COMPARATOR.reversed()); // Reverse to put the highest priority actions into lower indices.
	}
	
	@Override
	public void queueBlockHarvest(BlockPos pos, int priority)
	{
		breakActions.add(new BreakBlockAction(pos, priority));
		breakActions.sort(BreakBlockAction.COMPARATOR.reversed()); // Reverse to put the highest priority actions into lower indices.
	}
	
	@Override
	public void queueBlockTransformation(BlockPos pos, BlockState source, BlockState dest, int waterUsage, int priority)
	{
		transformActions.add(new TransformBlockAction(pos, source, dest, waterUsage, priority));
		transformActions.sort(TransformBlockAction.COMPARATOR.reversed()); // Reverse to put the highest priority actions into lower indices.
	}
	
	@Override
	public Direction getMultiblockDirection()
	{
		return getFront();
	}
	
	@Override
	public MultiBlockFormer<?> getFormer()
	{
		return BlockFarm.getFarmStructure();
	}
	
	@Override
	public boolean isMultiblockValid()
	{
		return isValid;
	}
	
	@Override
	public void queueMultiBlockValidityCheck()
	{
		checkNow = true;
	}
	
	protected int cachedLightValue, cachedLightTime;
	
	public int updateLighting(ToIntFunction<TileFarm> light)
	{
		if(ticksExisted - cachedLightTime > 2 || ticksExisted < cachedLightTime)
		{
			cachedLightValue = light.applyAsInt(this);
			cachedLightTime = ticksExisted;
		}
		return cachedLightValue;
	}
	
	@Override
	public AABB getRenderBoundingBox()
	{
		return new AABB(worldPosition.offset(-2, -2, -2), worldPosition.offset(2, 3, 2));
	}
	
	@Override
	public VoxelShape getShapeFor(BlockPos relativePos)
	{
		return fixedShapes.get(relativePos);
	}
	
	protected class AutomatedContainer
			implements WorldlyContainer
	{
		protected final Map<Integer, Tuple2<SimpleInventory, Integer>> slotMapping = new HashMap<>();
		
		public final int[] outputSlot = IntStream.range(0, resultInventory.getSlots()).toArray();
		public final int[] soilSlots = IntStream.range((outputSlot.length), (outputSlot.length) + soilInventory.getSlots()).toArray();
		public final int[] plantSlots = IntStream.range((outputSlot.length + soilSlots.length), (outputSlot.length + soilSlots.length) + plantInventory.getSlots()).toArray();
		
		public final int[] fertilizerSlots = IntStream.range((outputSlot.length + soilSlots.length + plantSlots.length), (outputSlot.length + soilSlots.length + plantSlots.length) + fertilizerInventory.getSlots()).toArray();
		
		public final int[] allSlots;
		
		public AutomatedContainer()
		{
			for(int i = 0; i < outputSlot.length; i++)
				slotMapping.put(outputSlot[i], Tuples.immutable(resultInventory, i));
			
			for(int i = 0; i < soilSlots.length; i++)
				slotMapping.put(soilSlots[i], Tuples.immutable(soilInventory, i));
			
			for(int i = 0; i < plantSlots.length; i++)
				slotMapping.put(plantSlots[i], Tuples.immutable(plantInventory, i));
			
			for(int i = 0; i < fertilizerSlots.length; i++)
				slotMapping.put(fertilizerSlots[i], Tuples.immutable(fertilizerInventory, i));
			
			allSlots = slotMapping.keySet().stream().mapToInt(Integer::intValue).toArray();
		}
		
		@Override
		public boolean canPlaceItem(int slot, ItemStack stack)
		{
			return Optional.ofNullable(slotMapping.get(slot))
					.map(t -> t.a().isItemValid(t.b(), stack))
					.orElse(false);
		}
		
		@Override
		public int[] getSlotsForFace(Direction dir)
		{
			return allSlots;
		}
		
		@Override
		public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir)
		{
			return canPlaceItem(slot, stack);
		}
		
		@Override
		public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir)
		{
			return Arrays.binarySearch(outputSlot, slot) >= 0;
		}
		
		@Override
		public int getContainerSize()
		{
			return allSlots.length;
		}
		
		@Override
		public boolean isEmpty()
		{
			return slotMapping.values().stream().allMatch(c -> c.a().getItem(c.b()).isEmpty());
		}
		
		@Override
		public ItemStack getItem(int slot)
		{
			return Optional.ofNullable(slotMapping.get(slot))
					.map(t -> t.a().getItem(t.b()))
					.orElse(ItemStack.EMPTY);
		}
		
		@Override
		public ItemStack removeItem(int slot, int count)
		{
			return Optional.ofNullable(slotMapping.get(slot))
					.map(t -> t.a().removeItem(t.b(), count))
					.orElse(ItemStack.EMPTY);
		}
		
		@Override
		public ItemStack removeItemNoUpdate(int slot)
		{
			return Optional.ofNullable(slotMapping.get(slot))
					.map(t -> t.a().removeItemNoUpdate(t.b()))
					.orElse(ItemStack.EMPTY);
		}
		
		@Override
		public void setItem(int slot, ItemStack stack)
		{
			Optional.ofNullable(slotMapping.get(slot))
					.ifPresent(t -> t.a().setItem(t.b(), stack));
		}
		
		@Override
		public void setChanged()
		{
			sync();
			slotMapping.values()
					.stream()
					.map(Tuple2::a)
					.distinct()
					.forEach(Container::setChanged);
		}
		
		@Override
		public boolean stillValid(Player player)
		{
			return !isRemoved() && isValid && player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 64D;
		}
		
		@Override
		public void clearContent()
		{
			slotMapping.values()
					.forEach(t -> t.a().setItem(t.b(), ItemStack.EMPTY));
		}
		
		@Override
		public int getMaxStackSize()
		{
			return 64;
		}
	}
}