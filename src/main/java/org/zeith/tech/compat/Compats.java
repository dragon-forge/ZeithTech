package org.zeith.tech.compat;

import net.minecraftforge.fml.ModList;
import org.zeith.tech.compat.ae2.AE2Compat;

import java.util.*;
import java.util.function.Supplier;

public class Compats
{
	private static final Map<String, Supplier<Supplier<? extends BaseCompat>>> COMPAT_MAP = new HashMap<>();
	
	static
	{
		COMPAT_MAP.put("ae2", () -> AE2Compat::new);
	}
	
	public static List<? extends BaseCompat> gatherAll()
	{
		return COMPAT_MAP.entrySet()
				.stream()
				.filter(m -> ModList.get().isLoaded(m.getKey()))
				.map(m -> m.getValue().get().get())
				.toList();
	}
}