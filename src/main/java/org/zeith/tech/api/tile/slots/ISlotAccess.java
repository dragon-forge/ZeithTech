package org.zeith.tech.api.tile.slots;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

public interface ISlotAccess<T>
{
	default Optional<Color> getColorOverride()
	{
		return Optional.empty();
	}
	
	SlotType<T> getType();
	
	T get();
	
	void set(T val);
	
	/**
	 * @return The amount of T that was (or would have been, if simulated) inserted into the slot.
	 */
	int insert(T val, boolean simulate);
	
	/**
	 * @return An instance of T that was (or would have been, if simulated) extracted from the slot.
	 */
	T extract(int amount, boolean simulate);
	
	int getAmount();
	
	int getMaxAmount();
	
	default @NotNull <CAP> LazyOptional<CAP> getCapability(@NotNull Capability<CAP> cap)
	{
		return LazyOptional.empty();
	}
	
	default boolean belongsTo(Object owner)
	{
		return false;
	}
}