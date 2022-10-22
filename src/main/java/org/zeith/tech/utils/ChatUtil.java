package org.zeith.tech.utils;

import net.minecraft.network.chat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ChatUtil
{
	public static UnaryOperator<Style> setColor(TextColor color)
	{
		return style -> style.withColor(color);
	}
	
	public static void prepend(Consumer<List<Component>> adder, List<Component> origin, Component prefix)
	{
		List<Component> tmp = new ArrayList<>();
		adder.accept(tmp);
		for(Component com : tmp)
			origin.add(prefix.copy()
					.append((com instanceof MutableComponent mut ? mut : com.copy())
							.withStyle(prefix.getStyle())
					));
	}
}