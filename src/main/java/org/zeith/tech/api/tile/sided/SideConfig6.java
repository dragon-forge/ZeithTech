package org.zeith.tech.api.tile.sided;

import net.minecraft.nbt.ShortTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.tech.api.enums.SideConfig;

import java.util.Arrays;
import java.util.Objects;

public class SideConfig6
		implements INBTSerializable<ShortTag>
{
	public final SideConfig[] configurations = new SideConfig[6];
	
	public SideConfig6()
	{
		this(SideConfig.NONE);
	}
	
	public SideConfig6(SideConfig def)
	{
		Arrays.fill(configurations, def);
	}
	
	public SideConfig6 setDefaults(SideConfig config)
	{
		Arrays.fill(configurations, Objects.requireNonNull(config));
		return this;
	}
	
	public SideConfig get(int idx)
	{
		return configurations[idx];
	}
	
	public SideConfig6 set(int idx, SideConfig cfg)
	{
		configurations[idx] = Objects.requireNonNull(cfg);
		return this;
	}
	
	// This magic below does a bit-wise shifts to fit 6*2 (we have 4 values for SideConfig) bits into a 16-bit short.
	// Goes both ways (read/write). Works like a charm!
	
	@Override
	public ShortTag serializeNBT()
	{
		return ShortTag.valueOf((short)
				(configurations[0].ordinal() << 10 |
						(configurations[1].ordinal() << 8) |
						(configurations[2].ordinal() << 6) |
						(configurations[3].ordinal() << 4) |
						(configurations[4].ordinal() << 2) |
						configurations[5].ordinal()
				)
		);
	}
	
	@Override
	public void deserializeNBT(ShortTag nbt)
	{
		var value = nbt.getAsShort();
		for(int i = 0; i < 6; ++i)
			configurations[i] = SideConfig.byId((value >> (10 - i * 2)) & 3);
	}
}