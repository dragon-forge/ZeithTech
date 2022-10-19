package org.zeith.tech.modules.generators.init;

import net.minecraft.advancements.CriteriaTriggers;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.advancements.AdvancementTrigger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public interface AdvancementTriggersZT
{
	AdvancementTrigger MAKE_PLATE = new AdvancementTrigger("make_plate");
	AdvancementTrigger FIND_HEVEA = new AdvancementTrigger("find_hevea");
	
	static void setup()
	{
		int reg = 0;
		
		for(Field field : AdvancementTriggersZT.class.getFields())
		{
			if(AdvancementTrigger.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers()))
			{
				field.setAccessible(true);
				try
				{
					CriteriaTriggers.register(AdvancementTrigger.class.cast(field.get(null)));
					++reg;
				} catch(IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		
		ZeithTech.LOG.info("Registered " + reg + " advancement triggers.");
	}
}