package org.zeith.tech.core.net.properties;

import net.minecraft.network.FriendlyByteBuf;
import org.zeith.hammerlib.net.properties.PropertyBase;
import org.zeith.hammerlib.util.java.DirectStorage;

import java.util.Arrays;

public class PropertyIntArray
		extends PropertyBase<int[]>
{
	public PropertyIntArray(DirectStorage<int[]> value)
	{
		super(int[].class, value);
	}
	
	public PropertyIntArray()
	{
		super(int[].class, DirectStorage.allocate(new int[0]));
	}
	
	@Override
	protected boolean differ(int[] a, int[] b)
	{
		return !Arrays.equals(a, b);
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		int[] value = this.value.get();
		buf.writeInt(value.length);
		for(var v : value) buf.writeInt(v);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		int[] value = new int[buf.readInt()];
		for(var i = 0; i < value.length; ++i) value[i] = buf.readInt();
		this.value.set(value);
	}
}
