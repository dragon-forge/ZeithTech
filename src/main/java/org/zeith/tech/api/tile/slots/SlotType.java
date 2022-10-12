package org.zeith.tech.api.tile.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.misc.AuxiliaryPortDefinition;

import java.util.*;
import java.util.stream.Stream;

public final class SlotType<T>
{
	private static final Map<Class<?>, SlotType<?>> TYPE_MAP = new HashMap<>();
	
	public static final SlotType<ItemStack> ITEM = get(ItemStack.class, AuxiliaryPortDefinition.ITEM_DEFINITION);
	public static final SlotType<FluidStack> FLUID = get(FluidStack.class, AuxiliaryPortDefinition.FLUID_DEFINITION);
	public static final SlotType<FECharge> ENERGY = get(FECharge.class, AuxiliaryPortDefinition.ENERGY_DEFINITION);
	
	private final Class<T> type;
	
	private final AuxiliaryPortDefinition textures;
	
	private SlotType(Class<T> type, AuxiliaryPortDefinition textures)
	{
		this.type = Objects.requireNonNull(type);
		this.textures = Objects.requireNonNull(textures);
	}
	
	public AuxiliaryPortDefinition getTextures()
	{
		return textures;
	}
	
	public static synchronized <T> SlotType<T> get(Class<T> type, AuxiliaryPortDefinition textures)
	{
		return Cast.cast(TYPE_MAP.computeIfAbsent(type, t -> new SlotType<>(t, textures)));
	}
	
	public static Stream<SlotType<?>> types()
	{
		return TYPE_MAP.values().stream();
	}
	
	public Optional<ISlotAccess<T>> cast(ISlotAccess<?> access)
	{
		return access == null || !access.getType().equals(this) ? Optional.empty() : Optional.ofNullable(Cast.cast(access));
	}
	
	public Class<T> getType()
	{
		return type;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		var that = (SlotType<?>) obj;
		return Objects.equals(this.type, that.type);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(type);
	}
	
	@Override
	public String toString()
	{
		return "SlotType[" +
				"type=" + type + ']';
	}
}