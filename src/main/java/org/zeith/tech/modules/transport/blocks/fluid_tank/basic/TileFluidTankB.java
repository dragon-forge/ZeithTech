package org.zeith.tech.modules.transport.blocks.fluid_tank.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyBool;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.block.ZeithTechStateProperties;
import org.zeith.tech.api.tile.IFluidTankTile;
import org.zeith.tech.api.tile.ILoadableFromItem;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;
import org.zeith.tech.utils.SerializableFluidTank;
import org.zeith.tech.utils.fluid.FluidHelperZT;
import org.zeith.tech.utils.fluid.FluidSmoothing;

import java.util.List;
import java.util.Objects;

public class TileFluidTankB
		extends TileBaseMachine<TileFluidTankB>
		implements ILoadableFromItem, IFluidTankTile
{
	@NBTSerializable("TankContents")
	public final SerializableFluidTank storage = createTank();
	
	public final FluidSmoothing tankSmooth;
	
	@NBTSerializable("Items")
	public final SimpleInventory inventory = new SimpleInventory(2);
	
	@NBTSerializable("FillItem")
	public boolean fillItem = true;
	
	public final PropertyBool fillItemProp = new PropertyBool(DirectStorage.create(v -> fillItem = v, () -> fillItem));
	
	public TileFluidTankB(BlockPos pos, BlockState state)
	{
		super(TilesZT_Transport.BASIC_FLUID_TANK, pos, state);
		
		this.tankSmooth = new FluidSmoothing("display", this);
		this.dispatcher.registerProperty("fill_mode", fillItemProp);
		this.inventory.isStackValid = (idx, stack) -> idx == 0 && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
	}
	
	protected SerializableFluidTank createTank()
	{
		return new SerializableFluidTank(64 * FluidType.BUCKET_VOLUME);
	}
	
	private int lastServerFluidAmount;
	
	@Override
	public void update()
	{
		tankSmooth.update(storage.getFluid());
		
		if(isOnServer() && atTickRate(5) && lastServerFluidAmount != storage.getFluidAmount())
		{
			lastServerFluidAmount = storage.getFluidAmount();
			sync();
		}
		
		if(isOnServer())
		{
			var fluid = storage.getFluid();
			if(!fluid.isEmpty())
				setLightLevel(Math.round(fluid.getAmount() * fluid.getFluid().getFluidType().getLightLevel(fluid) / (float) storage.getCapacity()));
			else
				setLightLevel(0);
			
			if(!storage.isEmpty())
			{
				var below = level.getBlockEntity(worldPosition.below());
				if(below instanceof TileFluidTankB tank)
					FluidUtil.tryFluidTransfer(tank.storage, storage, storage.getFluidAmount(), true);
			}
			
			var input = inventory.getItem(0);
			
			if(!input.isEmpty() && input.getCount() == 1)
			{
				var result = fillItem
						? FluidUtil.tryFillContainer(input, storage, 1000, null, true)
						: FluidUtil.tryEmptyContainer(input, outputFluidHandler.resolve().orElseThrow(), 1000, null, true);
				
				if(result.isSuccess())
					inventory.setItem(0, result.getResult());
				else if(inventory.getItem(1).isEmpty())
				{
					if((fillItem && FluidHelperZT.isFluidContainerFull(input)) || (!fillItem && FluidHelperZT.isFluidContainerEmpty(input)))
					{
						inventory.setItem(1, inventory.getItem(0));
						inventory.setItem(0, ItemStack.EMPTY);
					}
				}
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
	
	@Override
	public boolean isEnabled()
	{
		return false;
	}
	
	@Override
	public void setEnabledState(boolean enabled)
	{
	}
	
	@Override
	public ContainerBaseMachine<TileFluidTankB> openContainer(Player player, int windowId)
	{
		return new ContainerFluidTankB(this, player, windowId);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(inventory);
	}
	
	public ItemStack generateItem()
	{
		return generateItem(getBlockState().getBlock());
	}
	
	public ItemStack generateItem(ItemLike item)
	{
		ItemStack drop = new ItemStack(item);
		saveToItem(drop);
		return drop;
	}
	
	@Override
	public void saveToItem(ItemStack stack)
	{
		if(storage.isEmpty())
			stack.removeTagKey("TankContents");
		else
			stack.addTagElement("TankContents", storage.serializeNBT());
	}
	
	@Override
	public void loadFromItem(ItemStack stack)
	{
		if(stack.hasTag() && stack.getTag().contains("TankContents"))
			storage.deserializeNBT(stack.getTag().getCompound("TankContents"));
		else
			storage.setFluid(FluidStack.EMPTY);
	}
	
	@Override
	public int storeFluid(FluidStack resource, IFluidHandler.FluidAction action)
	{
		if(storage.isEmpty() || storage.getFluid().isFluidEqual(resource))
			return outputFluidHandler.map(fh -> fh.fill(resource, action)).orElse(0);
		return 0;
	}
	
	private final LazyOptional<IFluidHandler> outputFluidHandler = LazyOptional.of(() -> new FluidTankHandler(storage, (fluid, action) ->
	{
		if(hasLevel() && getLevel().getBlockEntity(worldPosition.above()) instanceof IFluidTankTile tank)
			return tank.storeFluid(fluid, action);
		return 0;
	}));
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.FLUID_HANDLER)
			return outputFluidHandler.cast();
		return super.getCapability(cap, side);
	}
}