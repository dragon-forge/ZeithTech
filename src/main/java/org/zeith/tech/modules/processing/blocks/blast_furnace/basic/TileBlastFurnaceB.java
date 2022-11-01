package org.zeith.tech.modules.processing.blocks.blast_furnace.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyBool;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.block.multiblock.base.MultiBlockFormer;
import org.zeith.tech.api.block.multiblock.blast_furnace.IBlastFurnaceCasingBlock;
import org.zeith.tech.api.enums.SideConfig;
import org.zeith.tech.api.enums.SidedConfigTyped;
import org.zeith.tech.api.recipes.processing.RecipeBlastFurnace;
import org.zeith.tech.api.tile.IFluidPipe;
import org.zeith.tech.api.tile.multiblock.IMultiblockTile;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.utils.InventoryHelper;
import org.zeith.tech.core.fluid.MultiTankHandler;
import org.zeith.tech.core.net.PacketAddDestroyBlockEffect;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.*;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;
import org.zeith.tech.modules.shared.init.RecipeRegistriesZT;
import org.zeith.tech.utils.SerializableFluidTank;
import org.zeith.tech.utils.SidedInventory;
import org.zeith.tech.utils.fluid.FluidSmoothing;

import java.util.EnumSet;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class TileBlastFurnaceB
		extends TileBaseMachine<TileBlastFurnaceB>
		implements IMultiblockTile
{
	@NBTSerializable("Fluids")
	public final SerializableFluidTank fuel = new SerializableFluidTank(8000, f -> FluidsZT_Processing.DIESEL_FUEL.is(f.getFluid()));
	
	public final FluidSmoothing tankSmooth;
	
	@NBTSerializable("Items")
	public final SidedInventory inventory = new SidedInventory(3, new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ITEM))
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.NONE)
			.createItemAccess(new int[] {
					0,
					1
			}, new int[] { 2 }));
	
	@NBTSerializable("IsValid")
	public boolean isValid = true;
	
	public boolean checkNow = false;
	
	@NBTSerializable("BurnDuration")
	public int maxBurnTime = 1000;
	
	@NBTSerializable("BurnTime")
	public float burnTime;
	
	@NBTSerializable("Progress")
	public float _progress;
	
	@NBTSerializable("MaxProgress")
	public int _maxProgress = 200;
	
	@NBTSerializable("Temperature")
	public float temperature;
	
	@NBTSerializable("BiomeTemp")
	public float biomeTemperature;
	
	@NBTSerializable("HeatMult")
	public float heatingMultiplier;
	
	@NBTSerializable("HeatSaveMult")
	public float heatSaveMultiplier;
	
	@NBTSerializable("AppliedBiomeTemp")
	public boolean hasAppliedBiome;
	
	protected int maxTemperature = 4000;
	
	public final PropertyBool isValidSynced = new PropertyBool(DirectStorage.create(v -> isValid = v, () -> isValid));
	
	public TileBlastFurnaceB(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.BASIC_BLAST_FURNACE, pos, state);
		
		this.tankSmooth = new FluidSmoothing("display", this);
		dispatcher.registerProperty("formed", isValidSynced);
		
		inventory.isStackValid = (slot, stack) -> (
				slot == 0
						&& RecipeRegistriesZT_Processing.BLAST_FURNACE.getRecipes()
						.stream()
						.filter(this::canPerform)
						.anyMatch(r -> r.getInputA().test(stack))
		) || (
				slot == 1
						&& RecipeRegistriesZT_Processing.BLAST_FURNACE.getRecipes()
						.stream()
						.filter(this::canPerform)
						.anyMatch(r -> r.getInputA().test(inventory.getItem(0)) && r.getInputB().test(stack))
		);
	}
	
	@Override
	public void update()
	{
		tankSmooth.update(fuel.getFluid());
		
		biomeTemperature = getBiomeTemperature();
		
		if(!hasAppliedBiome)
		{
			temperature = Math.max(biomeTemperature, temperature);
			hasAppliedBiome = true;
		}
		
		if((checkNow || atTickRate(40)) && isOnServer() && level != null)
		{
			var former = getFormer();
			var dir = getFront();
			
			var v = isValid;
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
				heatingMultiplier = 1;
				heatSaveMultiplier = 1;
				
				var positions = former.getPositionsFrom(worldPosition, dir);
				
				for(var pos : positions)
				{
					var state = TileMultiBlockPart.getPartState(level, pos);
					if(state.getBlock() instanceof IBlastFurnaceCasingBlock casing)
					{
						heatingMultiplier *= 1F - Mth.clamp(casing.getTemperatureLoss(level, pos, state), 0F, 1F);
						heatSaveMultiplier *= Mth.clamp(casing.getTemperatureReflectivityCoef(level, pos, state), 0F, 1F);
					}
				}
				
				sync();
			}
			
			checkNow = false;
		}
		
		if(isOnServer() && temperature > biomeTemperature && atTickRate(2))
			temperature = Math.max(biomeTemperature, temperature - (1F - heatSaveMultiplier) * 0.125F);
		
		if(!isValid || level == null)
			return;
		
		if(isOnClient())
		{
			if(isEnabled())
			{
				var rand = level.getRandom();
				
				Vec3 topPos = Vec3.atLowerCornerOf(worldPosition).add(0.5, 2, 0.5);
				level.addParticle(ParticleTypes.LARGE_SMOKE,
						topPos.x + rand.nextGaussian() * 0.2D, topPos.y, topPos.z + rand.nextGaussian() * 0.2D,
						rand.nextGaussian() * 0.01D, rand.nextDouble() * 0.05D, rand.nextGaussian() * 0.01D);
			}
			
			return;
		}
		
		int needInput = fuel.getCapacity() - fuel.getFluidAmount();
		if(needInput > 0 && atTickRate(2))
		{
			if(level.getBlockEntity(worldPosition.relative(Direction.DOWN, 2)) instanceof IFluidPipe pipe)
			{
				if(atTickRate(100))
					pipe.createVacuum(fuel.isEmpty() ? FluidIngredient.join(
							FluidsZT_Processing.DIESEL_FUEL.ingredient()
					) : FluidIngredient.ofFluids(List.of(fuel.getFluid())), 105);
				var in = pipe.extractFluidFromPipe(needInput, IFluidHandler.FluidAction.SIMULATE);
				if(fuel.isFluidValid(in))
				{
					var store = fuel.fill(in, IFluidHandler.FluidAction.EXECUTE);
					pipe.extractFluidFromPipe(store, IFluidHandler.FluidAction.EXECUTE);
				}
			}
		}
		
		if(burnTime > 0)
		{
			float add = 0.75F * heatingMultiplier;
			
			if(temperature + add <= maxTemperature)
			{
				--burnTime;
				temperature += add;
			} else burnTime -= 0.025F;
		}
		
		setEnabledState(burnTime > 0);
		
		if(burnTime <= 0 && fuel.getFluidAmount() > 50)
		{
			fuel.drain(50, IFluidHandler.FluidAction.EXECUTE);
			burnTime = maxBurnTime;
		}
		
		var recipe = getActiveRecipe();
		
		if(recipe != null)
		{
			_maxProgress = recipe.getCraftTime();
			
			if(_progress < _maxProgress && isOnServer()
					&& (_progress > 0 || storeRecipeResult(recipe, true)))
			{
				float minTemp = recipe.getNeededTemperature();
				
				if(temperature >= minTemp)
				{
					float mul = 1F + (temperature - minTemp) / 256F;
					
					_progress += 1 * mul;
					temperature -= 0.525F * mul;
					
					isInterrupted.setBool(false);
				} else
				{
					isInterrupted.setBool(true);
					_progress = Math.max(0, _progress - 2);
				}
			}
			
			if(isOnServer() && _progress >= _maxProgress && storeRecipeResult(recipe, false))
			{
				if(!recipe.getInputA().isEmpty()) inventory.getItem(0).shrink(1);
				if(!recipe.getInputB().isEmpty()) inventory.getItem(1).shrink(1);
				
				var former = getFormer();
				var dir = getFront();
				if(former != null)
				{
					var positions = former.getPositionsFrom(worldPosition, dir);
					if(!positions.isEmpty() && level instanceof ServerLevel srv)
					{
						var pos = positions.get(level.getRandom().nextInt(positions.size()));
						var state = TileMultiBlockPart.getPartState(level, pos);
						if(state.getBlock() instanceof IBlastFurnaceCasingBlock casing)
						{
							var cracked = casing.crackRandomly(srv, pos, state, level.getRandom());
							
							if(!cracked.equals(state))
							{
								if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part) part.setSubState(cracked);
								else level.setBlockAndUpdate(pos, cracked);
								
								PacketAddDestroyBlockEffect.spawn(level, pos, state);
								ZeithTechAPI.get().getAudioSystem().playTileSound(this, casing.getCasingDamageSound(), 0.5F, 1F);
							}
						}
					}
				}
				
				_progress = 0;
				
				sync();
			}
		}
	}
	
	private RecipeBlastFurnace cachedRecipe;
	private int lastCacheTime;
	
	public RecipeBlastFurnace getActiveRecipe()
	{
		if(cachedRecipe != null && !canPerform(cachedRecipe))
		{
			cachedRecipe = null;
		}
		
		if(ticksExisted > lastCacheTime + 5 || ticksExisted < lastCacheTime)
		{
			lastCacheTime = ticksExisted;
			cachedRecipe = RecipeRegistriesZT.BLAST_FURNACE.getRecipes()
					.stream()
					.filter(this::canPerform)
					.filter(this::matches)
					.findFirst()
					.orElse(null);
		}
		
		return cachedRecipe;
	}
	
	private boolean canPerform(RecipeBlastFurnace recipe)
	{
		return recipe.getTier().ordinal() < 1;
	}
	
	private boolean matches(RecipeBlastFurnace recipe)
	{
		return recipe.getInputA().test(inventory.getItem(0))
				&& (recipe.getInputB().isEmpty() || recipe.getInputB().test(inventory.getItem(1)))
				&& recipe.getNeededTemperature() <= maxTemperature;
	}
	
	
	protected boolean storeRecipeResult(RecipeBlastFurnace recipe, boolean simulate)
	{
		return store(recipe.assemble(), simulate);
	}
	
	public boolean store(ItemStack stacks, boolean simulate)
	{
		return InventoryHelper.storeStack(inventory, IntStream.of(2), stacks, simulate);
	}
	
	public int getBiomeTemperature()
	{
		return level != null ? Math.round(31.25F * level.getBiome(worldPosition).get().getBaseTemperature()) : 25;
	}
	
	protected int cachedLightValue, cachedLightTime;
	
	public int updateLighting(ToIntFunction<TileBlastFurnaceB> light)
	{
		if(ticksExisted - cachedLightTime > 2 || ticksExisted < cachedLightTime)
		{
			cachedLightValue = light.applyAsInt(this);
			cachedLightTime = ticksExisted;
		}
		return cachedLightValue;
	}
	
	@Override
	public ContainerBaseMachine<TileBlastFurnaceB> openContainer(Player player, int windowId)
	{
		return new ContainerBlastFurnaceB(this, player, windowId);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(inventory);
	}
	
	private final LazyOptional<? extends IItemHandler>[] itemHandlers = SidedInvWrapper.create(inventory, Direction.values());
	private final LazyOptional<IFluidHandler> dieselHandler = LazyOptional.of(() -> new MultiTankHandler(new IFluidTank[] { fuel }, new int[] { 0 }, new int[0]));
	
	@Override
	public <T> LazyOptional<T> getCapability(BlockPos relativePos, Capability<T> capability, Direction side)
	{
		if(worldPosition.above().equals(relativePos))
			return LazyOptional.empty();
		
		if(worldPosition.below().equals(relativePos) && side == Direction.DOWN && capability == ForgeCapabilities.FLUID_HANDLER)
			return dieselHandler.cast();
		
		if(capability == ForgeCapabilities.ITEM_HANDLER)
			return itemHandlers[side == null ? 0 : side.ordinal()].cast();
		
		return LazyOptional.empty();
	}
	
	@Override
	public Direction getMultiblockDirection()
	{
		return getFront();
	}
	
	@Override
	public MultiBlockFormer<?> getFormer()
	{
		return BlockBlastFurnaceB.getBasicBlastFurnaceStructure();
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
	
	@Override
	public AABB getRenderBoundingBox()
	{
		return new AABB(worldPosition.offset(-2, -2, -2), worldPosition.offset(2, 2, 2));
	}
}