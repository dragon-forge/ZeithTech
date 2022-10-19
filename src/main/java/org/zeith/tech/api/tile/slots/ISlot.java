package org.zeith.tech.api.tile.slots;

import org.zeith.tech.api.misc.IColorProvider;
import org.zeith.tech.api.misc.SampleColorGenerator;
import org.zeith.tech.utils.MathHelper;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;

public interface ISlot<T>
		extends IColorProvider
{
	UUID getUniqueIdentifier();
	
	SlotType<T> getType();
	
	SlotRole getRole();
	
	<V> Optional<ISlotAccess<V>> getSlotAccess(SlotType<V> byType);
	
	static <T> ISlot<T> simpleSlot(ISlotAccess<T> access, SlotRole role, String... seedInfo)
	{
		final var seeded = SampleColorGenerator.generateRandom(SampleColorGenerator.getCaller(), seedInfo);
		int rgb = MathHelper.hsvToRgb(seeded.nextFloat(), 1F, 1F);
		
		return simpleSlot(new UUID(seeded.nextLong(), seeded.nextLong()), access, role, access.getColorOverride().orElseGet(() -> new Color(rgb, false)));
	}
	
	static <T> ISlot<T> simpleSlot(UUID uuid, ISlotAccess<T> access, SlotRole role, Color color)
	{
		return new SimpleSlot<>(uuid, access, role, color);
	}
	
	class SimpleSlot<T>
			implements ISlot<T>
	{
		final UUID uuid;
		final ISlotAccess<T> access;
		final SlotType<T> type;
		final SlotRole role;
		final Color color;
		
		public SimpleSlot(UUID uuid, ISlotAccess<T> access, SlotRole role, Color color)
		{
			this.uuid = uuid;
			this.access = access;
			this.type = access.getType();
			this.role = role;
			this.color = color;
		}
		
		@Override
		public Color getColor()
		{
			return color;
		}
		
		@Override
		public UUID getUniqueIdentifier()
		{
			return uuid;
		}
		
		@Override
		public SlotType<T> getType()
		{
			return type;
		}
		
		@Override
		public SlotRole getRole()
		{
			return role;
		}
		
		@Override
		public <V> Optional<ISlotAccess<V>> getSlotAccess(SlotType<V> byType)
		{
			return byType.cast(access);
		}
	}
}