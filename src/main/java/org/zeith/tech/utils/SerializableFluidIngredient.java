package org.zeith.tech.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;

public class SerializableFluidIngredient
		implements INBTSerializable<CompoundTag>
{
	public FluidIngredient ingredient = FluidIngredient.EMPTY;
	
	@Override
	public CompoundTag serializeNBT()
	{
		return (CompoundTag) NbtOps.INSTANCE.withEncoder(FluidIngredient.CODEC).apply(ingredient).result().orElseThrow();
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		ingredient = NbtOps.INSTANCE.withDecoder(FluidIngredient.CODEC).apply(nbt).result().orElseThrow().getFirst();
	}
	
	public boolean isEmpty()
	{
		return ingredient.isEmpty();
	}
	
	public void empty()
	{
		ingredient = FluidIngredient.EMPTY;
	}
}