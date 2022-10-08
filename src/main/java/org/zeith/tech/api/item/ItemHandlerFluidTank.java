package org.zeith.tech.api.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.util.java.DirectStorage;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Simple add-on for making any item contain a fluid, this class easily overrides getFluid and setFluid to make for a lambda-based read-writing into the item.
 */
public class ItemHandlerFluidTank
		extends FluidHandlerItemStack
{
	private final DirectStorage<FluidStack> fluidAccess;
	
	public ItemHandlerFluidTank(@NotNull ItemStack container, int capacity, Function<ItemStack, FluidStack> getFluid, BiConsumer<ItemStack, FluidStack> setFluid)
	{
		super(container, capacity);
		this.fluidAccess = DirectStorage.create(stack -> setFluid.accept(getContainer(), stack), () -> getFluid.apply(getContainer()));
	}
	
	@Override
	public @NotNull FluidStack getFluid()
	{
		return fluidAccess.get();
	}
	
	@Override
	protected void setFluid(FluidStack fluid)
	{
		fluidAccess.set(fluid);
	}
	
	@Override
	protected void setContainerToEmpty()
	{
		setFluid(FluidStack.EMPTY);
	}
}